'use client'

import { MapPin } from 'lucide-react'
import { useMQTT } from '@/hooks/useMQTT'

export default function WorldDrivePage() {
  const location = useMQTT('supercar/location')

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-8 text-center">Nebula World Drive</h1>
        <p className="text-center text-gray-400 mb-12">Follow our Apex supercar on its global journey</p>
        
        <div className="bg-gray-800 rounded-lg p-8">
          <div className="aspect-video bg-gray-700 rounded-lg mb-6 relative overflow-hidden">
            <div className="absolute inset-0 bg-[url('https://api.mapbox.com/styles/v1/mapbox/dark-v10/static/9.1829,48.7758,11,0/800x600@2x?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw')] bg-cover bg-center"></div>
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="animate-pulse">
                <MapPin className="w-12 h-12 text-red-500" />
              </div>
            </div>
          </div>
          
          <div className="grid md:grid-cols-3 gap-6">
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Current Location</h3>
              <p className="text-2xl font-bold">Stuttgart, Germany</p>
            </div>
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Coordinates</h3>
              <p className="text-xl font-mono">
                {location ? `${location.lat.toFixed(4)}°N, ${location.lng.toFixed(4)}°E` : 'Loading...'}
              </p>
            </div>
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Status</h3>
              <p className="text-2xl font-bold text-green-500">● Live</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}