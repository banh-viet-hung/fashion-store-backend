package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.model.Order;
import com.fashionstore.fashion_store_backend.model.PaymentMethod;
import com.fashionstore.fashion_store_backend.repository.OrderRepository;
import com.fashionstore.fashion_store_backend.util.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    @Value("${vnp.TmnCode}")
    private String tmnCode;

    @Value("${vnp.HashSecret}")
    private String hashSecret;

    @Value("${vnp.PayUrl}")
    private String payUrl;

    @Value("${vnp.ReturnUrl}")
    private String returnUrl;

    @Autowired
    private OrderRepository orderRepository;

    public String createPaymentUrl(Long orderId, String ipAddr, String bankCode) throws Exception {
        // Lấy đối tượng Order từ DB dựa vào orderId
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        double total = order.get().getTotal(); // Tổng tiền từ đối tượng Order
        PaymentMethod paymentMethod = order.get().getPaymentMethod(); // Hình thức thanh toán
        String selectedBankCode = paymentMethod.getCode();
        if (bankCode != null) {
            selectedBankCode = bankCode;
        }

        // Cập nhật thời gian tạo và hết hạn
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = df.format(new Date());
        Calendar expireDate = Calendar.getInstance();
        expireDate.add(Calendar.MINUTE, 15); // Đặt thời gian hết hạn thanh toán là 15 phút sau
        String vnpExpireDate = df.format(expireDate.getTime());

        // Tính toán số tiền phải thanh toán (nhân với 100 để loại bỏ phần thập phân)
        long vnpAmount = Math.round(total * 100); // Đảm bảo số tiền được tính đúng

        // Tạo các tham số cho URL thanh toán
        Map<String, String> vnpParams = new TreeMap<>(); // Sử dụng TreeMap để tự động sắp xếp theo thứ tự tăng dần của tên tham số
        vnpParams.put("vnp_Version", "2.1.0"); // Phiên bản API
        vnpParams.put("vnp_Command", "pay"); // Lệnh thanh toán
        vnpParams.put("vnp_TmnCode", tmnCode); // Mã website của merchant
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount)); // Số tiền thanh toán (Nhớ nhân với 100)
        if (selectedBankCode != null) {
            vnpParams.put("vnp_BankCode", selectedBankCode); // Nếu có mã ngân hàng, thêm vào tham số
        }
        vnpParams.put("vnp_CurrCode", "VND"); // Đơn vị tiền tệ (VND)
        vnpParams.put("vnp_TxnRef", orderId.toString()); // Mã tham chiếu giao dịch
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho don hang " + orderId); // Thông tin đơn hàng
        vnpParams.put("vnp_OrderType", "other"); // Loại đơn hàng (Có thể là "other" hoặc tùy chỉnh theo yêu cầu)
        vnpParams.put("vnp_ReturnUrl", returnUrl); // URL trả kết quả sau thanh toán
        vnpParams.put("vnp_Locale", "vn"); // Ngôn ngữ giao diện (Tiếng Việt)
        vnpParams.put("vnp_IpAddr", ipAddr); // Địa chỉ IP của người thực hiện giao dịch
        vnpParams.put("vnp_CreateDate", vnpCreateDate); // Thời gian tạo giao dịch
        vnpParams.put("vnp_ExpireDate", vnpExpireDate); // Thời gian hết hạn thanh toán

        // Sử dụng VNPayUtil để tạo query string và hash data
        String queryString = VNPayUtil.getPaymentURL(vnpParams, true); // Tạo query string với mã hóa
        String hashData = VNPayUtil.getPaymentURL(vnpParams, false);  // Tạo hashData mà không mã hóa
        String secureHash = VNPayUtil.hmacSHA512(hashSecret, hashData); // Tạo mã checksum bằng HMACSHA512
        queryString += "&vnp_SecureHash=" + URLEncoder.encode(secureHash, "UTF-8");  // Thêm secure hash vào URL

        // Trả về URL thanh toán hoàn chỉnh
        return payUrl + "?" + queryString;
    }
}
