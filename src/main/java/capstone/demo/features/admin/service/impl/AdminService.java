package capstone.demo.features.admin.service.impl;

import capstone.demo.dto.response.BaseResponse;
import capstone.demo.entity.PasswordPolicy;
import capstone.demo.entity.enums.Role;
import capstone.demo.features.admin.dto.PasswordPolicyRequest;
import capstone.demo.features.admin.dto.RolePermissionRequest;
import capstone.demo.features.admin.repository.PasswordPolicyRepository;
import capstone.demo.features.admin.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final PasswordPolicyRepository passwordPolicyRepository;

    @Override
    public BaseResponse<?> updateRolePermissions(RolePermissionRequest request) {
        Role role = request.getRole();
        role.setPermissions(request.getPermissions());
        return BaseResponse.success("Role permissions updated successfully");
    }

    @Override
    public BaseResponse<?> updatePasswordPolicy(PasswordPolicyRequest request) {
        PasswordPolicy policy = passwordPolicyRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new PasswordPolicy());

        policy.setMinLength(request.getMinLength());
        policy.setRequireUpperCase(request.isRequireUpperCase());
        policy.setRequireLowerCase(request.isRequireLowerCase());
        policy.setRequireNumbers(request.isRequireNumbers());
        policy.setRequireSpecialChars(request.isRequireSpecialChars());
        policy.setPasswordExpiryDays(request.getPasswordExpiryDays());
        policy.setMaxPasswordHistory(request.getMaxPasswordHistory());

        passwordPolicyRepository.save(policy);
        return BaseResponse.success("Password policy updated successfully");
    }

    @Override
    public BaseResponse<?> getPasswordPolicy() {
        PasswordPolicy policy = passwordPolicyRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new PasswordPolicy());
        return BaseResponse.success(policy);
    }

    @Override
    public BaseResponse<?> getRolePermissions() {
        Map<Role, Object> rolePermissions = new HashMap<>();
        Arrays.stream(Role.values())
                .forEach(role -> rolePermissions.put(role, role.getPermissions()));
        return BaseResponse.success(rolePermissions);
    }
}
