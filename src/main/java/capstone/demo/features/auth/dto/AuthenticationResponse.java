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
@Schema(description = "Response khi đăng nhập/đăng ký thành công")
public class AuthenticationResponse {
    @Schema(description = "JWT token để xác thực các request tiếp theo",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    private String token;

    @Schema(description = "Yêu cầu đổi mật khẩu (true nếu là tài khoản Google mới)",
            example = "false")
    private boolean requirePasswordChange;
}
