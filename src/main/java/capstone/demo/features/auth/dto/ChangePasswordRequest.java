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
@Schema(description = "Request đổi mật khẩu")
public class ChangePasswordRequest {
    @Schema(description = "Mật khẩu hiện tại (Không bắt buộc nếu là tài khoản Google mới)",
            example = "OldPassword123!")
    private String currentPassword;

    @Schema(description = "Mật khẩu mới (Phải khác mật khẩu hiện tại)",
            example = "NewPassword123!",
            required = true)
    private String newPassword;

    @Schema(hidden = true)
    private String token;  // Set tự động từ JWT token
}
