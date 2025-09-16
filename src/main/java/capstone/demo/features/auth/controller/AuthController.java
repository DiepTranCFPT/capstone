package capstone.demo.features.auth.controller;

import capstone.demo.features.auth.dto.*;
import capstone.demo.features.auth.service.IAuthenticationService;
import capstone.demo.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {
    private final IAuthenticationService authenticationService;

    @Operation(
        summary = "Đăng ký tài khoản mới",
        description = "Đăng ký tài khoản mới và gửi email xác thực. User cần xác thực email trước khi có thể đăng nhập."
    )
    @ApiResponse(responseCode = "200", description = "Đăng ký thành công, check email để xác thực")
    @ApiResponse(responseCode = "400", description = "Email đã tồn tại hoặc thông tin không hợp lệ")
    @PostMapping("/register")
    public BaseResponse<AuthenticationResponse> register(
        @RequestBody RegisterRequest request
    ) {
        return BaseResponse.success(authenticationService.register(request));
    }

    @Operation(
        summary = "Đăng nhập",
        description = "Đăng nhập vào hệ thống. Lưu ý:\n" +
                     "- Email phải được xác thực trước\n" +
                     "- Tài khoản sẽ bị khóa sau 5 lần đăng nhập sai\n" +
                     "- Nếu đăng nhập bằng Google lần đầu, bắt buộc đổi mật khẩu"
    )
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công, trả về JWT token")
    @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập")
    @ApiResponse(responseCode = "403", description = "Tài khoản bị khóa hoặc chưa xác thực email")
    @PostMapping("/authenticate")
    public BaseResponse<AuthenticationResponse> authenticate(
        @RequestBody AuthenticationRequest request
    ) {
        return BaseResponse.success(authenticationService.authenticate(request));
    }

    @Operation(
        summary = "Gửi yêu cầu quên mật khẩu",
        description = "Gửi mã OTP về email để reset mật khẩu. OTP có hiệu lực trong 5 phút."
    )
    @PostMapping("/forgot-password")
    public BaseResponse<?> forgotPassword(
        @RequestBody ForgotPasswordRequest request
    ) {
        return authenticationService.forgotPassword(request);
    }

    @Operation(
        summary = "Xác thực email",
        description = "Xác thực email sau khi đăng ký tài khoản"
    )
    @PostMapping("/verify-email")
    public BaseResponse<?> verifyEmail(
        @RequestBody VerifyEmailRequest request
    ) {
        return authenticationService.verifyEmail(request);
    }

    @Operation(
        summary = "Xác thực OTP và đổi mật khẩu",
        description = "Xác thực OTP và đổi mật khẩu mới khi quên mật khẩu"
    )
    @PostMapping("/verify-otp")
    public BaseResponse<?> verifyOtp(
        @RequestBody VerifyOtpRequest request
    ) {
        return authenticationService.verifyOtp(request);
    }

    @Operation(
        summary = "Đổi mật khẩu",
        description = "Đổi mật khẩu khi đã đăng nhập. Bắt buộc với tài khoản đăng nhập Google lần đầu."
    )
    @PostMapping("/change-password")
    public BaseResponse<?> changePassword(
        @Parameter(description = "JWT token", required = true)
        @RequestHeader("Authorization") String token,
        @RequestBody ChangePasswordRequest request
    ) {
        String jwtToken = token.substring(7);
        request.setToken(jwtToken);
        return authenticationService.changePassword(request);
    }
}
