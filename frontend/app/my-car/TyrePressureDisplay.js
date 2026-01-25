'use client';

import { CheckCircle, AlertTriangle } from 'lucide-react';

export default function TyrePressureDisplay({ pressures, lowPressureThreshold = 32 }) {
    // Default values if data not loaded yet
    const safePressures = pressures || {
        frontLeft: 35,
        frontRight: 35,
        rearLeft: 35,
        rearRight: 35
    };

    const isLow = (value) => value < lowPressureThreshold;

    // Helper to render pressure badge
    const PressureBadge = ({ value, label, className }) => (
        <div className={`absolute flex flex-col items-center ${className}`}>
            <span className="text-xs text-gray-400 uppercase tracking-wider mb-1 font-semibold">{label}</span>
            <div className={`
        flex items-center gap-2 px-3 py-1.5 rounded-lg border backdrop-blur-md transition-all duration-300
        ${isLow(value)
                    ? 'bg-red-500/20 border-red-500/50 text-red-200 shadow-[0_0_15px_rgba(239,68,68,0.3)]'
                    : 'bg-white/5 border-white/10 text-white shadow-[0_0_10px_rgba(255,255,255,0.05)] hover:bg-white/10'}
      `}>
                <span className="text-xl font-bold font-mono">{value}</span>
                <span className="text-[10px] text-gray-400 font-normal">PSI</span>
                {isLow(value) ? (
                    <AlertTriangle className="w-3 h-3 text-red-500 animate-pulse" />
                ) : (
                    <CheckCircle className="w-3 h-3 text-green-500" />
                )}
            </div>
        </div>
    );

    return (
        <div className="relative w-full h-[300px] bg-gray-800/50 rounded-2xl border border-white/5 p-6 flex items-center justify-center overflow-hidden group">
            {/* Background accents */}
            <div className="absolute inset-0 bg-gradient-to-b from-transparent via-blue-900/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700"></div>

            {/* Car Silhouette (SVG) */}
            <div className="relative w-40 h-64 opacity-80 z-10">
                <svg viewBox="0 0 100 160" className="w-full h-full drop-shadow-2xl">
                    {/* Car Body Shadow */}
                    <path d="M20,30 Q10,30 10,50 L10,130 Q10,150 20,150 L80,150 Q90,150 90,130 L90,50 Q90,30 80,30 Z" fill="black" filter="blur(15px)" opacity="0.5" />

                    {/* Car Body */}
                    <defs>
                        <linearGradient id="carBodyGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                            <stop offset="0%" stopColor="#1f2937" />
                            <stop offset="50%" stopColor="#374151" />
                            <stop offset="100%" stopColor="#111827" />
                        </linearGradient>
                        <linearGradient id="glassGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                            <stop offset="0%" stopColor="#111827" stopOpacity="0.9" />
                            <stop offset="100%" stopColor="#1f2937" stopOpacity="0.8" />
                        </linearGradient>
                    </defs>

                    {/* Chassis */}
                    <path d="M20,20 Q50,5 80,20 L85,40 L85,130 Q85,150 50,155 Q15,150 15,130 L15,40 Z" fill="url(#carBodyGrad)" stroke="#4b5563" strokeWidth="0.5" />

                    {/* Windshield */}
                    <path d="M25,40 Q50,35 75,40 L78,55 Q50,60 22,55 Z" fill="url(#glassGrad)" stroke="#1f2937" strokeWidth="0.2" />

                    {/* Roof */}
                    <path d="M22,55 Q50,60 78,55 L75,100 Q50,105 25,100 Z" fill="#1f2937" opacity="0.5" />

                    {/* Rear Window */}
                    <path d="M25,100 Q50,105 75,100 L72,120 Q50,125 28,120 Z" fill="url(#glassGrad)" stroke="#1f2937" strokeWidth="0.2" />

                    {/* Headlights */}
                    <path d="M18,30 Q25,35 30,32" stroke="#60a5fa" strokeWidth="1" opacity="0.6" strokeLinecap="round" />
                    <path d="M82,30 Q75,35 70,32" stroke="#60a5fa" strokeWidth="1" opacity="0.6" strokeLinecap="round" />

                    {/* Taillights */}
                    <path d="M18,140 Q25,138 30,142" stroke="#ef4444" strokeWidth="1" opacity="0.8" strokeLinecap="round" />
                    <path d="M82,140 Q75,138 70,142" stroke="#ef4444" strokeWidth="1" opacity="0.8" strokeLinecap="round" />
                </svg>

                {/* Tyres (Visual Elements) */}
                <div className="absolute top-[20%] -left-[5%] w-[10%] h-[15%] bg-black rounded-sm shadow-lg"></div> {/* FL */}
                <div className="absolute top-[20%] -right-[5%] w-[10%] h-[15%] bg-black rounded-sm shadow-lg"></div> {/* FR */}
                <div className="absolute bottom-[20%] -left-[5%] w-[10%] h-[15%] bg-black rounded-sm shadow-lg"></div> {/* RL */}
                <div className="absolute bottom-[20%] -right-[5%] w-[10%] h-[15%] bg-black rounded-sm shadow-lg"></div> {/* RR */}
            </div>

            {/* Connection Lines (SVG Overlay) */}
            <svg className="absolute inset-0 w-full h-full pointer-events-none opacity-30">
                <line x1="20%" y1="20%" x2="40%" y2="35%" stroke="white" strokeWidth="1" strokeDasharray="4 4" />
                <line x1="80%" y1="20%" x2="60%" y2="35%" stroke="white" strokeWidth="1" strokeDasharray="4 4" />
                <line x1="20%" y1="80%" x2="40%" y2="65%" stroke="white" strokeWidth="1" strokeDasharray="4 4" />
                <line x1="80%" y1="80%" x2="60%" y2="65%" stroke="white" strokeWidth="1" strokeDasharray="4 4" />
            </svg>

            {/* Pressure Badges */}
            <PressureBadge value={safePressures.frontLeft} label="Front Left" className="top-[10%] left-[5%]" />
            <PressureBadge value={safePressures.frontRight} label="Front Right" className="top-[10%] right-[5%]" />
            <PressureBadge value={safePressures.rearLeft} label="Rear Left" className="bottom-[10%] left-[5%]" />
            <PressureBadge value={safePressures.rearRight} label="Rear Right" className="bottom-[10%] right-[5%]" />
        </div>
    );
}
