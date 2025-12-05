"use client";

import React, { useState } from "react";
import Image from "next/image";

interface Vehicle3DViewerProps {
  vehicleName: string;
  configuration: Record<string, string>;
}

const Vehicle3DViewer: React.FC<Vehicle3DViewerProps> = ({
  vehicleName,
  configuration,
}) => {
  const [imageError, setImageError] = useState(false);

  // Enhanced color filter system for realistic car paint effects
  const getColorFilter = (visualKey: string) => {
    const colorFilters: Record<
      string,
      { name: string; filter: string; hex: string; cssFilter: string }
    > = {
      "metallic-red": {
        name: "Metallic Red",
        hex: "#DC2626",
        filter: "brightness(0.9) saturate(1.3) hue-rotate(0deg)",
        cssFilter:
          "sepia(100%) saturate(300%) brightness(90%) hue-rotate(330deg)",
      },
      "midnight-black": {
        name: "Midnight Black",
        hex: "#1a1a1a",
        filter: "brightness(0.4) saturate(0.2)",
        cssFilter: "brightness(40%) saturate(20%)",
      },
      "pearl-white": {
        name: "Pearl White",
        hex: "#F8FAFC",
        filter: "brightness(1.2) saturate(0.3)",
        cssFilter: "brightness(120%) saturate(30%)",
      },
      "electric-blue": {
        name: "Electric Blue",
        hex: "#3B82F6",
        filter: "brightness(0.9) saturate(1.5) hue-rotate(200deg)",
        cssFilter:
          "sepia(100%) saturate(300%) brightness(90%) hue-rotate(200deg)",
      },
      "sunset-orange": {
        name: "Sunset Orange",
        hex: "#F97316",
        filter: "brightness(1.0) saturate(1.4) hue-rotate(20deg)",
        cssFilter:
          "sepia(100%) saturate(300%) brightness(100%) hue-rotate(10deg)",
      },
      "chrome-silver": {
        name: "Chrome Silver",
        hex: "#9CA3AF",
        filter: "brightness(1.1) saturate(0.5)",
        cssFilter: "brightness(110%) saturate(50%)",
      },
    };

    return (
      colorFilters[visualKey] || {
        name: "Default",
        hex: "#6B7280",
        filter: "none",
        cssFilter: "none",
      }
    );
  };

  const currentColor = getColorFilter(configuration.paint);

  // Get part name for display
  const getPartName = (visualKey: string) => {
    return visualKey
      .split("-")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");
  };

  // Check if specific part images exist
  const hasCustomImage = (category: string, visualKey: string) => {
    // This will be true when user adds their own images
    return false; // For now, we'll use the default car with overlays
  };

  return (
    <div className="flex flex-col items-center justify-center h-full bg-linear-to-br from-gray-900 via-gray-800 to-black p-6">
      <div className="relative w-full max-w-6xl">
        {/* Header */}
        <div className="text-center mb-6">
          <h2 className="text-5xl font-bold text-white mb-3 tracking-tight drop-shadow-lg">
            {vehicleName}
          </h2>
          <p className="text-gray-400 text-base flex items-center justify-center gap-2">
            <span className="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></span>
            Real-Time Photo Customization
          </p>
        </div>

        {/* Main Showroom Display */}
        <div className="relative rounded-3xl overflow-hidden border border-gray-700/50 shadow-2xl bg-linear-to-b from-gray-800 via-gray-900 to-black">
          {/* Dynamic Ambient Glow */}
          <div
            className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] opacity-20 blur-3xl transition-all duration-700 rounded-full"
            style={{ backgroundColor: currentColor.hex }}
          ></div>

          {/* Showroom Floor with Reflection */}
          <div className="absolute bottom-0 left-0 right-0 h-48 bg-linear-to-t from-black/80 via-transparent to-transparent"></div>

          {/* Car Display Container */}
          <div className="relative z-10 py-12 px-8 min-h-[600px] flex items-center justify-center">
            <div className="relative w-full max-w-5xl">
              {/* Photo-realistic Car Display */}
              <div className="relative aspect-video w-full">
                {/* INSTRUCTIONS OVERLAY - Shows when no custom image */}
                {imageError && (
                  <div className="absolute inset-0 flex items-center justify-center bg-gray-800/90 backdrop-blur-sm rounded-2xl border-2 border-dashed border-gray-600 z-50">
                    <div className="text-center p-8 max-w-2xl">
                      <div className="text-6xl mb-4">🚗</div>
                      <h3 className="text-2xl font-bold text-white mb-4">
                        Add Your Real Car Photo Here!
                      </h3>
                      <div className="text-left bg-gray-900/80 rounded-lg p-6 mb-4">
                        <p className="text-gray-300 mb-4 font-semibold">
                          Quick Setup (2 minutes):
                        </p>
                        <ol className="text-sm text-gray-300 space-y-2 list-decimal list-inside">
                          <li>
                            Download a <strong>side-view</strong> car photo
                            (like your Mustang image)
                          </li>
                          <li>
                            Save it as:{" "}
                            <code className="bg-gray-800 px-2 py-1 rounded text-blue-300">
                              public/cars/base/car-side.png
                            </code>
                          </li>
                          <li>
                            Refresh the page - your real car will appear!
                          </li>
                          <li>
                            Colors will change automatically using CSS filters
                          </li>
                        </ol>
                      </div>
                      <p className="text-xs text-gray-400">
                        📖 See REAL_CAR_SETUP_GUIDE.md for detailed
                        instructions
                      </p>
                    </div>
                  </div>
                )}

                {/* Layer 1: Base Car Photo with Color Filter */}
                <div className="relative w-full h-full group">
                  {/* Real Car Photo */}
                  <div
                    className="relative w-full h-full transition-all duration-700 transform hover:scale-[1.02]"
                    style={{
                      filter: `${currentColor.cssFilter} drop-shadow(0 30px 60px ${currentColor.hex}60)`,
                    }}
                  >
                    <Image
                      src="/cars/base/car-side.png"
                      alt="Custom Vehicle"
                      fill
                      className="object-contain"
                      priority
                      onError={() => setImageError(true)}
                    />
                  </div>

                  {/* Color Overlay for Enhanced Paint Effect */}
                  <div
                    className="absolute inset-0 mix-blend-overlay opacity-25 transition-all duration-700 pointer-events-none rounded-2xl"
                    style={{ backgroundColor: currentColor.hex }}
                  ></div>

                  {/* SVG Fallback - Only shows if image fails to load */}
                  {imageError && (
                    <div className="absolute inset-0 w-full h-full flex items-center justify-center bg-linear-to-b from-gray-800/30 to-gray-900/50 rounded-2xl">
                      <div className="relative w-[90%] h-[90%]">
                        <svg
                          viewBox="0 0 1200 600"
                          className="w-full h-full drop-shadow-2xl"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <defs>
                            <linearGradient
                              id="carBody"
                              x1="0%"
                              y1="0%"
                              x2="0%"
                              y2="100%"
                            >
                              <stop offset="0%" stopColor={currentColor.hex} stopOpacity="1" />
                              <stop offset="50%" stopColor={currentColor.hex} stopOpacity="0.9" />
                              <stop offset="100%" stopColor={currentColor.hex} stopOpacity="0.7" />
                            </linearGradient>
                            <radialGradient id="shine">
                              <stop offset="0%" stopColor="white" stopOpacity="0.6" />
                              <stop offset="100%" stopColor="white" stopOpacity="0" />
                            </radialGradient>
                          </defs>

                          {/* Realistic Sports Car Silhouette */}
                          <ellipse cx="600" cy="530" rx="380" ry="40" fill="black" opacity="0.4" />
                          
                          {/* Main Body */}
                          <path
                            d="M 250 380 L 300 350 L 950 350 L 980 380 L 980 450 L 250 450 Z"
                            fill="url(#carBody)"
                            stroke="#000"
                            strokeWidth="4"
                          />
                          <path
                            d="M 320 350 L 380 260 L 520 240 L 680 240 L 800 280 L 870 350"
                            fill="url(#carBody)"
                            stroke="#000"
                            strokeWidth="4"
                          />

                          {/* Shine Effect */}
                          <ellipse cx="650" cy="320" rx="220" ry="100" fill="url(#shine)" />

                          {/* Windows */}
                          <path d="M 395 265 L 430 255 L 530 250 L 560 265 Z" fill="#4A90E2" opacity="0.5" />
                          <path d="M 570 265 L 600 255 L 690 260 L 710 275 Z" fill="#4A90E2" opacity="0.5" />

                          {/* Wheels */}
                          <circle cx="380" cy="450" r="68" fill="#1a1a1a" stroke="#000" strokeWidth="8" />
                          <circle cx="380" cy="450" r="45" fill="#555" />
                          <circle cx="850" cy="450" r="68" fill="#1a1a1a" stroke="#000" strokeWidth="8" />
                          <circle cx="850" cy="450" r="45" fill="#555" />

                          {/* Headlights */}
                          <ellipse cx="960" cy="400" rx="22" ry="32" fill="#FFE66D" opacity="0.9" />
                          
                          {/* Spoiler */}
                          {configuration.spoilers !== "none" && (
                            <g>
                              <rect x="200" y="300" width="18" height="55" fill="#222" />
                              <rect x="250" y="300" width="18" height="55" fill="#222" />
                              <rect x="180" y="280" width="110" height="24" rx="4" fill="#1a1a1a" stroke="#000" strokeWidth="3" />
                            </g>
                          )}

                          {/* Performance Bumper */}
                          {configuration.bumpers !== "stock" && (
                            <g>
                              <path d="M 980 410 L 1020 410 L 1030 390 L 1020 370 L 980 370" fill="#1a1a1a" stroke="#000" strokeWidth="3" />
                              <rect x="990" y="380" width="30" height="6" fill="#333" />
                              <rect x="990" y="390" width="30" height="6" fill="#333" />
                              <rect x="990" y="400" width="30" height="6" fill="#333" />
                            </g>
                          )}

                          {/* Exhaust */}
                          {configuration.exhaust === "racing" || configuration.exhaust === "titanium" ? (
                            <g>
                              <circle cx="240" cy="440" r="14" fill="#333" stroke="#555" strokeWidth="3" />
                              <circle cx="240" cy="470" r="14" fill="#333" stroke="#555" strokeWidth="3" />
                            </g>
                          ) : (
                            <circle cx="240" cy="455" r="12" fill="#333" stroke="#555" strokeWidth="3" />
                          )}
                        </svg>

                        {/* Instruction Text */}
                        <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                          <div className="bg-black/90 backdrop-blur-md px-6 py-4 rounded-xl border border-blue-500/50 max-w-md">
                            <p className="text-white text-sm font-semibold mb-2">
                              🎨 Image Failed to Load
                            </p>
                            <p className="text-gray-300 text-xs leading-relaxed">
                              Make sure your image exists at: <code className="text-green-300">public/cars/base/car-side.png</code>
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Status Badges */}
              <div className="absolute top-4 left-4 bg-black/70 backdrop-blur-md px-4 py-2.5 rounded-full border border-gray-600 flex items-center gap-2 shadow-lg">
                <span className="relative flex h-3 w-3">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-3 w-3 bg-green-500"></span>
                </span>
                <span className="text-gray-200 text-xs font-bold">
                  PHOTO MODE
                </span>
              </div>

              <div className="absolute top-4 right-4 bg-black/70 backdrop-blur-md px-4 py-2 rounded-full border border-blue-500/50">
                <span className="text-blue-300 text-xs font-medium">
                  {currentColor.name}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Configuration Display Cards */}
        <div className="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4">
          <div className="group bg-gray-800/70 backdrop-blur-md rounded-xl p-4 border border-gray-700/60 hover:border-blue-500/60 hover:bg-gray-800/90 transition-all">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg">🎨</span>
              <p className="text-gray-400 text-xs uppercase tracking-wider font-bold">
                Paint
              </p>
            </div>
            <div className="flex items-center gap-2.5">
              <div
                className="w-6 h-6 rounded-full border-2 border-gray-600 shadow-lg group-hover:scale-110 transition-transform"
                style={{
                  backgroundColor: currentColor.hex,
                  boxShadow: `0 0 20px ${currentColor.hex}60`,
                }}
              ></div>
              <p className="text-white text-sm font-bold">
                {currentColor.name}
              </p>
            </div>
          </div>

          <div className="group bg-gray-800/70 backdrop-blur-md rounded-xl p-4 border border-gray-700/60 hover:border-blue-500/60 hover:bg-gray-800/90 transition-all">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg">⭕</span>
              <p className="text-gray-400 text-xs uppercase tracking-wider font-bold">
                Rims
              </p>
            </div>
            <p className="text-white text-sm font-bold">
              {getPartName(configuration.rims)}
            </p>
          </div>

          <div className="group bg-gray-800/70 backdrop-blur-md rounded-xl p-4 border border-gray-700/60 hover:border-blue-500/60 hover:bg-gray-800/90 transition-all">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg">🛫</span>
              <p className="text-gray-400 text-xs uppercase tracking-wider font-bold">
                Spoiler
              </p>
            </div>
            <p className="text-white text-sm font-bold">
              {getPartName(configuration.spoilers)}
            </p>
          </div>

          <div className="group bg-gray-800/70 backdrop-blur-md rounded-xl p-4 border border-gray-700/60 hover:border-blue-500/60 hover:bg-gray-800/90 transition-all">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg">⚙️</span>
              <p className="text-gray-400 text-xs uppercase tracking-wider font-bold">
                Engine
              </p>
            </div>
            <p className="text-white text-sm font-bold">
              {getPartName(configuration.engine)}
            </p>
          </div>
        </div>

        {/* Setup Instructions Banner */}
        <div className="mt-6 bg-linear-to-r from-blue-500/10 via-purple-500/10 to-blue-500/10 border border-blue-500/30 rounded-xl p-6 backdrop-blur-sm">
          <div className="flex items-start gap-4">
            <span className="text-4xl">📸</span>
            <div className="flex-1">
              <p className="text-blue-300 text-lg font-bold mb-3">
                Ready for Your Real Car Photo!
              </p>
              <div className="space-y-2 text-sm text-gray-300">
                <p>
                  This system uses <strong className="text-white">CSS filters</strong> to change your car's color in real-time.
                </p>
                <div className="bg-gray-800/50 rounded-lg p-4 mt-3">
                  <p className="font-semibold text-blue-300 mb-2">
                    Quick Setup (Like Your Mustang Image):
                  </p>
                  <ol className="list-decimal list-inside space-y-1.5 text-xs">
                    <li>
                      Save your car photo as:{" "}
                      <code className="bg-gray-900 px-2 py-0.5 rounded text-green-300">
                        public/cars/base/car-side.png
                      </code>
                    </li>
                    <li>Make sure it's a <strong>side-view</strong> angle (45°)</li>
                    <li>Preferably <strong>white or silver</strong> car for best color changes</li>
                    <li>Resolution: At least <strong>1920x1080px</strong></li>
                    <li>Format: <strong>PNG</strong> (with transparent background) or <strong>JPG</strong></li>
                  </ol>
                </div>
                <p className="text-xs text-gray-400 mt-3">
                  💡 The system will automatically apply color filters to match
                  your selected paint color!
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Vehicle3DViewer;
