'use client'

export default function ProgressBar3D({ progress = 0, checkpoints = 10 }) {
    const filledCheckpoints = Math.floor((progress / 100) * checkpoints)

    return (
        <div className="absolute top-10 left-1/2 -translate-x-1/2 w-[500px] max-w-[90vw]">
            <div className="relative">
                {/* Background "Road" */}
                <div className="h-2 bg-white/5 rounded-full relative overflow-hidden ring-1 ring-white/10">
                    {/* Glowing Progress */}
                    <div
                        className="absolute inset-y-0 left-0 bg-gradient-to-r from-cyan-600 to-cyan-400 transition-all duration-700 ease-out"
                        style={{
                            width: `${progress}%`,
                            boxShadow: '0 0 20px rgba(6, 182, 212, 0.5)'
                        }}
                    />
                </div>

                {/* Percentage Marker */}
                <div
                    className="absolute top-[-30px] transition-all duration-700 ease-out flex flex-col items-center"
                    style={{ left: `${progress}%`, transform: 'translateX(-50%)' }}
                >
                    <span className="text-sm font-bold text-white mb-1">{Math.round(progress)}%</span>
                    <div className="w-1.5 h-1.5 bg-cyan-400 rounded-full shadow-[0_0_10px_#22d3ee]" />
                </div>

                {/* Checkpoints */}
                <div className="absolute inset-x-0 -bottom-6 flex justify-between px-1">
                    <span className="text-[10px] text-gray-500 uppercase tracking-widest font-bold">Start</span>
                    <div className="flex gap-4">
                        {Array.from({ length: 5 }).map((_, i) => (
                            <div key={i} className="w-[1px] h-1 bg-white/10" />
                        ))}
                    </div>
                    <span className="text-[10px] text-gray-500 uppercase tracking-widest font-bold">End</span>
                </div>
            </div>
        </div>
    )
}
