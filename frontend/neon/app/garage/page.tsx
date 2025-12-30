"use client";

import React, { useState } from "react";
import Vehicle3DScene from "../components/Vehicle3DScene";
import CustomizationPanel from "../components/CustomizationPanel";
import {
  mockVehicle,
  defaultConfiguration,
  Part,
} from "../data/vehicleData";

const CustomizationGaragePage: React.FC = () => {
  // State management
  const [configuration, setConfiguration] =
    useState<Record<string, string>>(defaultConfiguration);
  const [activeCategory, setActiveCategory] = useState<string>(
    mockVehicle.categories[0].id
  );

  // Function to update configuration when a part is selected
  const handlePartSelect = (categoryId: string, partVisualKey: string) => {
    setConfiguration((prev) => ({
      ...prev,
      [categoryId]: partVisualKey,
    }));
  };

  // Function to calculate total price
  const calculateTotalPrice = (): number => {
    let totalCost = mockVehicle.basePrice;

    // Iterate through each category and find the selected part's cost
    mockVehicle.categories.forEach((category) => {
      const selectedPartKey = configuration[category.id];
      const selectedPart = category.parts.find(
        (part: Part) => part.visualKey === selectedPartKey
      );
      if (selectedPart) {
        totalCost += selectedPart.cost;
      }
    });

    return totalCost;
  };

  // Function to reset configuration to default
  const handleReset = () => {
    setConfiguration(defaultConfiguration);
  };

  // Function to get selected part details
  const getSelectedPartDetails = () => {
    const details: Array<{ category: string; part: Part }> = [];

    mockVehicle.categories.forEach((category) => {
      const selectedPartKey = configuration[category.id];
      const selectedPart = category.parts.find(
        (part: Part) => part.visualKey === selectedPartKey
      );
      if (selectedPart) {
        details.push({ category: category.name, part: selectedPart });
      }
    });

    return details;
  };

  const totalPrice = calculateTotalPrice();
  const customizationCost = totalPrice - mockVehicle.basePrice;

  return (
    <div className="flex flex-col h-screen bg-black text-white">
      {/* Header */}
      <header className="bg-gray-900 border-b border-gray-800 px-6 py-4 z-10">
        <div className="flex items-center justify-between max-w-screen-2xl mx-auto">
          <div>
            <h1 className="text-2xl font-bold text-white">
              Benny&apos;s Custom Shop
            </h1>
            <p className="text-gray-400 text-sm">
              Premium Vehicle Customization
            </p>
          </div>

          <div className="flex items-center gap-4">
            <button
              onClick={handleReset}
              className="px-4 py-2 bg-gray-800 hover:bg-gray-700 rounded-lg text-sm font-medium transition-colors border border-gray-700"
            >
              Reset All
            </button>
            <button className="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg text-sm font-bold transition-colors shadow-lg shadow-blue-500/20">
              Purchase Vehicle
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="flex flex-1 overflow-hidden">
        {/* Left: 3D Viewer */}
        <div className="flex-1 overflow-hidden">
          <Vehicle3DScene
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

      {/* Footer: Price Summary */}
      <footer className="bg-gray-900 border-t border-gray-800 px-6 py-4">
        <div className="max-w-screen-2xl mx-auto">
          <div className="flex items-center justify-between">
            {/* Price Breakdown */}
            <div className="flex items-center gap-8">
              <div>
                <p className="text-gray-400 text-xs mb-1">Base Price</p>
                <p className="text-white text-lg font-bold">
                  ${mockVehicle.basePrice.toLocaleString()}
                </p>
              </div>
              <div>
                <p className="text-gray-400 text-xs mb-1">Customization</p>
                <p
                  className={`text-lg font-bold ${
                    customizationCost > 0 ? "text-blue-400" : "text-gray-500"
                  }`}
                >
                  {customizationCost > 0 ? "+" : ""}$
                  {customizationCost.toLocaleString()}
                </p>
              </div>
              <div className="h-12 w-px bg-gray-700"></div>
              <div>
                <p className="text-gray-400 text-xs mb-1">Total Price</p>
                <p className="text-green-400 text-2xl font-bold">
                  ${totalPrice.toLocaleString()}
                </p>
              </div>
            </div>

            {/* Summary Stats */}
            <div className="flex items-center gap-6 text-sm">
              <div className="text-center">
                <p className="text-gray-400 text-xs mb-1">Parts Selected</p>
                <p className="text-white font-bold">
                  {getSelectedPartDetails().filter((d) => d.part.cost > 0).length}
                </p>
              </div>
              <div className="text-center">
                <p className="text-gray-400 text-xs mb-1">Categories</p>
                <p className="text-white font-bold">
                  {mockVehicle.categories.length}
                </p>
              </div>
            </div>
          </div>

          {/* Progress Bar */}
          <div className="mt-4">
            <div className="h-1 bg-gray-800 rounded-full overflow-hidden">
              <div
                className="h-full bg-linear-to-r from-blue-500 to-blue-600 transition-all duration-500"
                style={{
                  width: `${Math.min(
                    (customizationCost / 50000) * 100,
                    100
                  )}%`,
                }}
              ></div>
            </div>
            <p className="text-gray-500 text-xs mt-2">
              Customization progress
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default CustomizationGaragePage;
