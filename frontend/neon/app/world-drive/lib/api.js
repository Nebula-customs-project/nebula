'use client'

import { API_BASE_URL } from './config'
import { mqttClient } from './mqtt-client'

class WorldDriveApi {
  constructor(baseUrl = API_BASE_URL) {
    this.baseUrl = baseUrl
  }

  async getAllRoutes() {
    const res = await fetch(`${this.baseUrl}/api/v1/routes`)
    if (!res.ok) throw new Error('Failed to fetch routes')
    return res.json()
  }

  async getCurrentJourney() {
    const res = await fetch(`${this.baseUrl}/api/v1/journeys/current`)
    if (res.status === 204) {
      return null // No active journey
    }
    if (!res.ok) throw new Error('Failed to fetch current journey')
    return res.json()
  }

  async isJourneyActive() {
    const res = await fetch(`${this.baseUrl}/api/v1/journeys/active`)
    if (!res.ok) throw new Error('Failed to check journey status')
    return res.json()
  }

  subscribeToJourney(journeyId, onUpdate, options) {
    mqttClient
      .subscribeToJourney(journeyId, onUpdate, options?.onEvent)
      .catch((error) => {
        console.error('MQTT subscription failed:', error)
        if (options?.onError) {
          options.onError(error)
        }
      })

    return () => {
      mqttClient.unsubscribeFromJourney(journeyId)
    }
  }

  disconnect() {
    mqttClient.disconnect()
  }

  get isConnected() {
    return mqttClient.connected
  }
}

export const worldDriveApi = new WorldDriveApi()
