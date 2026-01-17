'use client'

const VEHICLE_SERVICE_URL = process.env.NEXT_PUBLIC_VEHICLE_SERVICE_URL || 'http://localhost:8081'

class VehicleServiceApi {
  constructor(baseUrl = VEHICLE_SERVICE_URL) {
    this.baseUrl = baseUrl
  }

  /**
   * Get all available vehicles
   */
  async getAllVehicles() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/vehicles?size=100`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        signal: AbortSignal.timeout(5000), // 5 second timeout
      })
      
      if (!res.ok) {
        throw new Error(`Failed to fetch vehicles: ${res.status} ${res.statusText}`)
      }
      
      const data = await res.json()
      return data.vehicles || []
    } catch (error) {
      console.error('Error fetching vehicles:', error)
      
      // Provide more descriptive error messages
      if (error.name === 'AbortError') {
        throw new Error('Request timeout: Vehicle service is not responding')
      } else if (error.name === 'TypeError' && error.message.includes('fetch')) {
        throw new Error('Network error: Cannot connect to vehicle service. Please check if the service is running on port 8081.')
      }
      
      throw error
    }
  }

  /**
   * Get configuration options for a specific vehicle
   */
  async getVehicleConfiguration(vehicleId) {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/vehicles/${vehicleId}/configuration`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        signal: AbortSignal.timeout(5000), // 5 second timeout
      })
      
      if (!res.ok) {
        if (res.status === 404) {
          throw new Error(`Vehicle with ID ${vehicleId} not found`)
        }
        throw new Error(`Failed to fetch configuration: ${res.status} ${res.statusText}`)
      }
      
      return await res.json()
    } catch (error) {
      console.error('Error fetching vehicle configuration:', error)
      
      // Provide more descriptive error messages
      if (error.name === 'AbortError') {
        throw new Error('Request timeout: Vehicle service is not responding')
      } else if (error.name === 'TypeError' && error.message.includes('fetch')) {
        throw new Error('Network error: Cannot connect to vehicle service')
      }
      
      throw error
    }
  }

  /**
   * Check if vehicle service is available
   * Tries to fetch vehicles as a health check (more reliable than actuator)
   */
  async checkHealth() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/vehicles?size=1`, {
        method: 'GET',
        signal: AbortSignal.timeout(3000), // 3 second timeout
      })
      return res.ok || res.status === 200
    } catch (error) {
      return false
    }
  }
}

export const vehicleServiceApi = new VehicleServiceApi()
