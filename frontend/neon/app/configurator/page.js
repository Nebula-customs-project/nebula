'use client'

import { useState } from 'react'
import { Car } from 'lucide-react'

export default function ConfiguratorPage() {
  const [config, setConfig] = useState({
    color: 'red',
    rims: 'sport',
    interior: 'black'
  })

  const colors = [
    { name: 'Racing Red', value: 'red', hex: '#DC2626' },
    { name: 'Midnight Black', value: 'black', hex: '#000000' },
    { name: 'Pearl White', value: 'white', hex: '#FFFFFF' },
    { name: 'Ocean Blue', value: 'blue', hex: '#2563EB' },
    { name: 'Silver', value: 'silver', hex: '#94A3B8' }
  ]

  const rims = [
    { name: 'Sport 19"', value: 'sport', price: 'Standard' },
    { name: 'Performance 20"', value: 'performance', price: '+€2,500' },
    { name: 'Premium 21"', value: 'premium', price: '+€4,800' }
  ]

  const interiors = [
    { name: 'Black Leather', value: 'black' },
    { name: 'Beige Leather', value: 'beige' },
    { name: 'Red Sport', value: 'red' },
    { name: 'Carbon Fiber', value: 'carbon' }
  ]

  const calculateTotal = () => {
    let total = 245000
    if (config.rims === 'performance') total += 2500
    if (config.rims === 'premium') total += 4800
    return total
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">Configure Your Nebula</h1>
        
        <div className="grid md:grid-cols-2 gap-12">
          <div className="bg-gray-800 rounded-lg p-8">
            <div 
              className="aspect-video rounded-lg mb-6 flex items-center justify-center" 
              style={{backgroundColor: colors.find(c => c.value === config.color)?.hex}}
            >
              <Car className="w-32 h-32 text-white opacity-80" />
            </div>
            <div className="text-center">
              <h2 className="text-3xl font-bold mb-2">Nebula Apex</h2>
              <p className="text-gray-400 mb-4">Your Configuration</p>
              <div className="bg-gray-700 p-4 rounded-lg">
                <div className="flex justify-between mb-2">
                  <span>Base Price:</span>
                  <span>€245,000</span>
                </div>
                <div className="flex justify-between mb-2">
                  <span>Rims:</span>
                  <span>{rims.find(r => r.value === config.rims)?.price}</span>
                </div>
                <div className="border-t border-gray-600 mt-2 pt-2">
                  <div className="flex justify-between font-bold text-xl">
                    <span>Total:</span>
                    <span className="text-red-500">€{calculateTotal().toLocaleString()}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div>
              <h3 className="text-2xl font-bold mb-4">Exterior Color</h3>
              <div className="grid grid-cols-5 gap-4">
                {colors.map(color => (
                  <button
                    key={color.value}
                    onClick={() => setConfig({...config, color: color.value})}
                    className={`aspect-square rounded-full border-4 transition ${
                      config.color === color.value ? 'border-red-500 scale-110' : 'border-gray-600'
                    }`}
                    style={{backgroundColor: color.hex}}
                    title={color.name}
                  />
                ))}
              </div>
            </div>

            <div>
              <h3 className="text-2xl font-bold mb-4">Rims</h3>
              <div className="space-y-3">
                {rims.map(rim => (
                  <button
                    key={rim.value}
                    onClick={() => setConfig({...config, rims: rim.value})}
                    className={`w-full p-4 rounded-lg border-2 transition ${
                      config.rims === rim.value ? 'border-red-500 bg-gray-700' : 'border-gray-700 bg-gray-800'
                    }`}
                  >
                    <div className="flex justify-between">
                      <span className="font-semibold">{rim.name}</span>
                      <span className="text-gray-400">{rim.price}</span>
                    </div>
                  </button>
                ))}
              </div>
            </div>

            <div>
              <h3 className="text-2xl font-bold mb-4">Interior</h3>
              <div className="grid grid-cols-2 gap-3">
                {interiors.map(interior => (
                  <button
                    key={interior.value}
                    onClick={() => setConfig({...config, interior: interior.value})}
                    className={`p-4 rounded-lg border-2 transition ${
                      config.interior === interior.value ? 'border-red-500 bg-gray-700' : 'border-gray-700 bg-gray-800'
                    }`}
                  >
                    {interior.name}
                  </button>
                ))}
              </div>
            </div>

            <button className="w-full bg-red-600 hover:bg-red-700 py-4 rounded-lg text-lg font-semibold transition">
              Save Configuration
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}