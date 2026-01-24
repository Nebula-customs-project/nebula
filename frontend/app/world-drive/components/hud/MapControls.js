'use client'

export default function MapControls({
    isFollowing = true,
    onFollowingToggle,
    onZoomIn,
    onZoomOut,
    isFullscreen,
    onFullscreenToggle
}) {
    return (
        <div className="absolute bottom-6 right-6 flex flex-col gap-3">
            {/* Following toggle */}
            <button
                onClick={onFollowingToggle}
                className={`w-12 h-12 rounded-xl flex items-center justify-center transition-all duration-200 ${isFollowing
                    ? 'bg-cyan-500/30 border-cyan-500/50 text-cyan-400 shadow-[0_0_15px_rgba(0,212,255,0.3)]'
                    : 'bg-gray-900/60 border-gray-700/50 text-gray-400 hover:bg-gray-800/60'
                    } backdrop-blur-md border`}
                title={isFollowing ? 'Camera following: ON' : 'Camera following: OFF'}
            >
                <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
                </svg>
                {isFollowing && (
                    <span className="absolute top-1 right-1 w-2 h-2 bg-cyan-400 rounded-full animate-pulse" />
                )}
            </button>

            {/* Zoom controls */}
            <div className="flex flex-col bg-gray-900/60 backdrop-blur-md rounded-xl border border-gray-700/50 overflow-hidden">
                <button
                    onClick={onZoomIn}
                    className="w-12 h-10 flex items-center justify-center text-gray-400 hover:text-white hover:bg-gray-800/60 transition-colors border-b border-gray-700/50"
                    title="Zoom in"
                >
                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v12m6-6H6" />
                    </svg>
                </button>
                <button
                    onClick={onZoomOut}
                    className="w-12 h-10 flex items-center justify-center text-gray-400 hover:text-white hover:bg-gray-800/60 transition-colors"
                    title="Zoom out"
                >
                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M18 12H6" />
                    </svg>
                </button>
            </div>

            {/* Fullscreen button */}
            <button
                onClick={onFullscreenToggle}
                className={`w-12 h-12 rounded-xl flex items-center justify-center transition-all duration-200 ${isFullscreen
                    ? 'bg-blue-600/80 border-blue-500 text-white shadow-[0_0_15px_rgba(37,99,235,0.3)]'
                    : 'bg-gray-900/60 border-gray-700/50 text-gray-400 hover:bg-gray-800/60'
                    } backdrop-blur-md border`}
                title={isFullscreen ? "Exit Fullscreen" : "Enter Fullscreen"}
            >
                {isFullscreen ? (
                    <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M9 9L4 4m0 0l5 0m-5 0l0 5M15 9l5-5m0 0l-5 0m5 0l0 5M9 15l-5 5m0 0l5 0m-5 0l0-5M15 15l5 5m0 0l-5 0m5 0l0-5" />
                    </svg>
                ) : (
                    <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                    </svg>
                )}
            </button>
        </div>
    )
}
