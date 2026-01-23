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
        <div className="w-72 pointer-events-auto">
            {/* Glassmorphism container */}
            <div className="bg-gray-900/80 backdrop-blur-xl rounded-xl p-4 border border-white/10 shadow-xl">
                {/* Header */}
                <div className="flex items-center justify-between mb-3">
                    <h3 className="text-base font-bold text-white tracking-tight">Live Journey</h3>
                    <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider ${statusStyle.bg} ${statusStyle.text} border border-current opacity-80`}>
                        {statusStyle.label}
                    </span>
                </div>

                {/* Route name */}
                <div className="mb-3">
                    <p className="text-[9px] text-gray-500 uppercase tracking-widest mb-0.5">Current Route</p>
                    <p className="text-sm font-bold text-white leading-tight">{routeName}</p>
                </div>

                {/* Stats grid */}
                <div className="grid grid-cols-2 gap-3 mb-3 pt-2 border-t border-white/5">
                    {/* Distance */}
                    <div>
                        <p className="text-base font-bold text-white">{formattedDistance}</p>
                        <p className="text-[9px] text-gray-500 uppercase tracking-widest">remaining</p>
                    </div>

                    {/* ETA */}
                    <div>
                        <p className="text-base font-bold text-white">{formatETA(etaSeconds)}</p>
                        <p className="text-[9px] text-gray-500 uppercase tracking-widest">ETA</p>
                    </div>
                </div>

                {/* Info rows */}
                <div className="space-y-2 pt-2 border-t border-white/5">
                    {/* Destination */}
                    <div className="flex items-center justify-between">
                        <span className="text-[9px] text-gray-500 uppercase tracking-widest">Destination</span>
                        <span className="text-xs font-medium text-white">Stuttgart, DE</span>
                    </div>

                    {/* Current position */}
                    <div className="flex items-center justify-between">
                        <span className="text-[9px] text-gray-500 uppercase tracking-widest">Position</span>
                        {currentPosition ? (
                            <span className="text-[10px] font-mono text-gray-300">
                                {currentPosition.lat.toFixed(4)}°, {currentPosition.lng.toFixed(4)}°
                            </span>
                        ) : (
                            <span className="text-xs text-gray-600">—</span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}
