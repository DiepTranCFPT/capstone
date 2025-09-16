package capstone.demo.utils;

import capstone.demo.entity.PasswordPolicy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    public List<String> validatePassword(String password, PasswordPolicy policy) {
        List<String> errors = new ArrayList<>();

        if (password.length() < policy.getMinLength()) {
            errors.add("Mật khẩu phải có ít nhất " + policy.getMinLength() + " ký tự");
        }

        // Kiểm tra chữ hoa
        if (policy.isRequireUpperCase() && !Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất một chữ cái viết hoa");
        }

        // Kiểm tra chữ thường
        if (policy.isRequireLowerCase() && !Pattern.compile("[a-z]").matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất một chữ cái viết thường");
        }

        // Kiểm tra số
        if (policy.isRequireNumbers() && !Pattern.compile("\\d").matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất một chữ số");
        }

        // Kiểm tra ký tự đặc biệt
        if (policy.isRequireSpecialChars() && !Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất một ký tự đặc biệt");
        }

        return errors;
    }
}
