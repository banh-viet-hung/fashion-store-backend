package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.exception.EmailAlreadyExistsException;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.FavoriteProductService;
import com.fashionstore.fashion_store_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteProductService favoriteProductService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Mật khẩu và xác nhận mật khẩu không khớp", false));
        }

        userService.registerUser(registrationDto);
        ApiResponse response = new ApiResponse("Đăng ký thành công", true, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email không được để trống", false));
        }

        boolean exists = userService.emailExists(email);
        if (exists) {
            return ResponseEntity.ok(new ApiResponse("Email tồn tại", true));
        } else {
            return ResponseEntity.ok(new ApiResponse("Không tồn tại email", false));
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email không được để trống", false));
        }

        userService.sendPasswordResetEmail(email);
        return ResponseEntity.ok(new ApiResponse("Email đặt lại mật khẩu đã được gửi", true));
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable String token,
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Mật khẩu và xác nhận mật khẩu không khớp", false));
        }

        try {
            userService.resetPassword(token, resetPasswordDto.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Mật khẩu đã được đặt lại thành công", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        UserInfoDto userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(new ApiResponse("Thông tin người dùng", true, userInfo));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        userService.updateUserInfo(username, userUpdateDto);
        return ResponseEntity.ok(new ApiResponse("Cập nhật thông tin thành công", true));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            userService.changePassword(username, changePasswordDto);
            return ResponseEntity.ok(new ApiResponse("Mật khẩu đã được đổi thành công", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/avatar-and-fullname")
    public ResponseEntity<ApiResponse> getUserAvatarAndFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        UserAvatarDto userAvatarDto = userService.getUserAvatarAndFullName(username);
        return ResponseEntity.ok(new ApiResponse("Thông tin người dùng", true, userAvatarDto));
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Lấy email từ token

        String avatarUrl = request.get("avatar");

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("URL avatar không được để trống", false));
        }

        userService.updateUserAvatar(email, avatarUrl);
        return ResponseEntity.ok(new ApiResponse("Cập nhật avatar thành công", true));
    }

    // API thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/favorite/add/{productId}")
    public ResponseEntity<ApiResponse> addToFavorites(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            favoriteProductService.addProductToFavorites(username, productId);
            return ResponseEntity.ok(new ApiResponse("Thêm sản phẩm vào danh sách yêu thích thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/favorite/remove/{productId}")
    public ResponseEntity<ApiResponse> removeFromFavorites(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            favoriteProductService.removeProductFromFavorites(username, productId);
            return ResponseEntity.ok(new ApiResponse("Xóa sản phẩm khỏi danh sách yêu thích thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API lấy danh sách các sản phẩm yêu thích
    @GetMapping("/favorite")
    public ResponseEntity<ApiResponse> getFavoriteProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            // Lấy danh sách các sản phẩm yêu thích theo ID
            List<Long> favoriteProductIds = favoriteProductService.getFavoriteProductIds(username);
            return ResponseEntity.ok(new ApiResponse("Danh sách sản phẩm yêu thích", true, favoriteProductIds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsersWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isActive) {

        if (page < 1) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Số trang phải lớn hơn hoặc bằng 1", false));
        }
        if (size < 1) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Kích thước trang phải lớn hơn hoặc bằng 1", false));
        }

        try {
            // Kiểm tra quyền: STAFF chỉ được xem danh sách USER
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isStaff = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("STAFF"));

            // Nếu là STAFF nhưng roleName không phải là "USER" thì từ chối quyền truy cập
            if (isStaff && (roleName == null || !roleName.equals("USER"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse("Bạn không có quyền xem danh sách này", false));
            }

            Page<UserRspDto> userPage = userService.getAllUsersWithPagination(page, size, searchTerm, roleName,
                    isActive);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Danh sách người dùng", true, userPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Lỗi server: " + e.getMessage(), false));
        }
    }

    // API lấy thông tin người dùng theo username
    @GetMapping("/info/{username}")
    public ResponseEntity<ApiResponse> getUserInfoByUsername(@PathVariable String username) {
        try {
            UserResponseDto userResponseDto = userService.getUserInfoByUsername(username);
            return ResponseEntity.ok(new ApiResponse("Thông tin người dùng", true, userResponseDto));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Người dùng không tồn tại", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/update-role/{username}")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable String username,
            @RequestBody Map<String, String> request) {
        String roleName = request.get("role");

        if (roleName == null || roleName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Tên quyền không được để trống", false));
        }

        try {
            userService.updateUserRole(username, roleName);
            return ResponseEntity.ok(new ApiResponse("Cập nhật quyền cho người dùng thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/change-status/{username}")
    public ResponseEntity<ApiResponse> changeUserStatus(@PathVariable String username) {
        try {
            // Gọi service để thay đổi trạng thái người dùng
            boolean isActive = userService.toggleUserStatus(username);

            String statusMessage = isActive ? "Đã mở khóa tài khoản người dùng" : "Đã khóa tài khoản người dùng";

            return ResponseEntity.ok(new ApiResponse(statusMessage, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/check-status")
    public ResponseEntity<ApiResponse> checkUserStatus(@RequestParam String username) {
        // Kiểm tra trạng thái tài khoản của người dùng dựa trên username
        System.out.println("username: " + username);
        boolean isActive = userService.isUserActive(username);
        String message = isActive ? "Tài khoản đang hoạt động"
                : "Tài khoản đã bị khóa! Vui lòng liên hệ quản trị viên để biết thêm chi tiết!";

        return ResponseEntity.ok(new ApiResponse(message, isActive));
    }

    @PostMapping("/admin/create-user")
    public ResponseEntity<ApiResponse> createUserByAdmin(@Valid @RequestBody AdminUserCreateDto userCreateDto) {
        try {
            User user = userService.createUserByAdmin(userCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Tạo người dùng thành công", true, user.getEmail()));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Lỗi server: " + e.getMessage(), false));
        }
    }

}
