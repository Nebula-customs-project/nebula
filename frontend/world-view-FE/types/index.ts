// API Types matching the backend DTOs
export interface CoordinateDto {
  latitude: number;
  longitude: number;
}

export interface RouteDto {
  id: string;
  name: string;
  description: string;
  start_point: CoordinateDto;
  end_point: CoordinateDto;
  waypoints: CoordinateDto[];
  total_distance_meters: number;
  estimated_duration_seconds: number;
  total_waypoints: number;
}

export interface JourneyStateDto {
  journey_id: string;
  route: RouteDto;
  current_position: CoordinateDto;
  current_waypoint_index: number;
  status: JourneyStatus;
  speed_meters_per_second: number;
  progress_percentage: number;
}

export interface CoordinateUpdateDto {
  journey_id: string;
  coordinate: CoordinateDto;
  progress_percentage: number;
  status: JourneyStatus;
  current_waypoint_index: number;
  total_waypoints: number;
  timestamp: string;
}

export interface StartJourneyRequest {
  journey_id: string;
  route_id?: string;
  speed_meters_per_second: number;
}

export type JourneyStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'PAUSED' | 'COMPLETED';

// Frontend-specific types
export interface MapPosition {
  lat: number;
  lng: number;
}

export interface JourneyInfo {
  journeyId: string;
  routeName: string;
  status: JourneyStatus;
  progress: number;
  currentPosition: MapPosition;
  destination: MapPosition;
  startPoint: MapPosition;
  waypoints: MapPosition[];
  distanceRemaining: number;
  estimatedTimeRemaining: number;
  speedKmh: number;
}
