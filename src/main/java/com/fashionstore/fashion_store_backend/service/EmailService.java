package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return; // Không gửi email nếu người dùng không tồn tại

        String token = generateToken();
        user.setResetToken(token);
        user.setResetTokenExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String subject = "Yêu cầu đặt lại mật khẩu";
        String resetUrl = "http://localhost:3000/account/password/" + token;

        String message = String.format("Xin chào,\n\nChúng tôi nhận được yêu cầu đặt lại mật khẩu của bạn tại coolman.me. Nếu bạn không yêu cầu, bạn có thể bỏ qua email này. Nếu bạn thực sự quên mật khẩu, hãy nhấp vào đường link dưới đây hoặc sao chép nó vào trình duyệt để đặt lại mật khẩu cho tài khoản của bạn.\n\n%s\n\nLưu ý: Đường link này chỉ có hiệu lực trong 10 phút.\n\nHy vọng bạn sẽ không quên mật khẩu của mình, nhưng nếu có, chúng tôi luôn sẵn sàng hỗ trợ bạn.\n\nNếu cần hỗ trợ, vui lòng liên hệ hotline: 0372590536 hoặc gửi email đến hộp thư chăm sóc khách hàng: banhviet.hung123@gmail.com. Chúng tôi cam kết hỗ trợ bạn kịp thời để đảm bảo trải nghiệm mua sắm tuyệt vời nhất.\n\nTrân trọng cảm ơn quý khách,\nĐội ngũ Coolman.", resetUrl);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }


    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
