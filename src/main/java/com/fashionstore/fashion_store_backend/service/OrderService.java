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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Long generateOrderId() {
        return System.currentTimeMillis() + (long) (Math.random() * 100000);
    }


    public Long createOrder(OrderCreateDto orderCreateDto, String username) {
        User user = null;
        if (username != null) {
            user = userRepository.findByEmail(username);
        }

        Optional<Address> optionalAddress = addressRepository.findByUserAndDefaultAddress(user, orderCreateDto.getAddress().isDefaultAddress());

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
        order.setTotal(0);  // Tạm thời đặt tổng là 0
        order.setOrderDetails(new ArrayList<>()); // Khởi tạo danh sách OrderDetails

        // Kiểm tra số lượng sản phẩm trong giỏ hàng và tính tổng tiền
        double total = 0;
        List<OrderDetail> orderDetails = order.getOrderDetails();
        for (CartProductDTO cartItem : orderCreateDto.getCart()) {
            // Lấy color và size của từng sản phẩm trong giỏ hàng
            Color color = null;
            Size size = null;

            if (cartItem.getColor() != null && !cartItem.getColor().isEmpty()) {
                color = colorRepository.findByName(cartItem.getColor());
            }

            if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
                size = sizeRepository.findByName(cartItem.getSize());
            }

            ProductVariant productVariant = productVariantRepository.findByProductIdAndColorAndSize(cartItem.getProductId(), color, size);

            if (productVariant == null) {
                throw new RuntimeException("Sản phẩm không tồn tại");
            }

            if (productVariant.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Có lỗi xảy ra liên quan đến số lượng sản phẩm, vui lòng kiểm tra lại tại trang giỏ hàng");
            }

            // Giảm số lượng trong ProductVariant
            productVariant.setQuantity(productVariant.getQuantity() - cartItem.getQuantity());
            productVariantRepository.save(productVariant);

            // Cập nhật tổng số lượng sản phẩm trong bảng Product
            Product product = productVariant.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(productVariant.getProduct());
            orderDetail.setSize(cartItem.getSize());
            orderDetail.setColor(cartItem.getColor());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(productVariant.getProduct().getSalePrice() != 0 ? productVariant.getProduct().getSalePrice() * cartItem.getQuantity() : productVariant.getProduct().getPrice() * cartItem.getQuantity());
            orderDetail.setOrder(order);  // Thiết lập order cho orderDetail
            orderDetails.add(orderDetail);

            total += productVariant.getProduct().getSalePrice() != 0 ? (productVariant.getProduct().getSalePrice() * cartItem.getQuantity()) : (productVariant.getProduct().getPrice() * cartItem.getQuantity());
        }

        // Tính phí vận chuyển
        total += shippingMethod.getFee();

        // Cập nhật lại tổng tiền của đơn hàng
        order.setTotal(total);

        // Kiểm tra và khởi tạo danh sách orderStatusDetails nếu cần
        if (order.getOrderStatusDetails() == null) {
            order.setOrderStatusDetails(new ArrayList<>()); // Khởi tạo danh sách nếu nó null
        }

        // Lưu đơn hàng
        orderRepository.save(order);

        // Tạo trạng thái đơn hàng mặc định là PENDING
        OrderStatusDetail orderStatusDetail = new OrderStatusDetail();
        orderStatusDetail.setOrder(order);
        orderStatusDetail.setOrderStatus(orderStatusRepository.findById("PENDING").orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng không hợp lệ")));
        orderStatusDetail.setUpdateAt(LocalDateTime.now());
        orderStatusDetail.setUser(user);
        orderStatusDetail.setActive(true);

        // Thêm trạng thái đơn hàng vào danh sách
        order.getOrderStatusDetails().add(orderStatusDetail);

        // Lưu OrderStatusDetail vào bảng OrderStatusDetail
        orderStatusDetailRepository.save(orderStatusDetail);

        return order.getId();
    }

    public List<OrderResponseDto> getOrdersByUsername(String username) {
        // Lấy danh sách đơn hàng của người dùng từ repository
        List<Order> orders = orderRepository.findByUser_Email(username); // Giả sử đã có phương thức này trong OrderRepository

        // Chuyển đổi các đơn hàng thành DTO và lấy trạng thái hiện tại của mỗi đơn hàng
        return orders.stream().map(order -> {
            // Lấy trạng thái hiện tại của đơn hàng
            OrderStatusDetail currentStatusDetail = orderStatusDetailRepository
                    .findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order); // Giả sử có phương thức này

            String currentStatus = (currentStatusDetail != null) ? currentStatusDetail.getOrderStatus().getStatusName() : "Chưa xác định";

            // Trả về DTO
            return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getTotal(), currentStatus);
        }).collect(Collectors.toList());
    }

    public OrderDetailResponseDto getOrderById(Long orderId, String username) {
        // Lấy thông tin đơn hàng từ cơ sở dữ liệu
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();

        // Kiểm tra quyền sở hữu đơn hàng (chủ sở hữu hoặc ADMIN/STAFF)
        if (!order.getUser().getEmail().equals(username)) {
            System.out.println("Order owner: " + order.getUser().getEmail());
            User user = userRepository.findByEmail(username);
            // Nếu không phải chủ sở hữu, kiểm tra vai trò
            String userRole = user.getRole().getName(); // Giả sử bạn có phương thức để lấy role của user
            if (!userRole.equals("ADMIN") && !userRole.equals("STAFF")) {
                System.out.println("User role: " + userRole);
                throw new RuntimeException("Unauthorized");
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
            statusDto.setStatusName(statusDetail.getOrderStatus().getStatusName());
            statusDto.setDescription(statusDetail.getOrderStatus().getDescription());
            statusDto.setUpdateAt(statusDetail.getUpdateAt());

            // Kiểm tra xem user có null không
            if (statusDetail.getUser() != null && statusDetail.getUser().getRole() != null) {
                statusDto.setUpdatedBy(statusDetail.getUser().getRole().getName());
            } else {
                // Nếu User hoặc Role là null, có thể gán giá trị mặc định hoặc bỏ qua
                statusDto.setUpdatedBy("USER");
            }

            statusDetails.add(statusDto);
        }


        // Tạo và trả về OrderResponseDto
        OrderDetailResponseDto responseDto = new OrderDetailResponseDto();
        responseDto.setItems(orderItems);
        responseDto.setPriceDetails(priceDetails);
        responseDto.setShippingAddress(addressDto);
        responseDto.setOrderStatusDetails(statusDetails);
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

            String currentStatus = (currentStatusDetail != null) ? currentStatusDetail.getOrderStatus().getStatusName() : "Chưa xác định";

            // Trả về DTO
            return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getTotal(), currentStatus);
        });
    }

    public void updateOrderStatus(Long orderId, String statusCode, String username) {
        // Tìm đơn hàng theo ID
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();

        // Tìm trạng thái mới theo mã trạng thái (statusCode)
        Optional<OrderStatus> orderStatusOpt = orderStatusRepository.findById(statusCode);
        if (orderStatusOpt.isEmpty()) {
            throw new RuntimeException("Invalid order status code");
        }
        OrderStatus orderStatus = orderStatusOpt.get();

        // Tắt tất cả các trạng thái đang active của đơn hàng này
        OrderStatusDetail activeStatusDetail = orderStatusDetailRepository.findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(order);
        activeStatusDetail.setActive(false);
        orderStatusDetailRepository.save(activeStatusDetail);
        // Tạo trạng thái chi tiết mới và lưu vào cơ sở dữ liệu
        OrderStatusDetail newOrderStatusDetail = new OrderStatusDetail();
        newOrderStatusDetail.setOrder(order);
        newOrderStatusDetail.setOrderStatus(orderStatus);
        newOrderStatusDetail.setUpdateAt(LocalDateTime.now());
        newOrderStatusDetail.setUser(userRepository.findByEmail(username)); // Lấy user thực hiện cập nhật trạng thái
        newOrderStatusDetail.setActive(true);

        // Lưu trạng thái chi tiết mới vào cơ sở dữ liệu
        orderStatusDetailRepository.save(newOrderStatusDetail);
    }


}


