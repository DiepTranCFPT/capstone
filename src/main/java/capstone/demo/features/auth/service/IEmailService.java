package capstone.demo.features.auth.service;

public interface IEmailService {
    void sendOtpEmail(String to, String otp);
    void sendVerificationEmail(String to, String content);
}
