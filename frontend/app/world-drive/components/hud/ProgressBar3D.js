'use client'

export default function ProgressBar3D({ progress = 0, routeName = "Automotive Dealership", isFullscreen = false }) {
    return (
        <div className={`absolute z-20 transition-all duration-700 ease-in-out pointer-events-none ${isFullscreen ? 'inset-0' : 'top-20 left-6'
            }`}>
            <div className={`relative ${isFullscreen ? 'w-full h-full' : 'flex flex-col gap-4 w-max'}`}>

                {/* Title Section - in fullscreen: top-left below navbar */}
                <div className={`pointer-events-auto transition-all duration-700 ease-in-out ${isFullscreen ? 'absolute top-20 left-6' : ''
                    }`}>
                    <span className="text-[10px] font-medium text-gray-400 uppercase tracking-wider mb-0.5 block">
                        {routeName}
                    </span>
                    <h1 className={`font-bold tracking-wide drop-shadow-md text-2xl md:text-3xl uppercase ${isFullscreen ? 'text-blue-500' : 'text-white'}`}>
                        WORLD DRIVE VIEW
                    </h1>
                </div>

                {/* 3D Progress Bar - in fullscreen: bottom center */}
                <div
                    className={`pointer-events-auto transition-all duration-700 ease-in-out ${isFullscreen ? 'absolute bottom-8 left-1/2 -translate-x-1/2 w-80 md:w-[500px] lg:w-[600px]' : 'w-full'
                        }`}
                    style={{ perspective: '800px' }}
                >
                    {/* Percentage Label */}
                    <div className="flex justify-start mb-0.5">
                        <span className="text-xs font-semibold text-blue-500">{Math.round(progress)}%</span>
                    </div>

                    {/* 3D Bar Track */}
                    <div
                        className="relative h-3 bg-gray-800/80 border border-gray-600/50 rounded-sm overflow-hidden"
                        style={{
                            transform: 'rotateX(45deg)',
                            transformStyle: 'preserve-3d',
                            boxShadow: '0 15px 30px rgba(0,0,0,0.5)'
                        }}
                    >
                        {/* Dashed Track Lines */}
                        <div className="absolute inset-0 flex items-center justify-around px-2">
                            {Array.from({ length: 7 }).map((_, i) => (
                                <div key={i} className="w-3 h-0.5 bg-gray-600/50" />
                            ))}
                        </div>

                        {/* Filled Progress */}
                        <div
                            className="absolute top-0 left-0 h-full bg-blue-500 transition-all duration-700 ease-out"
                            style={{
                                width: `${progress}%`,
                                boxShadow: '0 0 12px rgba(46,154,254,0.8)'
                            }}
                        />

                        {/* Current Position Marker */}
                        <div
                            className="absolute top-0 h-full w-0.5 bg-white transition-all duration-700 ease-out"
                            style={{
                                left: `${progress}%`,
                                boxShadow: '0 0 8px white'
                            }}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}
