'use client'

export default function JourneyPanel({
    status = 'WAITING',
    routeName = 'Unknown Route',
    distanceRemaining = 0,
    etaSeconds = 0,
    currentPosition = null
}) {
    // Format distance
    const formattedDistance = distanceRemaining >= 1000
        ? `${(distanceRemaining / 1000).toFixed(1)} km`
        : `${Math.round(distanceRemaining)} m`

    // Format ETA
    const formatETA = (seconds) => {
        if (seconds <= 0) return '--'
        const mins = Math.floor(seconds / 60)
        const secs = Math.floor(seconds % 60)
        if (mins >= 60) {
            const hrs = Math.floor(mins / 60)
            const remainingMins = mins % 60
            return `${hrs}h ${remainingMins}m`
        }
        return `${mins}m ${secs}s`
    }

    // Status badge styling
    const statusConfig = {
        WAITING: { bg: 'bg-gray-500/30', text: 'text-gray-300', label: 'Waiting' },
        IN_PROGRESS: { bg: 'bg-cyan-500/30', text: 'text-cyan-300', label: 'In Progress' },
        COMPLETED: { bg: 'bg-green-500/30', text: 'text-green-300', label: 'Completed' },
    }
    const statusStyle = statusConfig[status] || statusConfig.WAITING

    return (
        <div className="absolute top-24 right-6 w-72">
            {/* Glassmorphism container */}
            <div className="bg-gray-900/70 backdrop-blur-md rounded-2xl p-5 border border-gray-700/50 shadow-2xl">
                {/* Header */}
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold text-white">Live Journey</h3>
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${statusStyle.bg} ${statusStyle.text}`}>
                        {statusStyle.label}
                    </span>
                </div>

                {/* Route name */}
                <div className="mb-4">
                    <p className="text-xs text-gray-400 mb-1">Route</p>
                    <p className="text-sm font-medium text-white truncate">{routeName}</p>
                </div>

                {/* Stats grid */}
                <div className="grid grid-cols-2 gap-3 mb-4">
                    {/* Distance */}
                    <div className="bg-gray-800/50 rounded-xl p-3">
                        <p className="text-xs text-gray-400 mb-1">Distance</p>
                        <p className="text-lg font-bold text-cyan-400">{formattedDistance}</p>
                    </div>

                    {/* ETA */}
                    <div className="bg-gray-800/50 rounded-xl p-3">
                        <p className="text-xs text-gray-400 mb-1">ETA</p>
                        <p className="text-lg font-bold text-cyan-400">{formatETA(etaSeconds)}</p>
                    </div>
                </div>

                {/* Destination */}
                <div className="bg-gray-800/50 rounded-xl p-3 mb-3">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-red-500/20 rounded-lg flex items-center justify-center">
                            <span className="text-base">🏁</span>
                        </div>
                        <div>
                            <p className="text-xs text-gray-400">Destination</p>
                            <p className="text-sm font-medium text-white">Stuttgart, DE</p>
                        </div>
                    </div>
                </div>

                {/* Current position */}
                <div className="bg-gray-800/50 rounded-xl p-3">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-blue-500/20 rounded-lg flex items-center justify-center">
                            <span className="text-base">📍</span>
                        </div>
                        <div>
                            <p className="text-xs text-gray-400">Position</p>
                            {currentPosition ? (
                                <p className="text-xs font-mono text-white">
                                    {currentPosition.lat.toFixed(4)}°, {currentPosition.lng.toFixed(4)}°
                                </p>
                            ) : (
                                <p className="text-xs text-gray-500">—</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
