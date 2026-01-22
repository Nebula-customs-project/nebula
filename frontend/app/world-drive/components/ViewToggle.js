'use client'

import { useState, useEffect } from 'react'

export default function ViewToggle({ viewMode, onViewModeChange }) {
    // Load saved preference on mount
    useEffect(() => {
        const saved = localStorage.getItem('world-drive-view-mode')
        if (saved && (saved === 'map' || saved === '3d')) {
            onViewModeChange(saved)
        }
    }, [])

    // Save preference when changed
    const handleToggle = () => {
        const newMode = viewMode === 'map' ? '3d' : 'map'
        localStorage.setItem('world-drive-view-mode', newMode)
        onViewModeChange(newMode)
    }

    return (
        <div className="absolute top-6 left-6 z-[1000]">
            <button
                onClick={handleToggle}
                className="flex items-center gap-2 px-4 py-2.5 bg-gray-900/70 backdrop-blur-md rounded-xl border border-gray-700/50 shadow-xl hover:bg-gray-800/70 transition-all duration-200 group"
            >
                {/* Icon */}
                <span className="text-lg">
                    {viewMode === 'map' ? '🗺️' : '🎮'}
                </span>

                {/* Label */}
                <span className="text-sm font-medium text-white">
                    {viewMode === 'map' ? 'Map View' : '3D View'}
                </span>

                {/* Toggle indicator */}
                <div className="relative w-12 h-6 bg-gray-700 rounded-full ml-2">
                    <div
                        className={`absolute top-1 w-4 h-4 rounded-full transition-all duration-300 ${viewMode === '3d'
                                ? 'left-7 bg-cyan-400 shadow-[0_0_10px_rgba(0,212,255,0.6)]'
                                : 'left-1 bg-gray-400'
                            }`}
                    />
                </div>

                {/* Switch to label */}
                <span className="text-xs text-gray-400 group-hover:text-gray-300 transition-colors">
                    Switch to {viewMode === 'map' ? '3D' : 'Map'}
                </span>
            </button>
        </div>
    )
}
