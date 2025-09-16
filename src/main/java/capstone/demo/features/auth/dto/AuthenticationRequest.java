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
@Schema(description = "Request đăng nhập")
public class AuthenticationRequest {
    @Schema(description = "Email đăng nhập", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Mật khẩu", example = "Password123!", required = true)
    private String password;
}
