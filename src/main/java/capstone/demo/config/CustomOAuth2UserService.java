package capstone.demo.config;

import capstone.demo.entity.User;
import capstone.demo.entity.enums.Role;
import capstone.demo.features.auth.repository.UserRepository;
import capstone.demo.features.auth.service.IEmailService;
import capstone.demo.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final SpringTemplateEngine templateEngine;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            processOAuth2User(oauth2User);
        } catch (Exception e) {
            log.error("OAuth2 authentication error: ", e);
            throw new OAuth2AuthenticationException("Failed to process OAuth2 user");
        }

        return oauth2User;
    }

    private void processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");

        // Kiểm tra user đã tồn tại chưa
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        existingUser -> {
                            // User đã tồn tại - không làm gì cả
                            log.info("Existing user logged in via OAuth2: {}", email);
                        },
                        () -> {
                            // Tạo user mới
                            String randomPassword = PasswordGenerator.generateRandomPassword();
                            User newUser = createNewUser(email, firstName, lastName, randomPassword);
                            userRepository.save(newUser);

                            // Gửi email thông báo mật khẩu
                            sendTemporaryPasswordEmail(email, firstName, randomPassword);
                            log.info("Created new user via OAuth2: {}", email);
                        }
                );
    }

    private User createNewUser(String email, String firstName, String lastName, String password) {
        return User.builder()
                .email(email)
                .firstname(firstName)
                .lastname(lastName)
                .password(passwordEncoder.encode(password))
                .emailVerified(true)
                .role(Role.USER)
                .requirePasswordChange(true)
                .build();
    }

    private void sendTemporaryPasswordEmail(String email, String firstName, String password) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "firstName", firstName != null ? firstName : email.split("@")[0],
                    "password", password
            ));

            String emailContent = templateEngine.process("temporary-password", context);
            emailService.sendVerificationEmail(email, emailContent);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", email, e);
            throw new RuntimeException("Failed to send temporary password email");
        }
    }

    public User loadUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
