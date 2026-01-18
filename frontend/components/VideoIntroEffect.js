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
 * After the video ends, it performs a smooth "lift up" transition
 * before revealing the 3D scene underneath.
 */

const STORAGE_KEY = "nebula-car-configurator-intro-seen";

export default function VideoIntroEffect({ onComplete, videoSrc }) {
  const [showIntro, setShowIntro] = useState(false);
  const [isExiting, setIsExiting] = useState(false);
  const [videoLoaded, setVideoLoaded] = useState(false);
  const videoRef = useRef(null);

  // Check if user has seen the intro before
  useEffect(() => {
    // Check localStorage for first visit
    const hasSeenIntro = localStorage.getItem(STORAGE_KEY);

    if (!hasSeenIntro) {
      setShowIntro(true);
    } else {
      // User has seen intro, skip directly
      onComplete?.();
    }
  }, [onComplete]);

  // Handle video loaded
  const handleVideoLoaded = useCallback(() => {
    setVideoLoaded(true);
    // Start playing the video
    if (videoRef.current) {
      videoRef.current.play().catch((err) => {
        console.warn("Video autoplay failed:", err);
        // If autoplay fails, skip the intro
        handleIntroComplete();
      });
    }
  }, []);

  // Handle intro completion
  const handleIntroComplete = useCallback(() => {
    // Mark as seen in localStorage
    localStorage.setItem(STORAGE_KEY, "true");

    // Start exit animation (lift up)
    setIsExiting(true);

    // After exit animation completes, call onComplete
    setTimeout(() => {
      setShowIntro(false);
      onComplete?.();
    }, 800); // Match the CSS transition duration
  }, [onComplete]);

  // Handle video ended
  const handleVideoEnded = useCallback(() => {
    handleIntroComplete();
  }, [handleIntroComplete]);

  // Handle video error - skip intro on error
  const handleVideoError = useCallback(() => {
    console.warn("Video failed to load, skipping intro");
    localStorage.setItem(STORAGE_KEY, "true");
    setShowIntro(false);
    onComplete?.();
  }, [onComplete]);

  // Handle skip button click
  const handleSkip = useCallback(() => {
    if (videoRef.current) {
      videoRef.current.pause();
    }
    handleIntroComplete();
  }, [handleIntroComplete]);

  // Don't render if intro shouldn't show
  if (!showIntro) {
    return null;
  }

  return (
    <div
      className={`fixed inset-0 z-[100] bg-black flex items-center justify-center transition-transform duration-700 ease-out ${
        isExiting ? "-translate-y-full" : "translate-y-0"
      }`}
    >
      {/* Video Container */}
      <div className="relative w-full h-full">
        {/* Video Element */}
        <video
          ref={videoRef}
          className="absolute inset-0 w-full h-full object-cover"
          src={videoSrc}
          muted
          playsInline
          preload="auto"
          onLoadedData={handleVideoLoaded}
          onEnded={handleVideoEnded}
          onError={handleVideoError}
        />

        {/* Loading State (before video loads) */}
        {!videoLoaded && (
          <div className="absolute inset-0 flex items-center justify-center bg-black">
            <div className="text-center">
              <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-gray-400 text-sm">Loading experience...</p>
            </div>
          </div>
        )}

        {/* Skip Button */}
        <button
          onClick={handleSkip}
          className="absolute bottom-8 right-8 px-6 py-2 bg-black/50 hover:bg-black/70 backdrop-blur-sm border border-white/20 rounded-lg text-white text-sm font-medium transition-all duration-200 hover:border-red-500/50 z-10"
        >
          Skip Intro
        </button>

        {/* Progress indicator */}
        {videoLoaded && videoRef.current && (
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
  videoSrc: PropTypes.string,
};

VideoIntroEffect.defaultProps = {
  videoSrc: "/videos/car-intro.mp4",
};

VideoProgress.propTypes = {
  videoRef: PropTypes.shape({
    current: PropTypes.instanceOf(
      typeof HTMLVideoElement !== "undefined" ? HTMLVideoElement : Object,
    ),
  }).isRequired,
};
