package capstone.demo.features.admin.dto;

import capstone.demo.entity.enums.Permission;
import capstone.demo.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionRequest {
    private Role role;
    private Set<Permission> permissions;
}
