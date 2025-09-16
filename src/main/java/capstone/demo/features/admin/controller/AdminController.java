package capstone.demo.features.admin.controller;

import capstone.demo.dto.response.BaseResponse;
import capstone.demo.features.admin.dto.PasswordPolicyRequest;
import capstone.demo.features.admin.dto.RolePermissionRequest;
import capstone.demo.features.admin.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "API quản trị hệ thống - Yêu cầu ADMIN role")
public class AdminController {
    private final IAdminService adminService;

    @Operation(
        summary = "Cập nhật quyền cho role",
        description = "Cập nhật danh sách quyền cho một role cụ thể. Yêu cầu ADMIN role."
    )
    @PutMapping("/role-permissions")
    public BaseResponse<?> updateRolePermissions(@RequestBody RolePermissionRequest request) {
        return adminService.updateRolePermissions(request);
    }

    @Operation(
        summary = "Cập nhật chính sách mật khẩu",
        description = "Cập nhật các yêu cầu về độ phức tạp của mật khẩu. Yêu cầu ADMIN role."
    )
    @PutMapping("/password-policy")
    public BaseResponse<?> updatePasswordPolicy(@RequestBody PasswordPolicyRequest request) {
        return adminService.updatePasswordPolicy(request);
    }

    @Operation(
        summary = "Lấy chính sách mật khẩu hiện tại",
        description = "Xem các cài đặt về yêu cầu mật khẩu hiện tại. Yêu cầu ADMIN role."
    )
    @GetMapping("/password-policy")
    public BaseResponse<?> getPasswordPolicy() {
        return adminService.getPasswordPolicy();
    }

    @Operation(
        summary = "Lấy danh sách quyền theo role",
        description = "Xem danh sách quyền được cấp cho từng role. Yêu cầu ADMIN role."
    )
    @GetMapping("/role-permissions")
    public BaseResponse<?> getRolePermissions() {
        return adminService.getRolePermissions();
    }
}
