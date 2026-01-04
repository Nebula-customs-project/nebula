package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a geographic coordinate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GPS coordinate (latitude and longitude)")
public class CoordinateDto {

    @JsonProperty("latitude")
    @Schema(description = "Latitude in decimal degrees", example = "48.8354")
    private double latitude;

    @JsonProperty("longitude")
    @Schema(description = "Longitude in decimal degrees", example = "9.1520")
    private double longitude;
}