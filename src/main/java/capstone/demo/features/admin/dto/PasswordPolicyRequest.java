package capstone.demo.features.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordPolicyRequest {
    private int minLength;
    private boolean requireUpperCase;
    private boolean requireLowerCase;
    private boolean requireNumbers;
    private boolean requireSpecialChars;
    private int passwordExpiryDays;
    private int maxPasswordHistory;
}
