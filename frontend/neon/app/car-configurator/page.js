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
            onReset={handleReset}
          />
        </div>
      </div>

      {/* Footer: Enhanced Price Summary with Quick Access */}
      <footer className="bg-gradient-to-r from-gray-900 via-gray-800 to-gray-900 border-t border-gray-700/50 backdrop-blur-sm">
        <div className="px-6 py-5">
          <div className="flex items-center justify-between gap-6">
            {/* Left: Quick Category Access */}
            <div className="flex items-center gap-2">
              <div className="flex items-center gap-1 mr-4">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
                  <span className="text-white font-bold text-sm">N</span>
                </div>
              </div>
              <div className="flex gap-2 overflow-x-auto scrollbar-hide">
                {mockVehicle.categories.map((category) => (
                  <button
                    key={category.id}
                    onClick={() => setActiveCategory(category.id)}
                    className={`shrink-0 px-4 py-2 rounded-lg text-xs font-semibold transition-all duration-200 flex items-center gap-2 ${
                      activeCategory === category.id
                        ? 'bg-red-500/20 border-2 border-red-500 text-red-300 shadow-lg shadow-red-500/20'
                        : 'bg-gray-800/50 border-2 border-transparent text-gray-400 hover:text-white hover:bg-gray-800 hover:border-gray-600'
                    }`}
                  >
                    <span className="text-sm">{category.icon}</span>
                    <span className="hidden sm:inline">{category.name.toUpperCase()}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Center: Price Breakdown - Enhanced Design */}
            <div className="flex items-center gap-6">
              <div className="text-center">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1">Base Price</p>
                <p className="text-white text-lg font-bold">
                  €{mockVehicle.basePrice.toLocaleString()}
                </p>
              </div>
              
              <div className="h-10 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>
              
              <div className="text-center">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1">Customization</p>
                <p
                  className={`text-lg font-bold transition-colors ${
                    customizationCost > 0 
                      ? "text-red-400" 
                      : "text-gray-500"
                  }`}
                >
                  {customizationCost > 0 ? "+" : ""}€{customizationCost.toLocaleString()}
                </p>
              </div>
              
              <div className="h-10 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>
              
              <div className="text-center relative">
                <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1">Total Price</p>
                <div className="relative">
                  <p className="text-green-400 text-3xl font-bold tracking-tight">
                    €{totalPrice.toLocaleString()}
                  </p>
                  <div className="absolute -inset-1 bg-green-400/20 blur-xl rounded-lg"></div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons & Stats */}
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-4 pr-4 border-r border-gray-700">
                <div className="text-center">
                  <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1">Parts</p>
                  <p className="text-white font-bold text-sm">
                    {getSelectedPartDetails().filter((d) => d.part.cost > 0).length}
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-gray-500 text-[10px] uppercase tracking-wider mb-1">Categories</p>
                  <p className="text-white font-bold text-sm">
                    {mockVehicle.categories.length}
                  </p>
                </div>
              </div>
              
              <button
                onClick={handleReset}
                className="px-4 py-2 bg-gray-800 hover:bg-gray-700 rounded-lg text-xs font-semibold transition-all duration-200 border border-gray-700 hover:border-gray-600"
                title="Reset All"
              >
                Reset
              </button>
              
              <button 
                className="px-6 py-2 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 rounded-lg text-xs font-bold transition-all duration-200 shadow-lg shadow-red-500/30 hover:shadow-red-500/50 transform hover:scale-105"
              >
                Save
              </button>
            </div>
          </div>

          {/* Progress Indicator */}
          <div className="mt-4 pt-4 border-t border-gray-700/50">
            <div className="flex items-center justify-between mb-2">
              <span className="text-gray-500 text-[10px] uppercase tracking-wider">Customization Progress</span>
              <span className="text-gray-500 text-[10px]">
                {Math.round((customizationCost / 50000) * 100)}%
              </span>
            </div>
            <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-red-500 via-red-600 to-red-500 transition-all duration-500 shadow-lg shadow-red-500/50"
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
