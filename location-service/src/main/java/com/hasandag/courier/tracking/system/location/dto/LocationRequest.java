package com.hasandag.courier.tracking.system.location.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request object for recording courier location")
public class LocationRequest {

    @Schema(description = "Unique identifier of the courier", example = "courier-123", required = true)
    @NotBlank(message = "Courier ID is required")
    private String courierId;

    @Schema(description = "Latitude coordinate of the courier's location", example = "41.0082", required = true)
    @NotNull(message = "Latitude is required")
    private Double latitude;

    @Schema(description = "Longitude coordinate of the courier's location", example = "28.9784", required = true)
    @NotNull(message = "Longitude is required")
    private Double longitude;
}