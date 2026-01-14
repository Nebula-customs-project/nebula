'use client'

import mqtt from 'mqtt'
import { MQTT_BROKER_URL, MQTT_USERNAME, MQTT_PASSWORD } from './config'

class MqttJourneyClient {
  constructor() {
    this.client = null
    this.subscriptions = new Map()
    this.eventSubscriptions = new Map()
    this.isConnected = false
    this.connectionPromise = null
  }

  async connect() {
    if (this.isConnected && this.client) {
      return
    }

    if (this.connectionPromise) {
      return this.connectionPromise
    }

    this.connectionPromise = new Promise((resolve, reject) => {
      const options = {
        clientId: `neon-world-drive-${Math.random().toString(16).slice(2, 10)}`,
        username: MQTT_USERNAME,
        password: MQTT_PASSWORD,
        clean: true,
        reconnectPeriod: 5000,
        connectTimeout: 10000,
        keepalive: 30,
      }

      console.log(`Connecting to MQTT broker at ${MQTT_BROKER_URL}`)

      this.client = mqtt.connect(MQTT_BROKER_URL, options)

      this.client.on('connect', () => {
        console.log('Connected to MQTT broker')
        this.isConnected = true
        this.connectionPromise = null
        resolve()
      })

      this.client.on('error', (error) => {
        console.error('MQTT connection error:', error)
        this.isConnected = false
        this.connectionPromise = null
        reject(error)
      })

      this.client.on('close', () => {
        console.log('MQTT connection closed')
        this.isConnected = false
      })

      this.client.on('reconnect', () => {
        console.log('Reconnecting to MQTT broker...')
      })

      this.client.on('message', (topic, message) => {
        this.handleMessage(topic, message.toString())
      })
    })

    return this.connectionPromise
  }

  async subscribeToJourney(journeyId, onUpdate, onEvent) {
    await this.connect()

    if (!this.client) {
      throw new Error('MQTT client not connected')
    }

    const positionTopic = `nebula/journey/${journeyId}/position`
    const eventsTopic = `nebula/journey/${journeyId}/events`

    this.client.subscribe(positionTopic, { qos: 0 }, (err) => {
      if (err) {
        console.error(`Failed to subscribe to ${positionTopic}:`, err)
      } else {
        console.log(`Subscribed to ${positionTopic}`)
        this.subscriptions.set(positionTopic, onUpdate)
      }
    })

    if (onEvent) {
      this.client.subscribe(eventsTopic, { qos: 1 }, (err) => {
        if (err) {
          console.error(`Failed to subscribe to ${eventsTopic}:`, err)
        } else {
          console.log(`Subscribed to ${eventsTopic}`)
          this.eventSubscriptions.set(eventsTopic, onEvent)
        }
      })
    }
  }

  async unsubscribeFromJourney(journeyId) {
    if (!this.client) return

    const positionTopic = `nebula/journey/${journeyId}/position`
    const eventsTopic = `nebula/journey/${journeyId}/events`

    this.client.unsubscribe([positionTopic, eventsTopic], (err) => {
      if (err) {
        console.error(`Failed to unsubscribe:`, err)
      } else {
        console.log(`Unsubscribed from journey ${journeyId}`)
        this.subscriptions.delete(positionTopic)
        this.eventSubscriptions.delete(eventsTopic)
      }
    })
  }

  handleMessage(topic, message) {
    try {
      const data = JSON.parse(message)

      if (topic.endsWith('/position')) {
        const callback = this.subscriptions.get(topic)
        if (callback) {
          callback(data)
        }
      }

      if (topic.endsWith('/events')) {
        const callback = this.eventSubscriptions.get(topic)
        if (callback) {
          callback(data)
        }
      }
    } catch (error) {
      console.error('Failed to parse MQTT message:', error)
    }
  }

  disconnect() {
    if (this.client) {
      this.client.end()
      this.client = null
      this.isConnected = false
      this.subscriptions.clear()
      this.eventSubscriptions.clear()
      console.log('Disconnected from MQTT broker')
    }
  }

  get connected() {
    return this.isConnected
  }
}

export const mqttClient = new MqttJourneyClient()
export { MqttJourneyClient }
