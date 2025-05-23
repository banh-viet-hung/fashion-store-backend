package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderStatusDetailRepository orderStatusDetailRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private FeedbackService feedbackService;

    public Long generateOrderId() {
        return System.currentTimeMillis() + (long) (Math.random() * 100000);
    }

    public Long createOrder(OrderCreateDto orderCreateDto, String username) {
        User user = null;
        if (username != null) {
            user = userRepository.findByEmail(username);
        }

        Optional<Address> optionalAddress = addressRepository.findByUserAndDefaultAddress(user,
                orderCreateDto.getAddress().isDefaultAddress());

        // Nếu địa chỉ không tồn tại, tạo mới địa chỉ
        Address address = optionalAddress.orElseGet(() -> {
            Address newAddress = new Address();
            newAddress.setFullName(orderCreateDto.getAddress().getFullName());
            newAddress.setPhoneNumber(orderCreateDto.getAddress().getPhoneNumber());
            newAddress.setAddress(orderCreateDto.getAddress().getAddress());
            newAddress.setCity(orderCreateDto.getAddress().getCity());
            newAddress.setDistrict(orderCreateDto.getAddress().getDistrict());
            newAddress.setWard(orderCreateDto.getAddress().getWard());
            newAddress.setDefaultAddress(orderCreateDto.getAddress().isDefaultAddress());
            addressRepository.save(newAddress);
            return newAddress;
        });

        // Lấy phương thức vận chuyển
        ShippingMethod shippingMethod = shippingMethodRepository.findByCode(orderCreateDto.getShipping().getCode())
                .orElseThrow(() -> new RuntimeException("Phương thức vận chuyển không hợp lệ"));

        // Lấy phương thức thanh toán
        PaymentMethod paymentMethod = paymentMethodRepository.findByCode(orderCreateDto.getPayment())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));

        // Tạo đơn hàng trước khi thêm OrderDetail vào
        Order order = new Order();
        order.setId(generateOrderId());
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(address);
        order.setUser(user);
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);
        order.setTotal(0); // Tạm thời đặt tổng là 0
        order.setOrderDetails(new ArrayList<>()); // Khởi tạo danh sách OrderDetails

        // Danh sách lưu trữ các sản phẩm và biến động số lượng
        List<CartProductDTO> cart = orderCreateDto.getCart();
        List<ProductVariant> productVariantsToUpdate = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        // Kiểm tra số lượng sản phẩm trong giỏ hàng và tính tổng tiền
        double total = 0;
        List<OrderDetail> orderDetails = order.getOrderDetails();
        for (CartProductDTO cartItem : cart) {
            // Lấy color và size của từng sản phẩm trong giỏ hàng
            Color color = null;
            Size size = null;

            if (cartItem.getColor() != null && !cartItem.getColor().isEmpty()) {
                color = colorRepository.findByName(cartItem.getColor());
            }

            if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
                size = sizeRepository.findByName(cartItem.getSize());
            }

            ProductVariant productVariant = productVariantRepository
                    .findByProductIdAndColorAndSize(cartItem.getProductId(), color, size);

            if (productVariant == null) {
                throw new RuntimeException("Sản phẩm không tồn tại");
            }

            if (productVariant.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Có lỗi xảy ra liên quan đến số lượng sản phẩm, vui lòng kiểm tra lại tại trang giỏ hàng");
            }

            // Lưu các sản phẩm và biến động số lượng để cập nhật sau
            productVariantsToUpdate.add(productVariant);

            // Giảm tổng số lượng của sản phẩm trong giỏ hàng
            Product product = productVariant.getProduct();
            productsToUpdate.add(product);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(productVariant.getProduct());
            orderDetail.setSize(cartItem.getSize());
            orderDetail.setColor(cartItem.getColor());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(productVariant.getProduct().getSalePrice() != 0
                    ? productVariant.getProduct().getSalePrice() * cartItem.getQuantity()
                    : productVariant.getProduct().getPrice() * cartItem.getQuantity());
            orderDetail.setOrder(order); // Thiết lập order cho orderDetail
            orderDetails.add(orderDetail);

            total += productVariant.getProduct().getSalePrice() != 0
                    ? (productVariant.getProduct().getSalePrice() * cartItem.getQuantity())
                    : (productVariant.getProduct().getPrice() * cartItem.getQuantity());
        }

        // Tính phí vận chuyển
        total += shippingMethod.getFee();

        // Xử lý mã giảm giá nếu có
        double discount = 0;
        if (orderCreateDto.getCouponCode() != null && !orderCreateDto.getCouponCode().isEmpty()) {
            try {
                // Sử dụng couponService để xác thực và lấy giá trị giảm giá
                discount = couponService.validateCoupon(orderCreateDto.getCouponCode(), total);

                // Cập nhật discount và couponCode cho order
                order.setDiscount(discount);
                order.setCouponCode(orderCreateDto.getCouponCode());

                // Giảm tổng tiền sau khi áp dụng mã giảm giá
                total -= discount;
            } catch (IllegalArgumentException e) {
                // Nếu mã giảm giá không hợp lệ, ném lỗi
                throw new RuntimeException(e.getMessage());
            }
        } else {
            order.setDiscount(0.0); // Không có mã giảm giá
            order.setCouponCode(null); // Không có mã giảm giá
        }

        // Cập nhật lại tổng tiền của đơn hàng
        order.setTotal(total);

        // Kiểm tra và khởi tạo danh sách orderStatusDetails nếu cần
        if (order.getOrderStatusDetails() == null) {
            order.setOrderStatusDetails(new ArrayList<>()); // Khởi tạo danh sách nếu nó null
        }

        // Lưu đơn hàng
        orderRepository.save(order);

        // Tạo trạng thái đơn hàng mặc định
        String initialStatus = orderCreateDto.getPayment().equals("COD") ? "PENDING" : "WAITING_FOR_PAYMENT"; // Kiểm
                                                                                                              // tra
                                                                                                              // phương
                                                                                                              // thức
                                                                                                              // thanh
                                                                                                              // toán
        OrderStatusDetail orderStatusDetail = new OrderStatusDetail();
        orderStatusDetail.setOrder(order);
        orderStatusDetail.setOrderStatus(orderStatusRepository.findById(initialStatus)
                .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng không hợp lệ")));
        orderStatusDetail.setUpdateAt(LocalDateTime.now());
        orderStatusDetail.setUser(user);
        orderStatusDetail.setActive(true);

        // Thêm trạng thái đơn hàng vào danh sách
        order.getOrderStatusDetails().add(orderStatusDetail);

        // Lưu OrderStatusDetail vào bảng OrderStatusDetail
        orderStatusDetailRepository.save(orderStatusDetail);

        // Sau khi đơn hàng đã được lưu, cập nhật số lượng sản phẩm
        for (ProductVariant productVariant : productVariantsToUpdate) {
            productVariant.setQuantity(productVariant.getQuantity()
                    - cart.stream().filter(item -> item.getProductId().equals(productVariant.getProduct().getId()))
                            .mapToInt(CartProductDTO::getQuantity).sum());
            productVariantRepository.save(productVariant);
        }

        for (Product product : productsToUpdate) {
            product.setQuantity(
                    product.getQuantity() - cart.stream().filter(item -> item.getProductId().equals(product.getId()))
                            .mapToInt(CartProductDTO::getQuantity).sum());
            productRepository.save(product);
        }

        // Tăng số lượt sử dụng của mã giảm giá nếu đã áp dụng thành công
        if (orderCreateDto.getCouponCode() != null && !orderCreateDto.getCouponCode().isEmpty()) {
            couponService.incrementCouponUsage(orderCreateDto.getCouponCode());
        }

        return order.getId();
    }

    public List<OrderResponseDto> getOrdersByUsername(String username) {
        // Lấy danh sách đơn hàng của người dùng từ repository
        List<Order> orders = orderRepository.findByUser_Email(username); // Giả sử đã có phương thức này trong
                                                                         // OrderRepository

        // Chuyển đổi các đơn hàng thành DTO và lấy trạng thái hiện tại của mỗi đơn hàng
        return orders.stream().map(order -> {
            // Lấy trạng thái hiện tại của đơn hàng
            OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                    .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order); // Giả sử có phương thức này

            String currentStatus = (currentStatusDetail != null) ? currentStatusDetail.getOrderStatus().getStatusName()
                    : "Chưa xác định";

            // Trả về DTO
            return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getTotal(), currentStatus);
        }).collect(Collectors.toList());
    }

    public OrderDetailResponseDto getOrderById(Long orderId, String username) {
        // Lấy thông tin đơn hàng từ cơ sở dữ liệu
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }
        Order order = orderOpt.get();

        // Kiểm tra quyền sở hữu đơn hàng (chủ sở hữu hoặc ADMIN/STAFF)
        if (order.getUser() != null) {
            if (!order.getUser().getEmail().equals(username)) {
                System.out.println("Order owner: " + order.getUser().getEmail());
                User user = userRepository.findByEmail(username);
                // Nếu không phải chủ sở hữu, kiểm tra vai trò
                String userRole = user.getRole().getName(); // Giả sử bạn có phương thức để lấy role của user
                if (!userRole.equals("ADMIN") && !userRole.equals("STAFF")) {
                    System.out.println("User role: " + userRole);
                    throw new RuntimeException("Không thể xem đơn hàng của người khác!");
                }
            }
        }

        // Lấy thông tin chi tiết đơn hàng
        List<OrderItemResponseDto> orderItems = new ArrayList<>();
        double subTotal = 0;

        for (OrderDetail detail : order.getOrderDetails()) {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setProductId(detail.getProduct().getId());
            itemDto.setSize(detail.getSize());
            itemDto.setColor(detail.getColor());
            itemDto.setQuantity(detail.getQuantity());
            itemDto.setPrice(detail.getPrice());
            orderItems.add(itemDto);
            subTotal += detail.getPrice();
        }

        // Lấy thông tin về phí ship
        double shippingFee = order.getShippingMethod().getFee();

        // Lấy thông tin về giảm giá
        double discount = order.getDiscount();

        // Tính tổng tiền
        double total = subTotal + shippingFee - discount;

        // Tạo OrderPriceDto
        OrderPriceResponseDto priceDetails = new OrderPriceResponseDto();
        priceDetails.setSubTotal(subTotal);
        priceDetails.setShipping(shippingFee);
        priceDetails.setDiscount(discount);
        priceDetails.setTotal(total);

        // Lấy thông tin địa chỉ giao hàng
        AddressResponseDto addressDto = new AddressResponseDto();
        Address address = order.getShippingAddress();
        addressDto.setFullName(address.getFullName());
        addressDto.setPhoneNumber(address.getPhoneNumber());
        addressDto.setAddress(address.getAddress());
        addressDto.setCity(address.getCity());
        addressDto.setDistrict(address.getDistrict());
        addressDto.setWard(address.getWard());

        // Lấy thông tin trạng thái đơn hàng
        List<OrderStatusResponseDto> statusDetails = new ArrayList<>();
        for (OrderStatusDetail statusDetail : order.getOrderStatusDetails()) {
            OrderStatusResponseDto statusDto = new OrderStatusResponseDto();
            mapOrderStatusToDto(statusDetail, statusDto);
            statusDetails.add(statusDto);
        }

        // Tạo và trả về OrderResponseDto
        OrderDetailResponseDto responseDto = new OrderDetailResponseDto();
        responseDto.setItems(orderItems);
        responseDto.setPriceDetails(priceDetails);
        responseDto.setShippingAddress(addressDto);
        responseDto.setOrderStatusDetails(statusDetails);

        // Thêm thông tin phương thức thanh toán
        PaymentMethodDto paymentMethodDto = new PaymentMethodDto();
        PaymentMethod paymentMethod = order.getPaymentMethod();
        paymentMethodDto.setCode(paymentMethod.getCode());
        paymentMethodDto.setName(paymentMethod.getName());
        paymentMethodDto.setDescription(paymentMethod.getDescription());
        responseDto.setPaymentMethod(paymentMethodDto);

        // Thêm thông tin phương thức vận chuyển
        ShippingMethodDto shippingMethodDto = new ShippingMethodDto();
        ShippingMethod shippingMethod = order.getShippingMethod();
        shippingMethodDto.setCode(shippingMethod.getCode());
        shippingMethodDto.setName(shippingMethod.getName());
        shippingMethodDto.setDescription(shippingMethod.getDescription());
        shippingMethodDto.setFee(shippingMethod.getFee());
        responseDto.setShippingMethod(shippingMethodDto);

        return responseDto;
    }

    // Thêm phương thức trong OrderService
    public Page<OrderResponseDto> getAllOrdersWithPagination(int page, int size) {
        // Tạo Pageable với phân trang và sắp xếp theo ngày tạo đơn hàng
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("orderDate")));

        // Lấy danh sách đơn hàng phân trang
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        // Chuyển đổi thành DTO
        return ordersPage.map(order -> {
            // Lấy trạng thái hiện tại của đơn hàng
            OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                    .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order); // Giả sử có phương thức này

            String currentStatus = (currentStatusDetail != null) ? currentStatusDetail.getOrderStatus().getStatusName()
                    : "Chưa xác định";

            // Trả về DTO
            return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getTotal(), currentStatus);
        });
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String statusCode, String username, String cancelReason) {
        // Tìm đơn hàng theo ID
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Đơn hàng không tồn tại");
        }
        Order order = orderOpt.get();

        // Tìm trạng thái mới theo mã trạng thái (statusCode)
        Optional<OrderStatus> orderStatusOpt = orderStatusRepository.findById(statusCode);
        if (orderStatusOpt.isEmpty()) {
            throw new RuntimeException("Mã trạng thái đơn hàng không hợp lệ");
        }
        OrderStatus orderStatus = orderStatusOpt.get();

        // Lấy trạng thái hiện tại của đơn hàng
        OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);
        if (currentStatusDetail == null) {
            throw new RuntimeException("Không thể xác định trạng thái hiện tại của đơn hàng");
        }
        String currentStatus = currentStatusDetail.getOrderStatus().getCode();

        // Xử lý đặc biệt cho trạng thái CANCELLED (hủy đơn hàng)
        if ("CANCELLED".equals(statusCode)) {
            // Kiểm tra xem đơn hàng có thể hủy không
            if (!"WAITING_FOR_PAYMENT".equals(currentStatus) && !"PENDING".equals(currentStatus)) {
                throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái chờ thanh toán hoặc đang chờ xử lý");
            }

            // Kiểm tra nếu đơn hàng đã thanh toán
            boolean hasPaidStatus = order.getOrderStatusDetails().stream()
                    .anyMatch(statusDetail -> "PAID".equals(statusDetail.getOrderStatus().getCode()));

            if (hasPaidStatus) {
                throw new RuntimeException("Không thể hủy đơn hàng đã thanh toán");
            }

            // Tắt tất cả các trạng thái đang active của đơn hàng này
            deactivateAllOrderStatusDetails(order);

            // Tạo trạng thái chi tiết mới và lưu vào cơ sở dữ liệu
            OrderStatusDetail newOrderStatusDetail = new OrderStatusDetail();
            newOrderStatusDetail.setOrder(order);
            newOrderStatusDetail.setOrderStatus(orderStatus);
            newOrderStatusDetail.setUpdateAt(LocalDateTime.now());
            newOrderStatusDetail.setUser(userRepository.findByEmail(username));
            newOrderStatusDetail.setActive(true);
            newOrderStatusDetail.setCancelReason(cancelReason); // Thêm lý do hủy đơn hàng

            // Lưu trạng thái chi tiết mới vào cơ sở dữ liệu
            orderStatusDetailRepository.save(newOrderStatusDetail);

            // Khôi phục lại số lượng sản phẩm cho kho
            restoreProductQuantity(order);

            // Kiểm tra hoàn trả mã giảm giá nếu có
            if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
                decrementCouponUsage(order.getCouponCode());
            }
        } else {
            // Xử lý cập nhật trạng thái thông thường

            // Tắt tất cả các trạng thái đang active của đơn hàng này
            deactivateAllOrderStatusDetails(order);

            // Tạo trạng thái chi tiết mới và lưu vào cơ sở dữ liệu
            OrderStatusDetail newOrderStatusDetail = new OrderStatusDetail();
            newOrderStatusDetail.setOrder(order);
            newOrderStatusDetail.setOrderStatus(orderStatus);
            newOrderStatusDetail.setUpdateAt(LocalDateTime.now());
            newOrderStatusDetail.setUser(userRepository.findByEmail(username));
            newOrderStatusDetail.setActive(true);

            // Lưu trạng thái chi tiết mới vào cơ sở dữ liệu
            orderStatusDetailRepository.save(newOrderStatusDetail);

            // Nếu đơn hàng chuyển sang trạng thái "DELIVERED" (Đã giao) thì tạo feedback
            if ("DELIVERED".equals(statusCode)) {
                feedbackService.createFeedbacksForDeliveredOrder(order);
            }
        }
    }

    @Transactional
    public void updateOrderStatusToPaidAndPending(Long orderId, String username) {
        // Tìm đơn hàng theo orderId
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Đơn hàng không tồn tại");
        }
        Order order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng hiện tại
        OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);

        if (currentStatusDetail == null
                || !currentStatusDetail.getOrderStatus().getCode().equals("WAITING_FOR_PAYMENT")) {
            throw new RuntimeException("Đơn hàng đã được xử lý");
        }

        // Tắt tất cả các trạng thái đang active của đơn hàng
        deactivateAllOrderStatusDetails(order);

        // Lấy trạng thái "PAID" và "PENDING" từ bảng OrderStatus
        OrderStatus paidStatus = orderStatusRepository.findById("PAID")
                .orElseThrow(() -> new RuntimeException("Trạng thái PAID không tồn tại"));
        OrderStatus pendingStatus = orderStatusRepository.findById("PENDING")
                .orElseThrow(() -> new RuntimeException("Trạng thái PENDING không tồn tại"));

        // Tạo OrderStatusDetail cho PAID
        OrderStatusDetail paidStatusDetail = new OrderStatusDetail();
        paidStatusDetail.setOrder(order);
        paidStatusDetail.setOrderStatus(paidStatus);
        paidStatusDetail.setUpdateAt(LocalDateTime.now());
        if (username != null) {
            paidStatusDetail.setUser(userRepository.findByEmail(username)); // Người thực hiện thay đổi trạng thái
        }
        paidStatusDetail.setActive(false); // PAID không active

        // Lưu OrderStatusDetail cho PAID
        orderStatusDetailRepository.save(paidStatusDetail);

        // Tạo OrderStatusDetail cho PENDING (active)
        OrderStatusDetail pendingStatusDetail = new OrderStatusDetail();
        pendingStatusDetail.setOrder(order);
        pendingStatusDetail.setOrderStatus(pendingStatus);
        pendingStatusDetail.setUpdateAt(LocalDateTime.now().plusSeconds(1));
        if (username != null) {
            pendingStatusDetail.setUser(userRepository.findByEmail(username)); // Người thực hiện thay đổi trạng thái
        }
        pendingStatusDetail.setActive(true); // PENDING là active

        // Lưu OrderStatusDetail cho PENDING
        orderStatusDetailRepository.save(pendingStatusDetail);
    }

    // Hủy đơn hàng theo ID
    @Transactional
    public void cancelOrder(Long orderId, String username) {
        // Tìm đơn hàng theo ID
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Đơn hàng không tồn tại");
        }
        Order order = orderOpt.get();

        // Kiểm tra quyền sở hữu đơn hàng (chủ sở hữu hoặc ADMIN/STAFF)
        if (order.getUser() != null) {
            if (!order.getUser().getEmail().equals(username)) {
                User user = userRepository.findByEmail(username);
                if (user == null) {
                    throw new RuntimeException("Người dùng không tồn tại");
                }
                // Nếu không phải chủ sở hữu, kiểm tra vai trò
                String userRole = user.getRole().getName();
                if (!userRole.equals("ADMIN") && !userRole.equals("STAFF")) {
                    throw new RuntimeException("Bạn không có quyền hủy đơn hàng của người khác");
                }
            }
        }

        // Lấy trạng thái hiện tại của đơn hàng
        OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);
        if (currentStatusDetail == null) {
            throw new RuntimeException("Không thể xác định trạng thái hiện tại của đơn hàng");
        }

        String currentStatus = currentStatusDetail.getOrderStatus().getCode();

        // Kiểm tra xem đơn hàng có thể hủy không
        if (!"WAITING_FOR_PAYMENT".equals(currentStatus) && !"PENDING".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái chờ thanh toán hoặc đang chờ xử lý");
        }

        // Kiểm tra nếu đơn hàng đã thanh toán
        boolean hasPaidStatus = order.getOrderStatusDetails().stream()
                .anyMatch(statusDetail -> "PAID".equals(statusDetail.getOrderStatus().getCode()));

        if (hasPaidStatus) {
            throw new RuntimeException("Không thể hủy đơn hàng đã thanh toán");
        }

        try {
            // Tắt tất cả các trạng thái đang active của đơn hàng
            deactivateAllOrderStatusDetails(order);

            // Lấy trạng thái "CANCELLED" từ bảng OrderStatus
            OrderStatus cancelledStatus = orderStatusRepository.findById("CANCELLED")
                    .orElseThrow(() -> new RuntimeException("Trạng thái CANCELLED không tồn tại trong hệ thống"));

            // Tạo OrderStatusDetail cho CANCELLED
            OrderStatusDetail cancelledStatusDetail = new OrderStatusDetail();
            cancelledStatusDetail.setOrder(order);
            cancelledStatusDetail.setOrderStatus(cancelledStatus);
            cancelledStatusDetail.setUpdateAt(LocalDateTime.now());
            cancelledStatusDetail.setUser(userRepository.findByEmail(username)); // Người thực hiện thay đổi trạng thái
            cancelledStatusDetail.setActive(true); // CANCELLED là active

            // Lưu OrderStatusDetail cho CANCELLED
            orderStatusDetailRepository.save(cancelledStatusDetail);

            // Khôi phục lại số lượng sản phẩm cho kho
            restoreProductQuantity(order);

            // Kiểm tra hoàn trả mã giảm giá nếu có
            if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
                // Giảm số lượt sử dụng của mã giảm giá
                decrementCouponUsage(order.getCouponCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    // Phương thức riêng để khôi phục số lượng sản phẩm
    private void restoreProductQuantity(Order order) {
        List<OrderDetail> orderDetails = order.getOrderDetails();

        for (OrderDetail orderDetail : orderDetails) {
            // Tìm ProductVariant tương ứng
            ProductVariant productVariant = productVariantRepository.findByProductIdAndColorAndSize(
                    orderDetail.getProduct().getId(),
                    colorRepository.findByName(orderDetail.getColor()),
                    sizeRepository.findByName(orderDetail.getSize()));

            if (productVariant != null) {
                // Tăng số lượng trong ProductVariant
                productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
                productVariantRepository.save(productVariant);
            } else {
                // Nếu không tìm thấy ProductVariant hiện có, tạo mới
                ProductVariant newProductVariant = new ProductVariant();
                newProductVariant.setProduct(orderDetail.getProduct());
                newProductVariant.setColor(colorRepository.findByName(orderDetail.getColor()));
                newProductVariant.setSize(sizeRepository.findByName(orderDetail.getSize()));
                newProductVariant.setQuantity(orderDetail.getQuantity());
                productVariantRepository.save(newProductVariant);
            }

            // Tăng số lượng trong Product
            Product product = orderDetail.getProduct();
            product.setQuantity(product.getQuantity() + orderDetail.getQuantity());
            productRepository.save(product);
        }
    }

    // Phương thức để giảm số lượt sử dụng của mã giảm giá
    private void decrementCouponUsage(String couponCode) {
        try {
            Coupon coupon = couponRepository.findByCode(couponCode);
            if (coupon != null && coupon.getUsedCount() > 0) {
                coupon.setUsedCount(coupon.getUsedCount() - 1);
                couponRepository.save(coupon);
            }
        } catch (Exception e) {
            // Ghi log lỗi nhưng không làm gián đoạn quy trình hủy đơn hàng
            System.err.println("Lỗi khi giảm số lượt sử dụng mã giảm giá: " + e.getMessage());
        }
    }

    public List<UserOrderResponseDto> getOrdersByUserId(Long userId) {
        // Tìm người dùng theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Lấy tất cả đơn hàng của người dùng
        List<Order> orders = user.getOrders();

        // Chuyển đổi đơn hàng thành UserOrderResponseDto
        return orders.stream().map(order -> {
            // Lấy trạng thái hiện tại của đơn hàng
            OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                    .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);

            String currentStatus = (currentStatusDetail != null)
                    ? currentStatusDetail.getOrderStatus().getStatusName()
                    : "Chưa xác định";

            // Lấy số điện thoại từ địa chỉ giao hàng
            String phoneNumber = order.getShippingAddress() != null
                    ? order.getShippingAddress().getPhoneNumber()
                    : "Không có";

            // Tạo và trả về DTO
            return new UserOrderResponseDto(
                    order.getId(),
                    order.getOrderDate(),
                    phoneNumber,
                    order.getTotal(),
                    currentStatus);
        }).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách đơn hàng cho Admin và Staff với bộ lọc và phân trang
     * 
     * @param page      Số trang
     * @param size      Số lượng đơn hàng trên một trang
     * @param filterDto Các điều kiện lọc
     * @return Danh sách đơn hàng đã được phân trang và lọc
     */
    public Page<OrderResponseDto> getOrdersForAdmin(int page, int size, AdminOrderFilterRequestDto filterDto) {
        // Tạo Pageable với phân trang và sắp xếp theo ngày tạo đơn hàng giảm dần
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("orderDate")));

        // Tạo Specification để áp dụng điều kiện lọc
        Specification<Order> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo ID đơn hàng nếu có
            if (filterDto.getOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterDto.getOrderId()));
            }

            // Lọc theo phương thức thanh toán nếu có
            if (filterDto.getPaymentMethodCode() != null && !filterDto.getPaymentMethodCode().isEmpty()) {
                Join<Order, PaymentMethod> paymentMethodJoin = root.join("paymentMethod");
                predicates.add(criteriaBuilder.equal(paymentMethodJoin.get("code"), filterDto.getPaymentMethodCode()));
            }

            // Lọc theo phương thức vận chuyển nếu có
            if (filterDto.getShippingMethodCode() != null && !filterDto.getShippingMethodCode().isEmpty()) {
                Join<Order, ShippingMethod> shippingMethodJoin = root.join("shippingMethod");
                predicates
                        .add(criteriaBuilder.equal(shippingMethodJoin.get("code"), filterDto.getShippingMethodCode()));
            }

            // Lọc theo trạng thái đơn hàng nếu có
            if (filterDto.getOrderStatusCode() != null && !filterDto.getOrderStatusCode().isEmpty()) {
                // Subquery để lấy đơn hàng có trạng thái hiện tại (isActive = true) khớp với mã
                // trạng thái
                Subquery<Long> statusSubquery = query.subquery(Long.class);
                Root<OrderStatusDetail> statusDetailRoot = statusSubquery.from(OrderStatusDetail.class);
                statusSubquery.select(statusDetailRoot.get("order").get("id"))
                        .where(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(statusDetailRoot.get("orderStatus").get("code"),
                                                filterDto.getOrderStatusCode()),
                                        criteriaBuilder.isTrue(statusDetailRoot.get("isActive"))));
                predicates.add(root.get("id").in(statusSubquery));
            }

            // Lọc theo ngày bắt đầu nếu có
            if (filterDto.getStartDate() != null) {
                LocalDateTime startDateTime = filterDto.getStartDate().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDateTime));
            }

            // Lọc theo ngày kết thúc nếu có
            if (filterDto.getEndDate() != null) {
                LocalDateTime endDateTime = filterDto.getEndDate().plusDays(1).atStartOfDay();
                predicates.add(criteriaBuilder.lessThan(root.get("orderDate"), endDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Lấy danh sách đơn hàng từ database dựa trên các điều kiện lọc
        Page<Order> ordersPage = orderRepository.findAll(specification, pageable);

        // Chuyển đổi đơn hàng thành OrderResponseDto
        return ordersPage.map(order -> {
            // Lấy trạng thái hiện tại của đơn hàng
            OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                    .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);

            String currentStatus = (currentStatusDetail != null)
                    ? currentStatusDetail.getOrderStatus().getStatusName()
                    : "Chưa xác định";

            // Trả về DTO
            return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getTotal(), currentStatus);
        });
    }

    // Phương thức để tắt tất cả các trạng thái đang active của đơn hàng
    private void deactivateAllOrderStatusDetails(Order order) {
        List<OrderStatusDetail> activeStatusDetails = orderStatusDetailRepository.findByOrderAndIsActiveTrue(order);
        for (OrderStatusDetail statusDetail : activeStatusDetails) {
            statusDetail.setActive(false);
            orderStatusDetailRepository.save(statusDetail);
        }
    }

    // Cập nhật phương thức getOrderById để bao gồm lý do hủy đơn hàng trong
    // response
    private void mapOrderStatusToDto(OrderStatusDetail statusDetail, OrderStatusResponseDto statusDto) {
        statusDto.setStatusName(statusDetail.getOrderStatus().getStatusName());
        statusDto.setDescription(statusDetail.getOrderStatus().getDescription());
        statusDto.setActive(statusDetail.isActive());
        statusDto.setUpdateAt(statusDetail.getUpdateAt());
        statusDto.setCancelReason(statusDetail.getCancelReason());

        // Kiểm tra xem user có null không
        if (statusDetail.getUser() != null && statusDetail.getUser().getRole() != null) {
            statusDto.setUpdatedBy(statusDetail.getUser().getFullName() + "  " + "["
                    + statusDetail.getUser().getRole().getName() + "]");
        } else {
            statusDto.setUpdatedBy("Khách vãng lai");
        }
    }
}
