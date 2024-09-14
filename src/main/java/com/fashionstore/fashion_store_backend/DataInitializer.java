package com.fashionstore.fashion_store_backend;

import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FavoriteProductRepository favoriteProductRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;


    @Override
    public void run(String... args) throws Exception {

        // Tạo user => Done!
        User user = new User();
        user.setFirstName("Viết Hùng");
        user.setLastName("Bành");
        user.setUsername("hungbanh");
        user.setPassword("123456");
        user.setGender("Nam");
        user.setEmail("banhviet.hung123@gmail.com");
        user.setPhoneNumber("0123456789");

        // Tạo role cho user => Done!
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setUsers(Set.of(user));

        // Tạo địa chỉ cho user => Done!
        Address address = new Address();
        address.setStreet("Linh Trung");
        address.setCity("Thu Duc");
        address.setState("TP.HCM");
        address.setZipCode("700000");
        address.setCountry("Viet Nam");
//        address.setUser(user);

        // Tao feedback cho user => Done!
        Feedback feedback = new Feedback();
        feedback.setRating(5);
        feedback.setComment("Sản phẩm rất tốt");
        feedback.setCreatedAt(java.time.LocalDateTime.now());
//        feedback.setUser(user);

        // Tạo product => Done!
        Product product = new Product();
        product.setName("Áo thun cá sấu");
        product.setPrice(100000);
        product.setSalePrice(80000);
        product.setAverageRating(4.5);
        product.setQuantity(100);
        product.setDescription("Áo thun cá sấu hàng hiệu");
        product.setCreatedAt(java.time.LocalDateTime.now());
        product.setBrand("Lacoste");
        product.setSize("M");
        product.setColor("Xanh");
//        feedback.setProduct(product);

        // Tạo category => Done!
        Category category = new Category();
        category.setName("Áo thun");
        category.setDescription("Áo thun nam hàng hiệu");
        category.setCreatedAt(java.time.LocalDateTime.now());
        category.setProducts(Set.of(product));

        // Tạp image cho product => Done!
        Image image = new Image();
        image.setUrl("https://www.google.com.vn");
        image.setThumbnail(true);
        image.setAltText("Áo thun cá sấu");
        image.setCreatedAt(java.time.LocalDateTime.now());
//        image.setProduct(product);

        // Tạo favorite product => Done!
        FavoriteProduct favoriteProduct = new FavoriteProduct();
//        favoriteProduct.setUser(user);
//        favoriteProduct.setProduct(product);

        // Tạo order detail => Done!
        Order order = new Order();
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setTotalProductPrice(100000);
        order.setShippingFee(20000);
        order.setTax(10000);
        order.setTotal(130000);
//        order.setUser(user);
        order.setShippingAddress(address);

        // Tạo payment method => Done!
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName("Thanh toán khi nhận hàng");
        paymentMethod.setDescription("Thanh toán khi nhận hàng");
        order.setPaymentMethod(paymentMethod);

        // Tạo shipping method => Done!
        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setName("Giao hàng tiết kiệm");
        shippingMethod.setDescription("Giao hàng tiết kiệm");
        shippingMethod.setFee(20000);
        order.setShippingMethod(shippingMethod);

        // Tạo order detail => Done!
        OrderDetail orderDetail = new OrderDetail();
//        orderDetail.setProduct(product);
        orderDetail.setQuantity(1);
//        orderDetail.setOrder(order);
        orderDetail.setPrice(100000);

        // Save data => Done!
//        roleRepository.save(role);
//        userRepository.save(user);

//        addressRepository.save(address);

//        feedbackRepository.save(feedback);

//        productRepository.save(product);

//        categoryRepository.save(category);

//        imageRepository.save(image);

//        favoriteProductRepository.save(favoriteProduct);

//        orderRepository.save(order);
//        paymentMethodRepository.save(paymentMethod);
//        shippingMethodRepository.save(shippingMethod);

//        orderDetailRepository.save(orderDetail);
    }
}
