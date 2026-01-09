package pse.nebula.worldview.infrastructure.adapter.outbound.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.CoordinateUpdateDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MqttCoordinatePublisherAdapter.
 * Note: Full integration testing with actual MQTT broker is done in integration tests.
 */
@DisplayName("MqttCoordinatePublisherAdapter Tests")
class MqttCoordinatePublisherAdapterTest {

    private JourneyState testJourneyState;
    private DrivingRoute testRoute;

    @BeforeEach
    void setUp() {
        List<Coordinate> waypoints = Arrays.asList(
                new Coordinate(48.8973, 9.1920),
                new Coordinate(48.8354, 9.1520)
        );

        testRoute = new DrivingRoute(
                "route-1",
                "Test Route",
                "Test description",
                waypoints,
                5000.0,
                600
        );

        testJourneyState = new JourneyState("journey-1", testRoute, 13.89);
        testJourneyState.start();
    }

    @Nested
    @DisplayName("JourneyEventMessage Tests")
    class JourneyEventMessageTests {

        @Test
        @DisplayName("Should create journey event message with STARTED type")
        void shouldCreateJourneyEventMessageWithStartedType() {
            CoordinateUpdateDto updateDto = CoordinateUpdateDto.builder()
                    .journeyId("journey-1")
                    .build();

            MqttCoordinatePublisherAdapter.JourneyEventMessage message =
                    new MqttCoordinatePublisherAdapter.JourneyEventMessage("STARTED", updateDto);

            assertEquals("STARTED", message.eventType());
            assertEquals(updateDto, message.data());
        }

        @Test
        @DisplayName("Should create journey event message with COMPLETED type")
        void shouldCreateJourneyEventMessageWithCompletedType() {
            CoordinateUpdateDto updateDto = CoordinateUpdateDto.builder()
                    .journeyId("journey-1")
                    .status("COMPLETED")
                    .build();

            MqttCoordinatePublisherAdapter.JourneyEventMessage message =
                    new MqttCoordinatePublisherAdapter.JourneyEventMessage("COMPLETED", updateDto);

            assertEquals("COMPLETED", message.eventType());
            assertEquals("journey-1", message.data().getJourneyId());
        }

        @Test
        @DisplayName("JourneyEventMessage record should support equals and hashCode")
        void journeyEventMessageShouldSupportEqualsAndHashCode() {
            CoordinateUpdateDto updateDto = CoordinateUpdateDto.builder()
                    .journeyId("journey-1")
                    .build();

            MqttCoordinatePublisherAdapter.JourneyEventMessage message1 =
                    new MqttCoordinatePublisherAdapter.JourneyEventMessage("STARTED", updateDto);
            MqttCoordinatePublisherAdapter.JourneyEventMessage message2 =
                    new MqttCoordinatePublisherAdapter.JourneyEventMessage("STARTED", updateDto);

            assertEquals(message1, message2);
            assertEquals(message1.hashCode(), message2.hashCode());
        }
    }
}