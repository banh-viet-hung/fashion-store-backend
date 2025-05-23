package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.exception.EmailAlreadyExistsException;
import com.fashionstore.fashion_store_backend.model.Role;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.RoleRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã tồn tại");
        }

        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setActive(true);

        // Lấy role USER từ database và gán cho người dùng
        Role userRole = roleRepository.findByName("USER");
        if (userRole != null) {
            user.setRole(userRole); // Gán role USER cho người dùng
        }

        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendPasswordResetEmail(String email) {
        emailService.sendPasswordResetEmail(email);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }

    public UserInfoDto getUserInfo(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        return new UserInfoDto(user.getFullName(), user.getPhoneNumber(), user.getGender(), user.getDateOfBirth());
    }

    public void updateUserInfo(String username, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        user.setFullName(userUpdateDto.getFullName());
        user.setPhoneNumber(userUpdateDto.getPhoneNumber());
        user.setGender(userUpdateDto.getGender());
        user.setDateOfBirth(userUpdateDto.getDateOfBirth());

        userRepository.save(user);
    }

    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    public UserAvatarDto getUserAvatarAndFullName(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        return new UserAvatarDto(user.getFullName(), user.getAvatar());
    }

    public void updateUserAvatar(String email, String avatarUrl) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    public Page<UserRspDto> getAllUsersWithPagination(int page, int size, String searchTerm, String roleName,
            Boolean isActive) {
        Pageable pageable = PageRequest.of(page - 1, size); // Page bắt đầu từ 0 trong Spring Data

        Specification<User> spec = Specification.where(null);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Tìm kiếm theo email, hoặc fullName, hoặc phoneNumber
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("email"), "%" + searchTerm.trim() + "%"),
                    cb.like(root.get("fullName"), "%" + searchTerm.trim() + "%"),
                    cb.like(root.get("phoneNumber"), "%" + searchTerm.trim() + "%")));
        }

        if (roleName != null && !roleName.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role").get("name"), roleName.trim()));
        }

        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(this::convertToUserResponseDto);
    }

    private UserRspDto convertToUserResponseDto(User user) {
        UserRspDto dto = new UserRspDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setActive(user.isActive());
        dto.setRoleName(user.getRole() != null ? user.getRole().getName() : "Chưa cập nhật");
        return dto;
    }

    public UserResponseDto getUserInfoByUsername(String username) {
        User user = userRepository.findByEmail(username); // Hoặc dùng findByUsername nếu có cột username
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        // Chuyển đổi đối tượng User thành UserResponseDto
        return new UserResponseDto(user.getFullName(), user.getAvatar(), user.getEmail(), user.getPhoneNumber(),
                user.getRole() != null ? user.getRole().getName() : "Chưa cập nhật", user.isActive());
    }

    public void updateUserRole(String username, String roleName) {
        // Tìm người dùng theo email (hoặc username nếu có cột username)
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        // Tìm role từ database
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Quyền không hợp lệ");
        }

        // Cập nhật role cho người dùng
        user.setRole(role);
        userRepository.save(user);
    }

    public boolean toggleUserStatus(String username) {
        // Tìm người dùng theo username
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        // Thay đổi trạng thái người dùng
        user.setActive(!user.isActive()); // Nếu đang active thì set thành inactive và ngược lại

        // Lưu lại người dùng sau khi thay đổi trạng thái
        userRepository.save(user);

        return user.isActive(); // Trả về trạng thái mới của người dùng
    }

    public boolean isUserActive(String username) {
        User user = userRepository.findByEmail(username); // Hoặc sử dụng email nếu username là email
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }
        return user != null && user.isActive(); // Trả về trạng thái isActive của người dùng
    }

    @Transactional
    public User createUserByAdmin(AdminUserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã tồn tại");
        }

        User user = new User();
        user.setFullName(userCreateDto.getFullName());
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setActive(true);

        // Tìm role từ database dựa trên tên role được cung cấp
        Role role = roleRepository.findByName(userCreateDto.getRole());
        if (role == null) {
            throw new RuntimeException("Vai trò không hợp lệ");
        }
        user.setRole(role);

        return userRepository.save(user);
    }

}
