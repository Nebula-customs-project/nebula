'use client'

import { API_BASE_URL } from './config'
import { mqttClient } from './mqtt-client'

class WorldDriveApi {
  constructor(baseUrl = API_BASE_URL) {
    if (!baseUrl) {
      throw new Error('API_BASE_URL is not defined in the environment variables')
    }
    this.baseUrl = baseUrl
  }

  async getAllRoutes() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/routes`)
      if (!res.ok) {
        throw new Error(`Failed to fetch routes: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.error('Error fetching all routes:', error)
      throw error
    }
  }

  async getCurrentJourney() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/journeys/current`)
      if (res.status === 204) {
        return null // No active journey
      }
      if (!res.ok) {
        throw new Error(`Failed to fetch current journey: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.error('Error fetching current journey:', error)
      throw error
    }
  }

  async isJourneyActive() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/journeys/active`)
      if (!res.ok) {
        throw new Error(`Failed to check journey status: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.error('Error checking journey status:', error)
      throw error
    }
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
