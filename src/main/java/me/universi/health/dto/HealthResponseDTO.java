package me.universi.health.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class HealthResponseDTO {
    private @NotNull boolean up;
    private @NotNull String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Nullable String message;

    public HealthResponseDTO(@NotNull boolean up, @NotNull String name, @Nullable String message) {
        this.up = up;
        this.name = name;
        this.message = message;
    }

    public boolean isUp() { return up; }
    public String getName() { return name; }
    public String getMessage() { return message; }
}
