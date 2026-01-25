'use client';

import { CheckCircle, AlertTriangle } from 'lucide-react';

export default function TyrePressureDisplay({ pressures, lowPressureThreshold = 31 }) {
    // Default values if data not loaded yet
    const safePressures = pressures || {
        frontLeft: 33.6,
        frontRight: 32.5,
        rearLeft: 33.3,
        rearRight: 30.6
    };

    const isLow = (value) => value < lowPressureThreshold;
    const isOptimal = (value) => value >= lowPressureThreshold && value <= 36;

    // Get status color
    const getStatusColor = (value) => {
        if (isLow(value)) return { bg: 'rgba(239, 68, 68, 0.15)', border: 'rgba(239, 68, 68, 0.4)', text: '#fca5a5', glow: 'rgba(239, 68, 68, 0.4)' };
        return { bg: 'rgba(16, 185, 129, 0.1)', border: 'rgba(16, 185, 129, 0.3)', text: '#6ee7b7', glow: 'rgba(16, 185, 129, 0.3)' };
    };

    // Helper to render pressure badge
    const PressureBadge = ({ value, label, className }) => {
        const colors = getStatusColor(value);
        
        return (
            <div className={`absolute flex flex-col items-center gap-1 ${className}`}>
                <span className="text-[10px] text-gray-500 uppercase tracking-widest font-semibold">{label}</span>
                <div 
                    className="flex items-center gap-2 px-4 py-2 rounded-xl backdrop-blur-md transition-all duration-500 hover:scale-105"
                    style={{
                        background: colors.bg,
                        border: `1px solid ${colors.border}`,
                        boxShadow: `0 0 20px ${colors.glow}`
                    }}
                >
                    <span className="text-2xl font-bold font-mono tabular-nums" style={{ color: colors.text }}>
                        {value.toFixed(1)}
                    </span>
                    <span className="text-[10px] text-gray-400 font-medium">PSI</span>
                    {isLow(value) ? (
                        <AlertTriangle className="w-4 h-4 text-red-400 animate-pulse" />
                    ) : (
                        <CheckCircle className="w-4 h-4 text-emerald-400" />
                    )}
                </div>
            </div>
        );
    };

    return (
        <div className="relative w-full h-full flex items-center justify-center overflow-hidden">
            {/* Premium Car Top-View SVG */}
            <div className="relative w-48 h-72">
                <svg viewBox="0 0 120 180" className="w-full h-full drop-shadow-2xl">
                    {/* Definitions */}
                    <defs>
                        {/* Car Body Gradient */}
                        <linearGradient id="premiumBodyGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                            <stop offset="0%" stopColor="#374151" />
                            <stop offset="50%" stopColor="#1f2937" />
                            <stop offset="100%" stopColor="#111827" />
                        </linearGradient>
                        {/* Glass Gradient */}
                        <linearGradient id="premiumGlassGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                            <stop offset="0%" stopColor="#0f172a" stopOpacity="0.9" />
                            <stop offset="100%" stopColor="#1e293b" stopOpacity="0.8" />
                        </linearGradient>
                        {/* Glow Filter */}
                        <filter id="glowEffect" x="-50%" y="-50%" width="200%" height="200%">
                            <feGaussianBlur stdDeviation="2" result="blur" />
                            <feFlood floodColor="#3b82f6" floodOpacity="0.3" result="color" />
                            <feComposite in="color" in2="blur" operator="in" result="glow" />
                            <feMerge>
                                <feMergeNode in="glow" />
                                <feMergeNode in="SourceGraphic" />
                            </feMerge>
                        </filter>
                    </defs>

                    {/* Shadow */}
                    <ellipse cx="60" cy="165" rx="45" ry="8" fill="rgba(0,0,0,0.4)" />

                    {/* Car Body */}
                    <path 
                        d="M30,25 Q60,10 90,25 L95,45 L95,145 Q95,165 60,170 Q25,165 25,145 L25,45 Z" 
                        fill="url(#premiumBodyGrad)" 
                        stroke="#4b5563" 
                        strokeWidth="0.5"
                    />

                    {/* Hood Detail Lines */}
                    <path d="M35,35 L60,30 L85,35" stroke="#4b5563" strokeWidth="0.5" fill="none" opacity="0.5" />
                    <path d="M40,42 L60,38 L80,42" stroke="#4b5563" strokeWidth="0.3" fill="none" opacity="0.3" />

                    {/* Front Windshield */}
                    <path 
                        d="M32,48 Q60,42 88,48 L85,68 Q60,72 35,68 Z" 
                        fill="url(#premiumGlassGrad)" 
                        stroke="#1e293b" 
                        strokeWidth="0.5"
                    />

                    {/* Roof */}
                    <path 
                        d="M35,68 Q60,72 85,68 L82,115 Q60,120 38,115 Z" 
                        fill="#0f172a" 
                        opacity="0.6"
                    />

                    {/* Rear Windshield */}
                    <path 
                        d="M38,115 Q60,120 82,115 L78,138 Q60,142 42,138 Z" 
                        fill="url(#premiumGlassGrad)" 
                        stroke="#1e293b" 
                        strokeWidth="0.5"
                    />

                    {/* Headlights */}
                    <ellipse cx="38" cy="28" rx="6" ry="3" fill="#3b82f6" opacity="0.6" filter="url(#glowEffect)" />
                    <ellipse cx="82" cy="28" rx="6" ry="3" fill="#3b82f6" opacity="0.6" filter="url(#glowEffect)" />

                    {/* Taillights */}
                    <rect x="32" y="153" width="12" height="4" rx="2" fill="#ef4444" opacity="0.8" />
                    <rect x="76" y="153" width="12" height="4" rx="2" fill="#ef4444" opacity="0.8" />

                    {/* Side Mirrors */}
                    <ellipse cx="22" cy="58" rx="4" ry="6" fill="#374151" stroke="#4b5563" strokeWidth="0.3" />
                    <ellipse cx="98" cy="58" rx="4" ry="6" fill="#374151" stroke="#4b5563" strokeWidth="0.3" />

                    {/* Wheels */}
                    {/* Front Left */}
                    <rect x="15" y="38" width="10" height="22" rx="3" fill="#0a0a0a" stroke="#374151" strokeWidth="0.5" />
                    <rect x="17" y="42" width="6" height="14" rx="1" fill="#1f2937" />
                    
                    {/* Front Right */}
                    <rect x="95" y="38" width="10" height="22" rx="3" fill="#0a0a0a" stroke="#374151" strokeWidth="0.5" />
                    <rect x="97" y="42" width="6" height="14" rx="1" fill="#1f2937" />
                    
                    {/* Rear Left */}
                    <rect x="15" y="120" width="10" height="22" rx="3" fill="#0a0a0a" stroke="#374151" strokeWidth="0.5" />
                    <rect x="17" y="124" width="6" height="14" rx="1" fill="#1f2937" />
                    
                    {/* Rear Right */}
                    <rect x="95" y="120" width="10" height="22" rx="3" fill="#0a0a0a" stroke="#374151" strokeWidth="0.5" />
                    <rect x="97" y="124" width="6" height="14" rx="1" fill="#1f2937" />
                </svg>

                {/* Animated Connection Lines */}
                <svg className="absolute inset-0 w-full h-full pointer-events-none">
                    <defs>
                        <linearGradient id="lineGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" stopColor="transparent" />
                            <stop offset="50%" stopColor="rgba(255,255,255,0.3)" />
                            <stop offset="100%" stopColor="transparent" />
                        </linearGradient>
                    </defs>
                    {/* Connection lines from wheels to badges */}
                    <line x1="15%" y1="25%" x2="0%" y2="12%" stroke="url(#lineGrad)" strokeWidth="1" strokeDasharray="4 4" className="animate-pulse" />
                    <line x1="85%" y1="25%" x2="100%" y2="12%" stroke="url(#lineGrad)" strokeWidth="1" strokeDasharray="4 4" className="animate-pulse" />
                    <line x1="15%" y1="75%" x2="0%" y2="88%" stroke="url(#lineGrad)" strokeWidth="1" strokeDasharray="4 4" className="animate-pulse" />
                    <line x1="85%" y1="75%" x2="100%" y2="88%" stroke="url(#lineGrad)" strokeWidth="1" strokeDasharray="4 4" className="animate-pulse" />
                </svg>
            </div>

            {/* Pressure Badges */}
            <PressureBadge value={safePressures.frontLeft} label="Front Left" className="top-[2%] left-0" />
            <PressureBadge value={safePressures.frontRight} label="Front Right" className="top-[2%] right-0" />
            <PressureBadge value={safePressures.rearLeft} label="Rear Left" className="bottom-[2%] left-0" />
            <PressureBadge value={safePressures.rearRight} label="Rear Right" className="bottom-[2%] right-0" />
        </div>
    );
}
