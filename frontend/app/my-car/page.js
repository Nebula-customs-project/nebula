'use client'

import { Car, Fuel, Navigation, MapPin } from 'lucide-react'
import Image from 'next/image'
import { useVehicleTelemetry } from '@/hooks/useVehicleTelemetry'
import { useUserVehicleInfo } from '@/hooks/useUserVehicleInfo'
import { useAuth } from '@/hooks/useAuth'
import dynamic from 'next/dynamic'
import TyrePressureDisplay from './TyrePressureDisplay'

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
  const { telemetry, isConnected } = useVehicleTelemetry()
  const { vehicleInfo, loading } = useUserVehicleInfo()

  const vehicleName = vehicleInfo?.vehicleName || telemetry?.vehicleName || 'My Nebula Car'
  const vehicleImage = vehicleInfo?.vehicleImage || null
  const carLocation = telemetry?.carLocation || null
  const fuelLevel = telemetry?.fuelLevel || null

  return (
    <div className="min-h-screen bg-[#0B0F19] text-white pt-28 pb-32 px-6 font-sans selection:bg-red-500/30">
      <div className="max-w-[1700px] mx-auto space-y-8">

        {/* Header - Clean & Simple with Proper Spacing from Nav */}
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-gray-800 pb-6">
          <div className="flex items-center gap-6">
            <h1 className="text-6xl font-bold text-white tracking-tighter">
              {vehicleName}
            </h1>
            <div className={`px-4 py-1.5 rounded-full border ${isConnected ? 'bg-green-500/10 border-green-500/20 text-green-400' : 'bg-yellow-500/10 border-yellow-500/20 text-yellow-400'} flex items-center gap-2 text-sm font-medium self-center`}>
              <div className={`w-2 h-2 rounded-full ${isConnected ? 'bg-green-500 animate-pulse' : 'bg-yellow-500'}`}></div>
              {isConnected ? 'Connected' : 'Connecting...'}
            </div>
          </div>

          <div className="text-right">
            <p className="text-xs text-gray-400 uppercase tracking-widest mb-1 font-semibold">Maintenance Due</p>
            <p className="text-xl font-mono text-white tracking-tight">{vehicleInfo?.maintenanceDueDate || '---'}</p>
          </div>
        </div>

        {/* Main Content Grid - Responsive Split Layout */}
        <div className="grid lg:grid-cols-12 gap-8 items-stretch">

          {/* LEFT COLUMN: Hero Car (7 cols) - Flexible Height */}
          <div className="lg:col-span-7 flex flex-col min-h-[500px] lg:h-auto lg:min-h-[700px]">
            <div className="bg-[#111827] rounded-[32px] p-8 border border-gray-800 shadow-2xl relative overflow-hidden group flex-grow flex flex-col">
              {/* Ambient Background */}
              <div className="absolute inset-0 bg-gradient-to-b from-gray-900/50 via-[#111827] to-[#0B0F19] z-0"></div>
              <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[80%] h-[80%] bg-blue-500/5 rounded-full blur-[150px] pointer-events-none"></div>

              {/* Car Image - Object Cover for Poster Look */}
              <div className="relative w-full h-full z-10 rounded-2xl overflow-hidden flex-grow min-h-[400px]">
                {vehicleImage ? (
                  <Image
                    src={vehicleImage}
                    alt={vehicleName}
                    fill
                    className="object-cover hover:scale-105 transition-transform duration-1000 ease-out"
                    unoptimized
                    priority
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-gray-900/50">
                    <Car className="w-48 h-48 text-gray-800" />
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* RIGHT COLUMN: Info Stack (5 cols) - Natural Stacking */}
          <div className="lg:col-span-5 flex flex-col gap-6">

            {/* 1. Tyre Status */}
            <div className="bg-[#111827] rounded-3xl p-6 border border-gray-800 shadow-xl w-full min-h-[320px]">
              <div className="mb-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-gray-800 flex items-center justify-center text-gray-400">
                    <Car className="w-5 h-5" />
                  </div>
                  <h3 className="text-xl font-bold text-gray-200">Tyre Status</h3>
                </div>
                <span className="text-xs text-green-500 bg-green-500/10 px-3 py-1 rounded-full font-medium">Optimal</span>
              </div>
              <div className="h-[250px] w-full">
                <TyrePressureDisplay
                  pressures={vehicleInfo?.tyrePressures}
                  lowPressureThreshold={32}
                />
              </div>
            </div>

            {/* 2. Fuel Level */}
            <div className="bg-[#111827] rounded-3xl p-8 border border-gray-800 shadow-xl relative overflow-hidden w-full min-h-[200px]">
              <div className="absolute -right-10 -top-10 w-40 h-40 bg-red-500/10 rounded-full blur-3xl pointer-events-none"></div>

              <div className="flex items-center justify-between mb-8 relative z-10">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-red-500/10 flex items-center justify-center text-red-500">
                    <Fuel className="w-5 h-5" />
                  </div>
                  <h3 className="text-xl font-bold text-gray-200">Fuel Level</h3>
                </div>
              </div>

              <div className="relative z-10">
                <div className="flex items-end justify-between mb-4">
                  <div className="flex items-baseline gap-1">
                    <span className="text-6xl font-bold tracking-tighter tabular-nums text-white">
                      {fuelLevel !== null ? fuelLevel.toFixed(0) : '--'}
                    </span>
                    <span className="text-2xl text-gray-500 font-medium">%</span>
                  </div>
                  <span className="text-gray-400 font-medium mb-2">{(fuelLevel || 0) * 4} km Range</span>
                </div>

                <div className="h-5 bg-gray-800 rounded-full overflow-hidden p-1 shadow-inner border border-gray-700/50">
                  <div
                    className={`h-full rounded-full transition-all duration-1000 ease-out relative ${(fuelLevel || 0) < 20 ? 'bg-gradient-to-r from-red-600 to-red-500' : 'bg-gradient-to-r from-red-600 via-red-500 to-orange-500'
                      }`}
                    style={{ width: `${fuelLevel || 0}%` }}
                  ></div>
                </div>
              </div>
            </div>

            {/* 3. Location Map */}
            <div className="bg-[#111827] rounded-3xl p-1 border border-gray-800 shadow-xl flex-1 flex flex-col min-h-[300px]">
              <div className="p-5 flex justify-between items-center bg-[#111827] rounded-t-[20px]">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-blue-500/10 flex items-center justify-center text-blue-500">
                    <Navigation className="w-5 h-5" />
                  </div>
                  <h3 className="text-xl font-bold text-gray-200">Location</h3>
                </div>
                <p className="font-mono text-xs text-gray-500">
                  {carLocation ? `${carLocation.lat.toFixed(4)}°N, ${carLocation.lng.toFixed(4)}°E` : 'Searching...'}
                </p>
              </div>

              <div className="flex-grow rounded-[24px] overflow-hidden relative m-1 border border-gray-800 bg-gray-900 min-h-[250px]">
                <VehicleMap
                  location={carLocation}
                  vehicleName={vehicleName}
                />
                <div className="absolute inset-0 pointer-events-none shadow-[inset_0_0_30px_rgba(0,0,0,0.6)]"></div>
              </div>
            </div>

          </div>

        </div>
      </div>
    </div>
  )
}