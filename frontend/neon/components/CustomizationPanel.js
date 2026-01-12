'use client'

import React from 'react'

export default function CustomizationPanel({
  categories,
  activeCategory,
  setActiveCategory,
  configuration,
  onPartSelect,
}) {
  const currentCategory = categories.find((cat) => cat.id === activeCategory)

  return (
    <div className="flex flex-col h-full bg-gradient-to-b from-gray-900 to-black text-white">
      {/* Category Tabs - Enhanced */}
      <div className="bg-gray-900/80 backdrop-blur-sm border-b-2 border-gray-700/50 shadow-lg">
        <div className="flex overflow-x-auto scrollbar-hide">
          {categories.map((category) => (
            <button
              key={category.id}
              onClick={() => setActiveCategory(category.id)}
              className={`shrink-0 px-6 py-4 text-sm font-medium transition-all duration-200 border-b-2 relative ${
                activeCategory === category.id
                  ? "bg-gray-800/50 border-red-500 text-white shadow-lg shadow-red-500/20"
                  : "border-transparent text-gray-400 hover:text-white hover:bg-gray-800/30"
              }`}
            >
              {activeCategory === category.id && (
                <div className="absolute inset-0 bg-gradient-to-b from-red-500/10 to-transparent pointer-events-none"></div>
              )}
              <span className="mr-2 relative z-10">{category.icon}</span>
              <span className="relative z-10">{category.name}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Parts List */}
      <div className="flex-1 overflow-y-auto p-6 space-y-3 bg-gradient-to-b from-transparent via-gray-900/20 to-transparent">
        {currentCategory && (
          <>
            <div className="mb-6 pb-4 border-b border-gray-700/30">
              <h3 className="text-2xl font-bold text-white mb-1">
                {currentCategory.name}
              </h3>
              <p className="text-gray-400 text-sm">
                Select a {currentCategory.name.toLowerCase()} option
              </p>
            </div>

            <div className="grid gap-3">
              {currentCategory.parts.map((part) => {
                const isSelected =
                  configuration[currentCategory.id] === part.visualKey

                return (
                  <button
                    key={part.id}
                    onClick={() =>
                      onPartSelect(currentCategory.id, part.visualKey)
                    }
                    className={`group relative p-4 rounded-xl border-2 transition-all duration-200 text-left ${
                      isSelected
                        ? "bg-red-500/20 border-red-500 shadow-xl shadow-red-500/30 backdrop-blur-sm"
                        : "bg-gray-800/30 border-gray-700/50 hover:border-red-500/50 hover:bg-gray-800/50"
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <h4
                            className={`font-semibold ${
                              isSelected ? "text-red-300" : "text-white"
                            }`}
                          >
                            {part.name}
                          </h4>
                          {isSelected && (
                            <span className="px-2 py-0.5 text-xs font-medium bg-red-500 text-white rounded-full">
                              Selected
                            </span>
                          )}
                        </div>
                        {part.description && (
                          <p className="text-sm text-gray-400 mb-2">
                            {part.description}
                          </p>
                        )}
                        <div className="flex items-center justify-between">
                          <span
                            className={`text-lg font-bold ${
                              part.cost === 0
                                ? "text-green-400"
                                : isSelected
                                ? "text-red-300"
                                : "text-gray-300"
                            }`}
                          >
                            {part.cost === 0
                              ? "Included"
                              : `€${part.cost.toLocaleString()}`}
                          </span>
                        </div>
                      </div>

                      {/* Selection indicator */}
                      <div
                        className={`ml-4 w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all ${
                          isSelected
                            ? "bg-red-500 border-red-500"
                            : "border-gray-600 group-hover:border-gray-500"
                        }`}
                      >
                        {isSelected && (
                          <svg
                            className="w-4 h-4 text-white"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={3}
                              d="M5 13l4 4L19 7"
                            />
                          </svg>
                        )}
                      </div>
                    </div>
                  </button>
                )
              })}
            </div>
          </>
        )}
      </div>

      {/* Category Info Footer - Enhanced */}
      <div className="p-4 bg-gray-900/80 backdrop-blur-sm border-t-2 border-gray-700/50 shadow-lg">
        <div className="flex items-center justify-between text-sm">
          <span className="text-gray-400 font-medium">
            {currentCategory?.parts.length} options available
          </span>
          <span className="text-red-400 text-xs font-semibold uppercase tracking-wider">
            Tap to customize
          </span>
        </div>
      </div>
    </div>
  )
}
