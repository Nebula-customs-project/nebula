'use client'

import React, { useState } from 'react'
import Vehicle3DViewer from '../../components/Vehicle3DViewer'
import CustomizationPanel from '../../components/CustomizationPanel'
import { mockVehicle, defaultConfiguration } from '../data/vehicleData'

export default function CarConfiguratorPage() {
  // State management
  const [configuration, setConfiguration] = useState(defaultConfiguration)
  const [activeCategory, setActiveCategory] = useState(
    mockVehicle.categories[0].id
  )

  // Function to update configuration when a part is selected
  const handlePartSelect = (categoryId, partVisualKey) => {
    setConfiguration((prev) => ({
      ...prev,
      [categoryId]: partVisualKey,
    }))
  }

  // Function to calculate total price
  const calculateTotalPrice = () => {
    let totalCost = mockVehicle.basePrice

    // Iterate through each category and find the selected part's cost
    mockVehicle.categories.forEach((category) => {
      const selectedPartKey = configuration[category.id]
      const selectedPart = category.parts.find(
        (part) => part.visualKey === selectedPartKey
      )
      if (selectedPart) {
        totalCost += selectedPart.cost
      }
    })

    return totalCost
  }

  // Function to reset configuration to default
  const handleReset = () => {
    setConfiguration(defaultConfiguration)
  }

  // Function to get selected part details
  const getSelectedPartDetails = () => {
    const details = []

    mockVehicle.categories.forEach((category) => {
      const selectedPartKey = configuration[category.id]
      const selectedPart = category.parts.find(
        (part) => part.visualKey === selectedPartKey
      )
      if (selectedPart) {
        details.push({ category: category.name, part: selectedPart })
      }
    })

    return details
  }

  const totalPrice = calculateTotalPrice()
  const customizationCost = totalPrice - mockVehicle.basePrice

  return (
    <div className="flex flex-col h-screen bg-black text-white">
      {/* Main Content */}
      <div className="flex flex-1 overflow-hidden">
        {/* Left: 3D Viewer */}
        <div className="flex-1 overflow-hidden">
          <Vehicle3DViewer
            vehicleName={mockVehicle.name}
            configuration={configuration}
          />
        </div>

        {/* Right: Customization Panel */}
        <div className="w-96 border-l border-gray-800 overflow-hidden">
          <CustomizationPanel
            categories={mockVehicle.categories}
            activeCategory={activeCategory}
            setActiveCategory={setActiveCategory}
            configuration={configuration}
            onPartSelect={handlePartSelect}
          />
        </div>
      </div>

      {/* Footer: Premium Price Summary */}
      <footer className="bg-gradient-to-r from-gray-900 via-gray-800 to-gray-900 border-t border-gray-700/50 backdrop-blur-sm">
        <div className="px-8 py-6">
          <div className="flex items-center justify-between gap-8">
            {/* Left: Branding & Summary */}
            <div className="flex items-center gap-6">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center shadow-lg shadow-red-500/30">
                  <span className="text-white font-bold text-base">N</span>
                </div>
                <div>
                  <p className="text-white font-semibold text-sm">Nebula Apex</p>
                  <p className="text-gray-400 text-xs">
                    {getSelectedPartDetails().filter((d) => d.part.cost > 0).length} premium upgrades
                  </p>
                </div>
              </div>
            </div>

            {/* Center: Price Breakdown - Premium Design */}
            <div className="flex items-center gap-8 flex-1 justify-center">
              <div className="text-center">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1.5 font-semibold">Base Price</p>
                <p className="text-white text-xl font-bold">
                  €{mockVehicle.basePrice.toLocaleString()}
                </p>
              </div>
              
              <div className="h-12 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>
              
              <div className="text-center">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1.5 font-semibold">Customization</p>
                <p
                  className={`text-xl font-bold transition-colors ${
                    customizationCost > 0 
                      ? "text-red-400" 
                      : "text-gray-500"
                  }`}
                >
                  {customizationCost > 0 ? "+" : ""}€{customizationCost.toLocaleString()}
                </p>
              </div>
              
              <div className="h-12 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>
              
              <div className="text-center relative">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1.5 font-semibold">Total Price</p>
                <div className="relative">
                  <p className="text-green-400 text-4xl font-bold tracking-tight">
                    €{totalPrice.toLocaleString()}
                  </p>
                  <div className="absolute -inset-2 bg-green-400/20 blur-2xl rounded-lg"></div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons */}
            <div className="flex items-center gap-3">
              <button
                onClick={handleReset}
                className="px-5 py-2.5 bg-gray-800/80 hover:bg-gray-700 rounded-lg text-sm font-semibold transition-all duration-200 border border-gray-700 hover:border-gray-600 backdrop-blur-sm"
                title="Reset All Customizations"
              >
                Reset All
              </button>
              
              <button 
                className="px-8 py-2.5 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 rounded-lg text-sm font-bold transition-all duration-200 shadow-xl shadow-red-500/40 hover:shadow-red-500/60 transform hover:scale-105"
              >
                Save Configuration
              </button>
            </div>
          </div>

          {/* Progress Indicator - Enhanced */}
          <div className="mt-5 pt-5 border-t border-gray-700/50">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-3">
                <span className="text-gray-400 text-xs uppercase tracking-wider font-semibold">Customization Progress</span>
                <div className="h-1 w-1 rounded-full bg-gray-500"></div>
                <span className="text-gray-500 text-xs">
                  {Math.round((customizationCost / 50000) * 100)}% complete
                </span>
              </div>
              <span className="text-gray-500 text-xs">
                {customizationCost > 0 ? `€${customizationCost.toLocaleString()} added` : 'Base configuration'}
              </span>
            </div>
            <div className="h-2 bg-gray-800/50 rounded-full overflow-hidden backdrop-blur-sm">
              <div
                className="h-full bg-gradient-to-r from-red-500 via-red-600 to-red-500 transition-all duration-700 shadow-lg shadow-red-500/50"
                style={{
                  width: `${Math.min((customizationCost / 50000) * 100, 100)}%`,
                }}
              ></div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
