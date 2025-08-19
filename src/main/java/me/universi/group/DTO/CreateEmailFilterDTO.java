package me.universi.group.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.group.enums.GroupEmailFilterType;

@Schema( description = "Request body for creating a new GroupEmailFilter" )
public record CreateEmailFilterDTO(
    @NotBlank
    @Schema( description = "The Group ID or path this filter will be applied. It must be an organization" )
    String groupId,

    @NotBlank
    @Schema( description = "The filter declared" )
    String email,

    @NotNull
    @Schema( description = "If false, this filter will not be applied" )
    Boolean enabled,

    @NotBlank
    @Schema( implementation = GroupEmailFilterType.class )
    String type
) { }
