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
@Schema(description = "Request quên mật khẩu")
public class ForgotPasswordRequest {
    @Schema(description = "Email cần khôi phục mật khẩu",
            example = "user@example.com",
            required = true)
    private String email;
}
