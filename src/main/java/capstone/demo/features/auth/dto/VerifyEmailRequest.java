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
@Schema(description = "Request xác thực email")
public class VerifyEmailRequest {
    @Schema(description = "Email cần xác thực",
            required = true)
    private String email;

    @Schema(description = "Token xác thực nhận được từ email",
            required = true)
    private String token;
}
