import { 
  RouteDto, 
  JourneyStateDto, 
  StartJourneyRequest,
  CoordinateUpdateDto 
} from '@/types';
import { mqttClient, JourneyEvent } from './mqtt-client';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8082';

// Transport type for real-time updates
export type TransportType = 'sse' | 'mqtt';

class WorldViewApi {
  private baseUrl: string;
  private defaultTransport: TransportType;

  constructor(baseUrl: string = API_BASE_URL, transport: TransportType = 'sse') {
    this.baseUrl = baseUrl;
    this.defaultTransport = transport;
  }

  // ==================== Route Endpoints ====================

  /**
   * Get all available routes
   */
  async getAllRoutes(): Promise<RouteDto[]> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes`);
    if (!response.ok) {
      throw new Error(`Failed to fetch routes: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Get a specific route by ID
   */
  async getRouteById(routeId: string): Promise<RouteDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes/${routeId}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch route: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Get a random route
   */
  async getRandomRoute(): Promise<RouteDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes/random`);
    if (!response.ok) {
      throw new Error(`Failed to fetch random route: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Get total route count
   */
  async getRouteCount(): Promise<number> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes/count`);
    if (!response.ok) {
      throw new Error(`Failed to fetch route count: ${response.statusText}`);
    }
    return response.json();
  }

  // ==================== Journey Endpoints ====================

  /**
   * Start a new journey
   */
  async startJourney(request: StartJourneyRequest): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      throw new Error(`Failed to start journey: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Quick start a journey with random route
   */
  async quickStartJourney(): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/quick-start`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) {
      throw new Error(`Failed to quick start journey: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Get journey state
   */
  async getJourneyState(journeyId: string): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/${journeyId}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch journey state: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Pause a journey
   */
  async pauseJourney(journeyId: string): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/${journeyId}/pause`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error(`Failed to pause journey: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Resume a paused journey
   */
  async resumeJourney(journeyId: string): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/${journeyId}/resume`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error(`Failed to resume journey: ${response.statusText}`);
    }
    return response.json();
  }

  /**
   * Stop a journey
   */
  async stopJourney(journeyId: string): Promise<void> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/${journeyId}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error(`Failed to stop journey: ${response.statusText}`);
    }
  }

  /**
   * Subscribe to journey coordinate updates via SSE
   */
  subscribeToJourneySSE(
    journeyId: string, 
    onUpdate: (update: CoordinateUpdateDto) => void,
    onError?: (error: Event) => void
  ): EventSource {
    const eventSource = new EventSource(
      `${this.baseUrl}/api/v1/journeys/${journeyId}/stream`
    );

    // Listen for named events (coordinate-update, journey-started, journey-completed)
    eventSource.addEventListener('coordinate-update', (event: MessageEvent) => {
      try {
        const data: CoordinateUpdateDto = JSON.parse(event.data);
        console.log('Received coordinate update:', data);
        onUpdate(data);
      } catch (error) {
        console.error('Failed to parse SSE coordinate-update data:', error);
      }
    });

    eventSource.addEventListener('journey-started', (event: MessageEvent) => {
      try {
        const data: CoordinateUpdateDto = JSON.parse(event.data);
        console.log('Journey started event:', data);
        onUpdate(data);
      } catch (error) {
        console.error('Failed to parse SSE journey-started data:', error);
      }
    });

    eventSource.addEventListener('journey-completed', (event: MessageEvent) => {
      try {
        const data: CoordinateUpdateDto = JSON.parse(event.data);
        console.log('Journey completed event:', data);
        onUpdate(data);
      } catch (error) {
        console.error('Failed to parse SSE journey-completed data:', error);
      }
    });

    // Fallback for unnamed messages (shouldn't happen but just in case)
    eventSource.onmessage = (event) => {
      try {
        const data: CoordinateUpdateDto = JSON.parse(event.data);
        console.log('Received SSE message:', data);
        onUpdate(data);
      } catch (error) {
        console.error('Failed to parse SSE data:', error);
      }
    };

    eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      if (onError) {
        onError(error);
      }
    };

    return eventSource;
  }

  /**
   * Subscribe to journey coordinate updates via MQTT
   * Uses RabbitMQ WebSocket MQTT for real-time updates
   */
  async subscribeToJourneyMQTT(
    journeyId: string,
    onUpdate: (update: CoordinateUpdateDto) => void,
    onEvent?: (event: JourneyEvent) => void
  ): Promise<void> {
    await mqttClient.subscribeToJourney(journeyId, onUpdate, onEvent);
  }

  /**
   * Unsubscribe from journey MQTT updates
   */
  async unsubscribeFromJourneyMQTT(journeyId: string): Promise<void> {
    await mqttClient.unsubscribeFromJourney(journeyId);
  }

  /**
   * Subscribe to journey updates using the configured transport (SSE or MQTT)
   * Returns a cleanup function
   */
  subscribeToJourney(
    journeyId: string, 
    onUpdate: (update: CoordinateUpdateDto) => void,
    options?: {
      transport?: TransportType;
      onError?: (error: Event | Error) => void;
      onEvent?: (event: JourneyEvent) => void;
    }
  ): () => void {
    const transport = options?.transport || this.defaultTransport;

    if (transport === 'mqtt') {
      // MQTT subscription
      this.subscribeToJourneyMQTT(journeyId, onUpdate, options?.onEvent)
        .catch((error) => {
          console.error('MQTT subscription failed, falling back to SSE:', error);
          // Fallback to SSE if MQTT fails
          this.subscribeToJourneySSE(journeyId, onUpdate, options?.onError as ((error: Event) => void) | undefined);
        });

      return () => {
        this.unsubscribeFromJourneyMQTT(journeyId);
      };
    } else {
      // SSE subscription (default)
      const eventSource = this.subscribeToJourneySSE(journeyId, onUpdate, options?.onError as ((error: Event) => void) | undefined);
      
      return () => {
        eventSource.close();
      };
    }
  }

  /**
   * Disconnect MQTT client
   */
  disconnectMQTT(): void {
    mqttClient.disconnect();
  }
}

// Export singleton instance
export const worldViewApi = new WorldViewApi();

// Export class for custom instances
export { WorldViewApi };
