import { useEffect, useState } from 'react'
import { apiClient } from '@/lib/api'
import { useAuth } from './useAuth'

export function useUserVehicleInfo() {
  const { user } = useAuth()
  const [vehicleInfo, setVehicleInfo] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!user?.id) {
      setVehicleInfo(null)
      setLoading(false)
      return
    }
    setLoading(true)
    apiClient
      .get('/v1/user-vehicle/info')
      .then(setVehicleInfo)
      .catch((err) => {
        // Suppress 403/401 errors during initial load to prevent noise
        if (err.message && (err.message.includes('403') || err.message.includes('401'))) {
          return;
        }
        setError(err);
      })
      .finally(() => setLoading(false))
  }, [user?.id])

  return { vehicleInfo, loading, error }
}
