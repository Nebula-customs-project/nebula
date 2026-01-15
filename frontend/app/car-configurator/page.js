'use client'

/**
 * Car Configurator Page
 * 
 * Main page component for the Nebula car configurator feature.
 * Allows users to customize their vehicle with real-time 3D preview.
 * 
 * Features:
 * - 3D interactive car viewer
 * - Customization panel with multiple categories
 * - Real-time price calculation
 * - Configuration management
 */

import React, { useState, useMemo, useCallback } from 'react'
import Vehicle3DScene from '../../components/Vehicle3DScene'
import CustomizationPanel from '../../components/CustomizationPanel'
import CarSelector from '../../components/CarSelector'
import RenderingEffect from '../../components/RenderingEffect'
import { mockVehicle, defaultConfiguration } from '../data/vehicleData'
import { availableCars } from './carModels'
import { PROGRESS_MAX_COST } from './constants'

export default function CarConfiguratorPage() {
  // State management
  const [configuration, setConfiguration] = useState(defaultConfiguration)
  const [activeCategory, setActiveCategory] = useState(
    mockVehicle.categories[0].id
  )
  const [currentCarId, setCurrentCarId] = useState(availableCars[0].id)
  const [isRendering, setIsRendering] = useState(true)
  const [modelLoaded, setModelLoaded] = useState(false)

  // Get current car model path
  const currentCar = availableCars.find((car) => car.id === currentCarId) || availableCars[0]

  /**
   * Handles car change with rendering effect
   */
  const handleCarChange = useCallback((newCarId) => {
    if (newCarId !== currentCarId) {
      setModelLoaded(false)
      setIsRendering(true)
      setCurrentCarId(newCarId)
      // Reset configuration when changing cars
      setConfiguration(defaultConfiguration)
    }
  }, [currentCarId])

  /**
   * Called when 3D model finishes loading
   */
  const handleModelLoad = useCallback(() => {
    setModelLoaded(true)
    // Let the rendering effect handle the completion timing
    // It will complete when progress reaches 70%+ if model is loaded
  }, [])

  /**
   * Handles part selection in a category
   * @param {string} categoryId - The ID of the category
   * @param {string} partVisualKey - The visual key of the selected part
   */
  const handlePartSelect = (categoryId, partVisualKey) => {
    setConfiguration((prev) => ({
      ...prev,
      [categoryId]: partVisualKey,
    }))
  }

  /**
   * Calculate pricing and selected parts count
   * Memoized for performance optimization
   */
  const { totalPrice, customizationCost, selectedPartsCount } = useMemo(() => {
    let total = mockVehicle.basePrice
    const selectedParts = []

    mockVehicle.categories.forEach((category) => {
      const selectedPartKey = configuration[category.id]
      const selectedPart = category.parts.find(
        (part) => part.visualKey === selectedPartKey
      )
      if (selectedPart) {
        total += selectedPart.cost
        if (selectedPart.cost > 0) {
          selectedParts.push({ category: category.name, part: selectedPart })
        }
      }
    })

    return {
      totalPrice: total,
      customizationCost: total - mockVehicle.basePrice,
      selectedPartsCount: selectedParts.length,
    }
  }, [configuration])

  /**
   * Resets the configuration to default values
   */
  const handleReset = () => {
    setConfiguration(defaultConfiguration)
  }

  /**
   * Calculate customization progress percentage
   */
  const progressPercentage = Math.round((customizationCost / PROGRESS_MAX_COST) * 100)

  return (
    <div className="flex flex-col h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      {/* Rendering Effect Overlay */}
      <RenderingEffect 
        isRendering={isRendering}
        modelLoaded={modelLoaded}
        onComplete={() => setIsRendering(false)}
      />

      {/* Main Content */}
      <div className="flex flex-1 overflow-hidden relative">
        {/* Decorative corner accents */}
        <div className="absolute top-0 left-0 w-32 h-32 bg-gradient-to-br from-red-500/10 to-transparent pointer-events-none z-10"></div>
        <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-bl from-red-500/10 to-transparent pointer-events-none z-10"></div>
        
        {/* Left: 3D Viewer with Premium Border */}
        <div className="flex-1 overflow-hidden p-4">
          <div className="w-full h-full relative rounded-xl overflow-hidden border border-gray-700/50 shadow-xl">
            {/* Static border glow effect - Removed animate-pulse for better performance */}
            <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500/20 via-transparent to-red-500/20 opacity-50 pointer-events-none"></div>
            
            {/* Inner border for depth */}
            <div className="absolute inset-[1px] rounded-xl border border-red-500/30 pointer-events-none"></div>
            
            {/* Corner accent decorations */}
            <div className="absolute top-0 left-0 w-12 h-12 border-t border-l border-red-500/50 rounded-tl-xl pointer-events-none"></div>
            <div className="absolute top-0 right-0 w-12 h-12 border-t border-r border-red-500/50 rounded-tr-xl pointer-events-none"></div>
            <div className="absolute bottom-0 left-0 w-12 h-12 border-b border-l border-red-500/50 rounded-bl-xl pointer-events-none"></div>
            <div className="absolute bottom-0 right-0 w-12 h-12 border-b border-r border-red-500/50 rounded-br-xl pointer-events-none"></div>
            
            <div className="w-full h-full relative">
              {/* 3D Scene */}
              <Vehicle3DScene
                vehicleName={currentCar.name}
                configuration={configuration}
                modelPath={currentCar.modelPath}
                onModelLoad={handleModelLoad}
              />
            </div>
          </div>
        </div>

        {/* Right: Customization Panel with Enhanced Border */}
        <div className="w-80 border-l border-gray-700/50 overflow-hidden bg-gray-900/50 backdrop-blur-sm shadow-xl">
          <div className="h-full border-l border-red-500/20">
            <CustomizationPanel
              categories={mockVehicle.categories}
              activeCategory={activeCategory}
              setActiveCategory={setActiveCategory}
              configuration={configuration}
              onPartSelect={handlePartSelect}
            />
          </div>
        </div>
      </div>

      {/* Footer: Premium Price Summary */}
      <footer className="bg-gradient-to-r from-gray-900 via-black to-gray-900 border-t border-gray-700/50 backdrop-blur-sm shadow-xl">
        <div className="px-6 py-4">
          <div className="flex items-center justify-between gap-6">
            {/* Left: Branding & Summary */}
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center shadow-md shadow-red-500/30">
                  <span className="text-white font-bold text-sm">N</span>
                </div>
                <div>
                  <p className="text-white font-semibold text-xs">{currentCar.name}</p>
                  <p className="text-gray-400 text-[10px]">
                    {selectedPartsCount} premium upgrade{selectedPartsCount !== 1 ? 's' : ''}
                  </p>
                </div>
              </div>
            </div>

            {/* Center: Car Selector */}
            <div className="flex items-center justify-center flex-1">
              <CarSelector
                currentCarId={currentCarId}
                onCarChange={handleCarChange}
                availableCars={availableCars}
              />
            </div>

            {/* Right: Price Breakdown & Action Buttons */}
            <div className="flex items-center gap-4">
              {/* Price Breakdown - Moved to Right */}
              <div className="flex items-center gap-4">
                <div className="text-center">
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">Base Price</p>
                  <p className="text-white text-base font-bold">
                    €{mockVehicle.basePrice.toLocaleString()}
                  </p>
                </div>
                
                <div className="h-10 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>
                
                <div className="text-center">
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">Customization</p>
                  <p
                    className={`text-base font-bold transition-colors ${
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
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">Total Price</p>
                  <div className="relative">
                    <p className="text-green-400 text-2xl font-bold tracking-tight">
                      €{totalPrice.toLocaleString()}
                    </p>
                    <div className="absolute -inset-1 bg-green-400/20 blur-xl rounded-lg"></div>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2">
                <button
                  onClick={handleReset}
                  className="px-4 py-2 bg-gray-800/80 hover:bg-gray-700 rounded-lg text-xs font-semibold transition-all duration-200 border border-gray-700 hover:border-gray-600 backdrop-blur-sm"
                  title="Reset All Customizations"
                >
                  Reset All
                </button>
                
                <button 
                  className="px-6 py-2 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 rounded-lg text-xs font-bold transition-all duration-200 shadow-lg shadow-red-500/40 hover:shadow-red-500/60 transform hover:scale-105"
                >
                  Save Configuration
                </button>
              </div>
            </div>
          </div>

          {/* Progress Indicator - Enhanced */}
          <div className="mt-3 pt-3 border-t border-gray-700/50">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="text-gray-400 text-[10px] uppercase tracking-wider font-semibold">Customization Progress</span>
                <div className="h-0.5 w-0.5 rounded-full bg-gray-500"></div>
                <span className="text-gray-500 text-[10px]">
                  {progressPercentage}% complete
                </span>
              </div>
              <span className="text-gray-500 text-[10px]">
                {customizationCost > 0 ? `€${customizationCost.toLocaleString()} added` : 'Base configuration'}
              </span>
            </div>
            <div className="h-1.5 bg-gray-800/50 rounded-full overflow-hidden backdrop-blur-sm">
              <div
                className="h-full bg-gradient-to-r from-red-500 via-red-600 to-red-500 transition-all duration-700 shadow-lg shadow-red-500/50"
                style={{
                  width: `${Math.min((customizationCost / PROGRESS_MAX_COST) * 100, 100)}%`,
                }}
              ></div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
