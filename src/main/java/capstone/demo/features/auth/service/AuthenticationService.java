package capstone.demo.features.auth.service;

import capstone.demo.config.JwtService;
import capstone.demo.entity.LoginHistory;
import capstone.demo.entity.OtpEntity;
import capstone.demo.entity.User;
import capstone.demo.entity.enums.Role;
import capstone.demo.features.auth.dto.*;
import capstone.demo.features.auth.repository.UserRepository;
import capstone.demo.repository.OtpRepository;
import capstone.demo.repository.LoginHistoryRepository;
import capstone.demo.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository repository;
    private final OtpRepository otpRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
    private final SpringTemplateEngine templateEngine;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if email already exists
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();

        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();
        repository.save(user);

        // Send verification email
        sendVerificationEmail(user.getEmail(), verificationToken);

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Kiểm tra tài khoản có bị khóa không
            if (!user.isAccountNonLocked()) {
                throw new LockedException("Tài khoản của bạn đã bị khóa. Vui lòng thử lại sau 24 giờ hoặc liên hệ admin.");
            }

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

                // Đăng nhập thành công - reset số lần thất bại
                if (user.getFailedLoginAttempts() > 0) {
                    user.setFailedLoginAttempts(0);
                    repository.save(user);
                }

            } catch (BadCredentialsException e) {
                // Tăng số lần đăng nhập thất bại
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

                // Kiểm tra nếu đạt ngưỡng khóa tài khoản
                if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                    handleAccountLock(user);
                    throw new LockedException("Tài khoản đã bị khóa do đăng nhập sai nhiều lần. Vui lòng thử lại sau 24 giờ.");
                }

                repository.save(user);
                throw new BadCredentialsException("Sai mật khẩu. Còn " + (MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts()) + " lần thử.");
            }

            // Các kiểm tra khác (email verified, require password change)
            if (!user.isEmailVerified()) {
                throw new DisabledException("Email not verified");
            }

            if (user.isRequirePasswordChange()) {
                throw new DisabledException("PASSWORD_CHANGE_REQUIRED");
            }

            // Tạo JWT token và trả về
            var jwtToken = jwtService.generateToken(user);

            // Lưu lịch sử đăng nhập thành công
            saveLoginHistory(user, true, null);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (DisabledException | LockedException | BadCredentialsException e) {
            // Lưu lịch sử đăng nhập thất bại
            var user = repository.findByEmail(request.getEmail()).orElse(null);
            if (user != null) {
                saveLoginHistory(user, false, e.getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleAccountLock(User user) {
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());
        repository.save(user);

        // Gửi email thông báo
        sendAccountLockedEmail(user);
    }

    private void sendAccountLockedEmail(User user) {
        try {
            Context context = new Context();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

            context.setVariables(Map.of(
                    "firstName", user.getFirstname(),
                    "email", user.getEmail(),
                    "attempts", MAX_FAILED_ATTEMPTS,
                    "lockTime", user.getLockTime().format(formatter),
                    "unlockTime", user.getLockTime().plusHours(24).format(formatter)
            ));

            String emailContent = templateEngine.process("account-locked", context);
            emailService.sendVerificationEmail(user.getEmail(), emailContent);
        } catch (Exception e) {
            // Log error but don't throw - this shouldn't affect the main flow
            e.printStackTrace();
        }
    }

    private void saveLoginHistory(User user, boolean success, String failureReason) {
        try {
            LoginHistory history = LoginHistory.builder()
                    .user(user)
                    .loginTime(LocalDateTime.now())
                    .loginSuccess(success)
                    .failureReason(failureReason)
                    .ipAddress(getCurrentIpAddress())
                    .userAgent(getCurrentUserAgent())
                    .build();
            loginHistoryRepository.save(history);
        } catch (Exception e) {
            // Log error but don't throw
            e.printStackTrace();
        }
    }

    private String getCurrentIpAddress() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest().getRemoteAddr();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getCurrentUserAgent() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest().getHeader("User-Agent");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @Override
    public BaseResponse<?> verifyEmail(VerifyEmailRequest request) {
        var user = repository.findByEmailAndVerificationToken(request.getEmail(), request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        repository.save(user);

        return BaseResponse.success("Email verified successfully");
    }

    @Override
    public BaseResponse<?> forgotPassword(ForgotPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return BaseResponse.error("User not found");
        }

        // Generate OTP
        String otp = generateOTP();

        // Save OTP to database
        OtpEntity otpEntity = OtpEntity.builder()
                .email(request.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        otpRepository.save(otpEntity);

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);

        return BaseResponse.success("OTP has been sent to your email");
    }

    @Override
    public BaseResponse<?> verifyOtp(VerifyOtpRequest request) {
        var otpEntity = otpRepository.findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp())
                .orElse(null);

        if (otpEntity == null) {
            return BaseResponse.error("Invalid OTP");
        }

        if (otpEntity.isExpired()) {
            return BaseResponse.error("OTP has expired");
        }

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);

        // Mark OTP as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        return BaseResponse.success("Password has been reset successfully");
    }

    @Override
    public BaseResponse<?> resetPassword(ResetPasswordRequest request) {
        try {
            // Verify the token
            String email = jwtService.extractUsername(request.getToken());
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate token
            if (!jwtService.isTokenValid(request.getToken(), user)) {
                return BaseResponse.error("Invalid or expired reset token");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);

            return BaseResponse.success("Password has been reset successfully");
        } catch (Exception e) {
            return BaseResponse.error("Failed to reset password: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> changePassword(ChangePasswordRequest request) {
        try {
            // Lấy email từ token
            String email = jwtService.extractUsername(request.getToken());
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Kiểm tra mật khẩu hiện tại nếu không phải yêu cầu đổi mật khẩu bắt buộc
            if (!user.isRequirePasswordChange()) {
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    return BaseResponse.error("Current password is incorrect");
                }
            }

            // Kiểm tra mật khẩu mới không được trùng mật khẩu cũ
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                return BaseResponse.error("New password must be different from current password");
            }

            // Cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setRequirePasswordChange(false); // Đánh dấu đã đổi mật khẩu
            repository.save(user);

            return BaseResponse.success("Password changed successfully");
        } catch (Exception e) {
            return BaseResponse.error("Failed to change password: " + e.getMessage());
        }
    }

    private void sendVerificationEmail(String email, String token) {
        String verificationLink = "http://your-frontend-url/verify-email?email=" + email + "&token=" + token;
        String emailContent = String.format("""
            <html>
                <body>
                    <h2>Verify Your Email</h2>
                    <p>Thank you for registering. Please click the link below to verify your email:</p>
                    <a href="%s">Verify Email</a>
                    <p>If you did not create an account, please ignore this email.</p>
                </body>
            </html>
        """, verificationLink);

        emailService.sendVerificationEmail(email, emailContent);
    }

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
