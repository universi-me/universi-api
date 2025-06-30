package me.universi.health.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class UsageResponseDTO {
    public @NotNull double systemCpuLoad;
    public @NotNull long memoryMax;
    public @NotNull long totalMemory;
    public @NotNull long freeMemory;
}
