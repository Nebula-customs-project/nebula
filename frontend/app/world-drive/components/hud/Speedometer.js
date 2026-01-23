'use client'

import { useMemo } from 'react'

export default function Speedometer({ speed = 0, maxSpeed = 200 }) {
    const speedKmh = Math.round(speed)
    const percentage = Math.min(speedKmh / maxSpeed, 1)

    // SVG arc calculations
    const radius = 70
    const strokeWidth = 8
    const circumference = 2 * Math.PI * radius * 0.75 // 270 degrees arc
    const offset = circumference * (1 - percentage)

    // Color based on speed
    const speedColor = useMemo(() => {
        if (percentage < 0.5) return '#00d4ff'
        if (percentage < 0.75) return '#ffcc00'
        return '#ff4444'
    }, [percentage])

    return (
        <div className="absolute bottom-6 left-6 w-44 h-44">
            {/* Glassmorphism background */}
            <div className="absolute inset-0 bg-gray-900/60 backdrop-blur-md rounded-full border border-gray-700/50 shadow-2xl" />

            {/* SVG Gauge */}
            <svg className="absolute inset-0 w-full h-full -rotate-[135deg]" viewBox="0 0 160 160">
                {/* Background arc */}
                <circle
                    cx="80"
                    cy="80"
                    r={radius}
                    fill="none"
                    stroke="#1a1a2e"
                    strokeWidth={strokeWidth}
                    strokeLinecap="round"
                    strokeDasharray={circumference}
                    strokeDashoffset={circumference * 0.25}
                />

                {/* Progress arc */}
                <circle
                    cx="80"
                    cy="80"
                    r={radius}
                    fill="none"
                    stroke={speedColor}
                    strokeWidth={strokeWidth}
                    strokeLinecap="round"
                    strokeDasharray={circumference}
                    strokeDashoffset={offset + circumference * 0.25}
                    style={{
                        transition: 'stroke-dashoffset 0.3s ease-out, stroke 0.3s ease-out',
                        filter: `drop-shadow(0 0 8px ${speedColor})`
                    }}
                />
            </svg>

            {/* Speed value */}
            <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span
                    className="text-4xl font-bold tabular-nums"
                    style={{ color: speedColor, textShadow: `0 0 20px ${speedColor}50` }}
                >
                    {speedKmh}
                </span>
                <span className="text-xs text-gray-400 uppercase tracking-wider mt-1">km/h</span>
            </div>

            {/* Speed tick marks */}
            <div className="absolute inset-0">
                {[50, 100, 150, 200, 0].map((tick, i) => {
                    const angle = -135 + (i / 4) * 270
                    const rad = (angle * Math.PI) / 180
                    const x = 80 + 55 * Math.cos(rad)
                    const y = 80 + 55 * Math.sin(rad)

                    return (
                        <span
                            key={tick}
                            className="absolute text-[10px] text-gray-500 font-medium"
                            style={{
                                left: `${x}px`,
                                top: `${y}px`,
                                transform: 'translate(-50%, -50%)'
                            }}
                        >
                            {tick}
                        </span>
                    )
                })}
            </div>
        </div>
    )
}
