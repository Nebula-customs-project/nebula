"use client";

/**
 * OrderSuccessPopup Component
 *
 * Animated celebration popup shown when user clicks "Save and Order"
 * Features:
 * - Car delivery animation (car moving towards house)
 * - Sparkle/confetti particle effects
 * - Auto-close after 4 seconds
 */

import React, { useEffect, useState } from "react";

export default function OrderSuccessPopup({ isOpen, onClose }) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (isOpen) {
      // Trigger entrance animation
      setTimeout(() => setIsVisible(true), 10);

      // Auto-close after 4 seconds
      const timer = setTimeout(() => {
        handleClose();
      }, 4000);

      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onClose(), 300); // Wait for exit animation
  };

  if (!isOpen) return null;

  return (
    <div
      className={`fixed inset-0 z-50 flex items-center justify-center transition-all duration-300 ${
        isVisible ? "opacity-100" : "opacity-0"
      }`}
    >
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={handleClose}
      />

      {/* Modal */}
      <div
        className={`relative bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 rounded-2xl border border-gray-700 shadow-2xl p-8 max-w-md w-full mx-4 overflow-hidden transition-all duration-300 ${
          isVisible ? "scale-100 translate-y-0" : "scale-95 translate-y-4"
        }`}
      >
        {/* Sparkle particles */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          {[...Array(12)].map((_, i) => (
            <div
              key={i}
              className="absolute w-2 h-2 bg-yellow-400 rounded-full animate-sparkle"
              style={{
                left: `${10 + i * 7}%`,
                top: `${20 + (i % 3) * 25}%`,
                animationDelay: `${i * 0.15}s`,
                opacity: 0.8,
              }}
            />
          ))}
        </div>

        {/* Glow effect behind content */}
        <div className="absolute inset-0 bg-gradient-to-r from-red-500/10 via-green-500/10 to-red-500/10 animate-pulse" />

        {/* Content */}
        <div className="relative z-10 text-center">
          {/* Celebration emoji header */}
          <div className="text-4xl mb-4 animate-bounce">🎉</div>

          {/* Main message */}
          <h2 className="text-2xl font-bold text-white mb-2">Yay!</h2>
          <p className="text-gray-300 text-lg mb-6">
            Your supercar is on its way to your home!
          </p>

          {/* Close button */}
          <button
            onClick={handleClose}
            className="px-8 py-3 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-500 hover:to-green-600 rounded-xl text-white font-bold transition-all duration-200 shadow-lg shadow-green-500/30 hover:shadow-green-500/50 transform hover:scale-105"
          >
            Got it! 🚀
          </button>
        </div>

        {/* Corner accents */}
        <div className="absolute top-0 left-0 w-16 h-16 bg-gradient-to-br from-red-500/20 to-transparent rounded-tl-2xl" />
        <div className="absolute top-0 right-0 w-16 h-16 bg-gradient-to-bl from-green-500/20 to-transparent rounded-tr-2xl" />
        <div className="absolute bottom-0 left-0 w-16 h-16 bg-gradient-to-tr from-green-500/20 to-transparent rounded-bl-2xl" />
        <div className="absolute bottom-0 right-0 w-16 h-16 bg-gradient-to-tl from-red-500/20 to-transparent rounded-br-2xl" />
      </div>

      {/* CSS Animations */}
      <style jsx>{`
        @keyframes drive {
          0% {
            left: -10%;
          }
          100% {
            left: 65%;
          }
        }

        @keyframes sparkle {
          0%,
          100% {
            transform: scale(0) rotate(0deg);
            opacity: 0;
          }
          50% {
            transform: scale(1) rotate(180deg);
            opacity: 1;
          }
        }

        .animate-drive {
          animation: drive 2.5s ease-out forwards;
        }

        .animate-sparkle {
          animation: sparkle 1.5s ease-in-out infinite;
        }
      `}</style>
    </div>
  );
}
