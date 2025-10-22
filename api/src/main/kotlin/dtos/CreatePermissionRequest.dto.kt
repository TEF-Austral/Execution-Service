package api.dtos

import api.entities.PermissionType

data class CreatePermissionRequestDTO(
    val userId: String,
    val snippetId: Long,
    val permission: PermissionType,
)
