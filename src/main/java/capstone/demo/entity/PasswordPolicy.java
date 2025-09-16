package capstone.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_policy")
public class PasswordPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minLength = 8;
    private boolean requireUpperCase = true;
    private boolean requireLowerCase = true;
    private boolean requireNumbers = true;
    private boolean requireSpecialChars = true;
    private int passwordExpiryDays = 90;
    private int maxPasswordHistory = 3;
}
