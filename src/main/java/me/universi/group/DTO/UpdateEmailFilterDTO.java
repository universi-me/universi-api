package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateEmailFilterDTO(
    @NotNull( message = "O parâmetro 'groupId' não foi informado" )
    @NotBlank( message = "O parâmetro 'groupId' não pode estar vazio" )
    String groupId,

    @NotNull( message = "O parâmetro 'groupEmailFilterId' não foi informado" )
    @NotBlank( message = "O parâmetro 'groupEmailFilterId' não pode estar vazio" )
    UUID groupEmailFilterId,

    @NotNull( message = "O parâmetro 'email' não foi informado" )
    @NotBlank( message = "O parâmetro 'email' não pode estar vazio" )
    String email,

    @NotNull( message = "O parâmetro 'enabled' não foi informado" )
    Boolean enabled,

    @NotNull( message = "O parâmetro 'type' não foi informado" )
    @NotBlank( message = "O parâmetro 'type' não pode estar vazio" )
    String type
) { }
