package capstone.demo.features.admin.service;

import capstone.demo.dto.response.BaseResponse;
import capstone.demo.features.admin.dto.PasswordPolicyRequest;
import capstone.demo.features.admin.dto.RolePermissionRequest;

public interface IAdminService {
    BaseResponse<?> updateRolePermissions(RolePermissionRequest request);
    BaseResponse<?> updatePasswordPolicy(PasswordPolicyRequest request);
    BaseResponse<?> getPasswordPolicy();
    BaseResponse<?> getRolePermissions();
}
