package capstone.demo.entity;

import capstone.demo.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private String password;

    @Builder.Default
    private boolean emailVerified = false;

    private String verificationToken;

    @Builder.Default
    private boolean requirePasswordChange = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private int failedLoginAttempts = 0;

    @Builder.Default
    private boolean accountLocked = false;

    private LocalDateTime lockTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (accountLocked && lockTime != null) {
            // Tự động mở khóa sau 24 giờ
            if (LocalDateTime.now().isAfter(lockTime.plusHours(24))) {
                accountLocked = false;
                failedLoginAttempts = 0;
                lockTime = null;
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }
}
