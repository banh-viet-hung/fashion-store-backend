package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.CartProductDTO;
import com.fashionstore.fashion_store_backend.dto.OrderCreateDto;
import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        // Thêm trạng thái đơn hàng vào danh sách
        order.getOrderStatusDetails().add(orderStatusDetail);

        // Lưu OrderStatusDetail vào bảng OrderStatusDetail
        orderStatusDetailRepository.save(orderStatusDetail);

        return order.getId();
    }


}


