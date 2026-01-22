'use client'

export default function MapControls({
    isFollowing = true,
    onFollowingToggle,
    onZoomIn,
    onZoomOut,
    onSettings
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

            {/* Settings button */}
            <button
                onClick={onSettings}
                className="w-12 h-12 rounded-xl bg-gray-900/60 backdrop-blur-md border border-gray-700/50 flex items-center justify-center text-gray-400 hover:text-white hover:bg-gray-800/60 transition-colors"
                title="Settings"
            >
                <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
            </button>
        </div>
    )
}
