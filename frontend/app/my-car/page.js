'use client'

import { Car, Fuel, Navigation, MapPin } from 'lucide-react'
import Image from 'next/image'
import { useVehicleTelemetry } from '@/hooks/useVehicleTelemetry'
import { useUserVehicleInfo } from '@/hooks/useUserVehicleInfo'
import { useAuth } from '@/hooks/useAuth'
import dynamic from 'next/dynamic'

const VehicleMap = dynamic(() => import('./VehicleMap'), {
  ssr: false,
  loading: () => (
    <div className="w-full h-full bg-gray-900 animate-pulse flex items-center justify-center text-gray-500">
      Loading Map...
    </div>
  )
})

export default function MyCarPage() {
  const { user } = useAuth()
  // WebSocket: Real-time telemetry (vehicleName, location, fuel)
  const { telemetry, isConnected } = useVehicleTelemetry()
  // REST: Static vehicle info (vehicleName, vehicleImage, maintenanceDueDate, tyrePressures)
  const { vehicleInfo, loading } = useUserVehicleInfo()

  // Get vehicle name from REST first, then WebSocket, then fallback
  const vehicleName = vehicleInfo?.vehicleName || telemetry?.vehicleName || 'My Nebula Car'
  const vehicleImage = vehicleInfo?.vehicleImage || null
  const carLocation = telemetry?.carLocation || null
  const fuelLevel = telemetry?.fuelLevel || null

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">My Nebula Car</h1>

        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-gray-800 rounded-lg p-8">
            <h2 className="text-2xl font-bold mb-6">{vehicleName}</h2>
            <div className="aspect-video bg-gray-700 rounded-lg mb-6 flex items-center justify-center overflow-hidden">
              {vehicleImage ? (
                <Image
                  src={vehicleImage}
                  alt={vehicleName}
                  width={600}
                  height={400}
                  className="w-full h-full object-cover"
                  unoptimized
                />
              ) : (
                <Car className="w-32 h-32 text-red-500" />
              )}
            </div>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Maintenance Due</span>
                <span className="font-semibold">{info?.maintenanceDueDate ? info.maintenanceDueDate : 'Loading...'}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Tyre Pressures</span>
                <span className="font-mono text-sm">
                  {info?.tyrePressures
                    ? `FL: ${info.tyrePressures.frontLeft} | FR: ${info.tyrePressures.frontRight} | RL: ${info.tyrePressures.rearLeft} | RR: ${info.tyrePressures.rearRight}`
                    : 'Loading...'}
                </span>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div className="bg-gray-800 rounded-lg p-8">
              <div className="flex items-center gap-3 mb-4">
                <Fuel className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Fuel Level</h3>
              </div>
              <div className="relative pt-1">
                <div className="flex mb-2 items-center justify-between">
                  <div className="text-3xl font-bold">
                    {info?.fuelLevel ? `${info.fuelLevel.toFixed(1)}%` : 'Loading...'}
                  </div>
                  <span className="text-sm text-gray-400">Live Data</span>
                </div>
                <div className="overflow-hidden h-4 mb-4 text-xs flex rounded-full bg-gray-700">
                  <div
                    style={{ width: `${info?.fuelLevel || 0}%` }}
                    className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-gradient-to-r from-red-600 to-red-400 transition-all duration-1000"
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-gray-800 rounded-lg p-8 h-[500px] flex flex-col">
              <div className="flex items-center gap-3 mb-4 flex-shrink-0">
                <Navigation className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Current Position</h3>
              </div>
              <div className="flex-grow rounded-lg overflow-hidden relative border border-gray-700">
                <VehicleMap
                  location={carLocation}
                  vehicleName={vehicleName}
                />
              </div>
              <div className="text-center mt-4 flex-shrink-0">
                <p className="text-sm text-gray-400 mb-1">GPS Coordinates</p>
                <p className="font-mono text-lg">
                  {carLocation ? `${carLocation.lat.toFixed(4)}°N, ${carLocation.lng.toFixed(4)}°E` : 'Waiting for signal...'}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}