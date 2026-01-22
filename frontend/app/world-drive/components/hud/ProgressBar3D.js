'use client'

export default function ProgressBar3D({ progress = 0, checkpoints = 10 }) {
    const filledCheckpoints = Math.floor((progress / 100) * checkpoints)

    return (
        <div className="absolute top-6 left-1/2 -translate-x-1/2 w-[400px] max-w-[80vw]">
            {/* Glassmorphism container */}
            <div className="bg-gray-900/60 backdrop-blur-md rounded-2xl px-6 py-4 border border-gray-700/50 shadow-2xl">
                {/* Label */}
                <div className="flex justify-between items-center mb-3">
                    <span className="text-xs text-gray-400 uppercase tracking-wider">Progress</span>
                    <span
                        className="text-lg font-bold text-cyan-400"
                        style={{ textShadow: '0 0 10px rgba(0, 212, 255, 0.5)' }}
                    >
                        {Math.round(progress)}%
                    </span>
                </div>

                {/* Checkpoint bar */}
                <div className="relative h-3 bg-gray-800 rounded-full overflow-hidden">
                    {/* Glow background */}
                    <div
                        className="absolute inset-y-0 left-0 bg-gradient-to-r from-cyan-500 to-cyan-400 rounded-full transition-all duration-500"
                        style={{
                            width: `${progress}%`,
                            boxShadow: '0 0 15px rgba(0, 212, 255, 0.6), 0 0 30px rgba(0, 212, 255, 0.3)'
                        }}
                    />

                    {/* Checkpoint markers */}
                    <div className="absolute inset-0 flex justify-between items-center px-1">
                        {Array.from({ length: checkpoints }).map((_, i) => (
                            <div
                                key={i}
                                className={`w-1.5 h-1.5 rounded-full transition-all duration-300 ${i < filledCheckpoints
                                        ? 'bg-white shadow-[0_0_6px_rgba(255,255,255,0.8)]'
                                        : 'bg-gray-600'
                                    }`}
                            />
                        ))}
                    </div>
                </div>

                {/* Distance markers */}
                <div className="flex justify-between mt-2 text-[10px] text-gray-500">
                    <span>Start</span>
                    <span>Destination</span>
                </div>
            </div>
        </div>
    )
}
