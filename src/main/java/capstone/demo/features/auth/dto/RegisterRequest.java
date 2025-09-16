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
@Schema(description = "Request đăng ký tài khoản mới")
public class RegisterRequest {
    @Schema(description = "Email đăng ký", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Mật khẩu", example = "Password123!", required = true)
    private String password;

    @Schema(description = "Tên", example = "John", required = true)
    private String firstName;

    @Schema(description = "Họ", example = "Doe", required = true)
    private String lastName;
}
