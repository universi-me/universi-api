package me.universi.health.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class HealthResponseDTO {
    private @NotNull boolean up;
    private @NotNull boolean disabled;
    private @NotNull String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Nullable String statusMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Nullable String exceptionMessage;

    public HealthResponseDTO(@NotNull boolean up, @NotNull boolean disabled, @NotNull String name, @Nullable String statusMessage, @Nullable String exceptionMessage) {
        this.up = up;
        this.disabled = disabled;
        this.name = name;
        this.statusMessage = statusMessage;
        this.exceptionMessage = exceptionMessage;
    }

    public boolean isUp() { return up; }
    public boolean isDisabled() { return disabled; }
    public String getName() { return name; }
    public String getStatusMessage() { return statusMessage; }
    public String getExceptionMessage() { return exceptionMessage; }
}
