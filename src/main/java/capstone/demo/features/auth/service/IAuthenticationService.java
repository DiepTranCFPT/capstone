package capstone.demo.features.auth.service;

import capstone.demo.dto.response.BaseResponse;
import capstone.demo.features.auth.dto.*;


public interface IAuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    BaseResponse<?> forgotPassword(ForgotPasswordRequest request);
    BaseResponse<?> verifyOtp(VerifyOtpRequest request);
    BaseResponse<?> verifyEmail(VerifyEmailRequest request);
    BaseResponse<?> resetPassword(ResetPasswordRequest request);
    BaseResponse<?> changePassword(ChangePasswordRequest request);
}
