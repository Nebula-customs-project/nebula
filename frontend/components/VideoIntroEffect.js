"use client";

import React, { useState, useEffect, useRef, useCallback } from "react";
import PropTypes from "prop-types";

/**
 * VideoIntroEffect Component
 *
 * Plays a cinematic intro video animation on the user's FIRST visit
 * to the car configurator page. Uses localStorage to track if the
 * user has already seen the intro.
 *
 * Features a premium exit transition with:
 * - Red energy pulse expanding from center
 * - Zoom blur effect
 * - Particle dispersion
 * - Smooth fade out
 */

const STORAGE_KEY = "nebula-car-configurator-intro-seen";

export default function VideoIntroEffect({ onComplete, onStart, videoSrc }) {
  const [showIntro, setShowIntro] = useState(false);
  const [exitPhase, setExitPhase] = useState(0); // 0: playing, 1: pulse, 2: zoom, 3: fade, 4: done
  const [videoLoaded, setVideoLoaded] = useState(false);
  const videoRef = useRef(null);

  // Check if user has seen the intro before
  useEffect(() => {
    const hasSeenIntro = localStorage.getItem(STORAGE_KEY);

    if (!hasSeenIntro) {
      setShowIntro(true);
    } else {
      // User has seen intro - skip but still trigger callbacks
      onStart?.(); // Start BG music even on return visits
      onComplete?.();
    }
  }, [onComplete, onStart]);

  // Handle video loaded
  const handleVideoLoaded = useCallback(() => {
    setVideoLoaded(true);
    if (videoRef.current) {
      videoRef.current
        .play()
        .then(() => {
          // Video started playing - trigger onStart callback
          onStart?.();
        })
        .catch((err) => {
          console.warn("Video autoplay failed:", err);
          handleIntroComplete();
        });
    }
  }, [onStart]);

  // Premium exit animation sequence - smooth timing
  const handleIntroComplete = useCallback(() => {
    localStorage.setItem(STORAGE_KEY, "true");

    // Phase 1: Red energy pulse (0ms)
    setExitPhase(1);

    // Phase 2: Zoom blur (600ms)
    setTimeout(() => setExitPhase(2), 600);

    // Phase 3: Fade out (1200ms)
    setTimeout(() => setExitPhase(3), 1200);

    // Phase 4: Complete (1800ms)
    setTimeout(() => {
      setExitPhase(4);
      setShowIntro(false);
      onComplete?.();
    }, 1800);
  }, [onComplete]);

  const handleVideoEnded = useCallback(() => {
    handleIntroComplete();
  }, [handleIntroComplete]);

  const handleVideoError = useCallback(() => {
    console.warn("Video failed to load, skipping intro");
    localStorage.setItem(STORAGE_KEY, "true");
    setShowIntro(false);
    onComplete?.();
  }, [onComplete]);

  const handleSkip = useCallback(() => {
    if (videoRef.current) {
      videoRef.current.pause();
    }
    // User clicked - this gesture enables audio, so trigger onStart
    onStart?.();
    handleIntroComplete();
  }, [handleIntroComplete, onStart]);

  if (!showIntro) {
    return null;
  }

  return (
    <div
      className={`fixed inset-0 z-[100] bg-black flex items-center justify-center overflow-hidden
        ${exitPhase >= 2 ? "scale-110" : "scale-100"}
        ${exitPhase >= 3 ? "opacity-0" : "opacity-100"}
      `}
      style={{
        transition:
          "transform 0.8s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.6s ease-out",
        filter: exitPhase >= 2 ? "blur(12px)" : "blur(0px)",
      }}
    >
      {/* Video Container - clicking anywhere enables audio */}
      <div className="relative w-full h-full" onClick={() => onStart?.()}>
        {/* Video Element */}
        <video
          ref={videoRef}
          className={`absolute inset-0 w-full h-full object-cover transition-all duration-500
            ${exitPhase >= 1 ? "brightness-150" : "brightness-100"}
          `}
          src={videoSrc}
          muted
          playsInline
          preload="auto"
          onLoadedData={handleVideoLoaded}
          onEnded={handleVideoEnded}
          onError={handleVideoError}
        />

        {/* Loading State */}
        {!videoLoaded && (
          <div className="absolute inset-0 flex items-center justify-center bg-black">
            <div className="text-center">
              <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-gray-400 text-sm">Loading experience...</p>
            </div>
          </div>
        )}

        {/* RED ENERGY PULSE - Expanding circle from center */}
        <div
          className={`absolute inset-0 pointer-events-none flex items-center justify-center
            ${exitPhase >= 1 ? "opacity-100" : "opacity-0"}
          `}
        >
          <div
            className={`rounded-full bg-gradient-radial from-red-500/60 via-red-600/30 to-transparent
              ${exitPhase >= 1 ? "w-[300vmax] h-[300vmax]" : "w-0 h-0"}
            `}
            style={{
              transition: "all 1s cubic-bezier(0.16, 1, 0.3, 1)",
              boxShadow:
                exitPhase >= 1
                  ? "0 0 200px 100px rgba(239, 68, 68, 0.4)"
                  : "none",
            }}
          />
        </div>

        {/* HORIZONTAL LIGHT STREAKS */}
        {exitPhase >= 1 && (
          <div className="absolute inset-0 pointer-events-none">
            <div
              className="absolute top-1/2 left-0 right-0 h-px bg-gradient-to-r from-transparent via-red-500 to-transparent"
              style={{
                transform: `scaleX(${exitPhase >= 2 ? 3 : 0})`,
                opacity: exitPhase >= 3 ? 0 : 1,
                transition: "all 0.8s cubic-bezier(0.16, 1, 0.3, 1)",
                boxShadow: "0 0 30px 10px rgba(239, 68, 68, 0.5)",
              }}
            />
            <div
              className="absolute top-1/2 left-0 right-0 h-1 bg-gradient-to-r from-transparent via-white to-transparent"
              style={{
                transform: `scaleX(${exitPhase >= 2 ? 3 : 0})`,
                opacity: exitPhase >= 3 ? 0 : 0.6,
                transition: "all 0.7s cubic-bezier(0.16, 1, 0.3, 1)",
              }}
            />
          </div>
        )}

        {/* VIGNETTE OVERLAY */}
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            background:
              exitPhase >= 1
                ? "radial-gradient(circle at center, transparent 0%, rgba(0,0,0,0.8) 100%)"
                : "none",
            opacity: exitPhase >= 3 ? 0 : 1,
            transition: "opacity 0.6s ease-out",
          }}
        />

        {/* Skip Button */}
        {exitPhase === 0 && (
          <button
            onClick={handleSkip}
            className="absolute bottom-8 right-8 px-6 py-2 bg-black/50 hover:bg-black/70 backdrop-blur-sm border border-white/20 rounded-lg text-white text-sm font-medium transition-all duration-200 hover:border-red-500/50 z-10"
          >
            Skip Intro
          </button>
        )}

        {/* Progress indicator */}
        {videoLoaded && exitPhase === 0 && videoRef.current && (
          <VideoProgress videoRef={videoRef} />
        )}
      </div>
    </div>
  );
}

/**
 * VideoProgress - Shows a subtle progress bar for the video
 */
function VideoProgress({ videoRef }) {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;

    const updateProgress = () => {
      if (video.duration) {
        setProgress((video.currentTime / video.duration) * 100);
      }
    };

    video.addEventListener("timeupdate", updateProgress);
    return () => video.removeEventListener("timeupdate", updateProgress);
  }, [videoRef]);

  return (
    <div className="absolute bottom-0 left-0 right-0 h-1 bg-black/50">
      <div
        className="h-full bg-gradient-to-r from-red-500 to-red-600 transition-all duration-100"
        style={{ width: `${progress}%` }}
      />
    </div>
  );
}

VideoIntroEffect.propTypes = {
  onComplete: PropTypes.func.isRequired,
  onStart: PropTypes.func,
  videoSrc: PropTypes.string,
};

VideoIntroEffect.defaultProps = {
  onStart: () => {},
  videoSrc: "/videos/car-intro.mp4",
};

VideoProgress.propTypes = {
  videoRef: PropTypes.shape({
    current: PropTypes.instanceOf(
      typeof HTMLVideoElement !== "undefined" ? HTMLVideoElement : Object,
    ),
  }).isRequired,
};
