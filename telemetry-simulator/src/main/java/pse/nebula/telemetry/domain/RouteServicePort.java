package pse.nebula.telemetry.domain;

public interface RouteServicePort {
    Route getRoute(GeoPoint start, GeoPoint end);
}

