import mqtt, { MqttClient, IClientOptions } from 'mqtt';
import { CoordinateUpdateDto } from '@/types';

// MQTT broker URL (RabbitMQ Web MQTT via WebSocket)
const MQTT_BROKER_URL = process.env.NEXT_PUBLIC_MQTT_URL || 'ws://localhost:15675/ws';
const MQTT_USERNAME = process.env.NEXT_PUBLIC_MQTT_USERNAME || 'nebula_user';
const MQTT_PASSWORD = process.env.NEXT_PUBLIC_MQTT_PASSWORD || 'nebula@2025';

/**
 * MQTT client for subscribing to journey coordinate updates.
 * Uses RabbitMQ's Web MQTT plugin via WebSocket.
 * 
 * Topic structure:
 * - nebula/journey/{journeyId}/position - Real-time coordinate updates
 * - nebula/journey/{journeyId}/events - Journey lifecycle events
 */
class MqttJourneyClient {
  private client: MqttClient | null = null;
  private readonly subscriptions: Map<string, (update: CoordinateUpdateDto) => void> = new Map();
  private readonly eventSubscriptions: Map<string, (event: JourneyEvent) => void> = new Map();
  private isConnected: boolean = false;
  private connectionPromise: Promise<void> | null = null;

  /**
   * Connect to the MQTT broker
   */
  async connect(): Promise<void> {
    if (this.isConnected && this.client) {
      return;
    }

    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    this.connectionPromise = new Promise((resolve, reject) => {
      const options: IClientOptions = {
        clientId: `world-view-fe-${Math.random().toString(16).slice(2, 10)}`,
        username: MQTT_USERNAME,
        password: MQTT_PASSWORD,
        clean: true,
        reconnectPeriod: 5000,
        connectTimeout: 10000,
      };

      console.log(`Connecting to MQTT broker at ${MQTT_BROKER_URL}`);

      this.client = mqtt.connect(MQTT_BROKER_URL, options);

      this.client.on('connect', () => {
        console.log('Connected to MQTT broker');
        this.isConnected = true;
        this.connectionPromise = null;
        resolve();
      });

      this.client.on('error', (error: Error) => {
        console.error('MQTT connection error:', error);
        this.isConnected = false;
        this.connectionPromise = null;
        reject(error);
      });

      this.client.on('close', () => {
        console.log('MQTT connection closed');
        this.isConnected = false;
      });

      this.client.on('reconnect', () => {
        console.log('Reconnecting to MQTT broker...');
      });

      this.client.on('message', (topic: string, message: Buffer) => {
        this.handleMessage(topic, message.toString());
      });
    });

    return this.connectionPromise;
  }

  /**
   * Subscribe to journey position updates
   */
  async subscribeToJourney(
    journeyId: string,
    onUpdate: (update: CoordinateUpdateDto) => void,
    onEvent?: (event: JourneyEvent) => void
  ): Promise<void> {
    await this.connect();

    if (!this.client) {
      throw new Error('MQTT client not connected');
    }

    const positionTopic = `nebula/journey/${journeyId}/position`;
    const eventsTopic = `nebula/journey/${journeyId}/events`;

    // Subscribe to position updates
    this.client.subscribe(positionTopic, { qos: 0 }, (err: Error | null) => {
      if (err) {
        console.error(`Failed to subscribe to ${positionTopic}:`, err);
      } else {
        console.log(`Subscribed to ${positionTopic}`);
        this.subscriptions.set(positionTopic, onUpdate);
      }
    });

    // Subscribe to events
    if (onEvent) {
      this.client.subscribe(eventsTopic, { qos: 1 }, (err: Error | null) => {
        if (err) {
          console.error(`Failed to subscribe to ${eventsTopic}:`, err);
        } else {
          console.log(`Subscribed to ${eventsTopic}`);
          this.eventSubscriptions.set(eventsTopic, onEvent);
        }
      });
    }
  }

  /**
   * Unsubscribe from journey updates
   */
  async unsubscribeFromJourney(journeyId: string): Promise<void> {
    if (!this.client) return;

    const positionTopic = `nebula/journey/${journeyId}/position`;
    const eventsTopic = `nebula/journey/${journeyId}/events`;

    this.client.unsubscribe([positionTopic, eventsTopic], (err: Error | undefined) => {
      if (err) {
        console.error(`Failed to unsubscribe:`, err);
      } else {
        console.log(`Unsubscribed from journey ${journeyId}`);
        this.subscriptions.delete(positionTopic);
        this.eventSubscriptions.delete(eventsTopic);
      }
    });
  }

  /**
   * Handle incoming MQTT messages
   */
  private handleMessage(topic: string, message: string): void {
    try {
      const data = JSON.parse(message);

      // Check if it's a position update
      if (topic.endsWith('/position')) {
        const callback = this.subscriptions.get(topic);
        if (callback) {
          callback(data as CoordinateUpdateDto);
        }
      }

      // Check if it's an event
      if (topic.endsWith('/events')) {
        const callback = this.eventSubscriptions.get(topic);
        if (callback) {
          callback(data as JourneyEvent);
        }
      }
    } catch (error) {
      console.error('Failed to parse MQTT message:', error);
    }
  }

  /**
   * Disconnect from MQTT broker
   */
  disconnect(): void {
    if (this.client) {
      this.client.end();
      this.client = null;
      this.isConnected = false;
      this.subscriptions.clear();
      this.eventSubscriptions.clear();
      console.log('Disconnected from MQTT broker');
    }
  }

  /**
   * Check if connected
   */
  get connected(): boolean {
    return this.isConnected;
  }
}

/**
 * Journey event message from MQTT
 */
export interface JourneyEvent {
  eventType: 'STARTED' | 'COMPLETED' | 'PAUSED' | 'RESUMED';
  data: CoordinateUpdateDto;
}

// Export singleton instance
export const mqttClient = new MqttJourneyClient();

// Export class for custom instances
export { MqttJourneyClient };
