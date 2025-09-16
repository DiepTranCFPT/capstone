package capstone.demo.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request xác thực OTP và đặt mật khẩu mới")
public class VerifyOtpRequest {
    @Schema(description = "Email đang reset mật khẩu",
            example = "user@example.com",
            required = true)
    private String email;

    @Schema(description = "Mã OTP nhận được từ email",
            example = "123456",
            required = true)
    private String otp;

    @Schema(description = "Mật khẩu mới",
            example = "NewPassword123!",
            required = true)
    private String newPassword;
}
