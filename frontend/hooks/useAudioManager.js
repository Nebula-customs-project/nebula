'use client'

import { useRef, useEffect, useCallback, useState } from 'react'

/**
 * useAudioManager Hook
 *
 * Manages background music and sound effects for the car configurator.
 * Features:
 * - Singleton BG music (persists position across page navigations)
 * - Pause on leave, resume on return (without restarting)
 * - Volume ducking (lowers BG music when SFX plays)
 * - User interaction requirement handling (autoplay policies)
 */

// Audio paths
const BG_MUSIC_PATH = '/audio/BG_music_cC.mp3'
const PAINT_SFX_PATH = '/audio/Paint-job.mp3'

// Volume levels (0.0 to 1.0)
const BG_MUSIC_VOLUME = 0.15 // Subtle background, not intrusive
const BG_MUSIC_DUCKED_VOLUME = 0.05 // When SFX is playing
const PAINT_SFX_VOLUME = 0.4 // Sound effect volume

// Module-level singleton for BG music (persists position across navigations)
let bgMusicInstance = null
let bgMusicWasPlaying = false // Track if it was playing before pause

const getBgMusic = () => {
  if (typeof window === 'undefined') return null
  
  if (!bgMusicInstance) {
    bgMusicInstance = new Audio(BG_MUSIC_PATH)
    bgMusicInstance.loop = true
    bgMusicInstance.volume = BG_MUSIC_VOLUME
    bgMusicInstance.preload = 'auto'
  }
  return bgMusicInstance
}

export function useAudioManager() {
  const paintSfxRef = useRef(null)
  const [isAudioEnabled, setIsAudioEnabled] = useState(false)
  const [isBgMusicPlaying, setIsBgMusicPlaying] = useState(false)

  // Initialize paint SFX lazily
  const initializePaintSfx = useCallback(() => {
    if (typeof window === 'undefined') return

    if (!paintSfxRef.current) {
      paintSfxRef.current = new Audio(PAINT_SFX_PATH)
      paintSfxRef.current.volume = PAINT_SFX_VOLUME
      paintSfxRef.current.preload = 'auto'
    }
  }, [])

  // Start or resume background music
  const startBgMusic = useCallback(() => {
    const bgMusic = getBgMusic()
    if (!bgMusic) return

    // Check if already playing
    if (!bgMusic.paused) {
      setIsBgMusicPlaying(true)
      setIsAudioEnabled(true)
      return
    }

    bgMusic.play()
      .then(() => {
        bgMusicWasPlaying = true
        setIsBgMusicPlaying(true)
        setIsAudioEnabled(true)
      })
      .catch((err) => {
        console.log('BG music autoplay blocked:', err.message)
      })
  }, [])

  // Pause background music (preserves position for resume)
  const pauseBgMusic = useCallback(() => {
    const bgMusic = getBgMusic()
    if (bgMusic && !bgMusic.paused) {
      bgMusic.pause()
      // Don't reset currentTime - preserve position for resume
      setIsBgMusicPlaying(false)
    }
  }, [])

  // Stop and reset background music
  const stopBgMusic = useCallback(() => {
    const bgMusic = getBgMusic()
    if (bgMusic) {
      bgMusic.pause()
      bgMusic.currentTime = 0
      bgMusicWasPlaying = false
      setIsBgMusicPlaying(false)
    }
  }, [])

  // Play paint job sound effect with callback when complete
  const playPaintSfx = useCallback((onComplete) => {
    initializePaintSfx()

    if (!paintSfxRef.current) return onComplete?.()

    const bgMusic = getBgMusic()

    // Duck the background music
    if (bgMusic && !bgMusic.paused) {
      bgMusic.volume = BG_MUSIC_DUCKED_VOLUME
    }

    paintSfxRef.current.currentTime = 0

    const handleEnded = () => {
      if (bgMusic) {
        bgMusic.volume = BG_MUSIC_VOLUME
      }
      onComplete?.()
      paintSfxRef.current.removeEventListener('ended', handleEnded)
    }

    paintSfxRef.current.addEventListener('ended', handleEnded)

    paintSfxRef.current.play().catch((err) => {
      console.log('Paint SFX play failed:', err.message)
      if (bgMusic) {
        bgMusic.volume = BG_MUSIC_VOLUME
      }
      onComplete?.()
    })
  }, [initializePaintSfx])

  // Enable audio on first user interaction
  const enableAudioOnInteraction = useCallback(() => {
    if (!isAudioEnabled) {
      startBgMusic()
    }
  }, [isAudioEnabled, startBgMusic])

  // Cleanup paint SFX on unmount, pause BG music when leaving page
  useEffect(() => {
    return () => {
      if (paintSfxRef.current) {
        paintSfxRef.current.pause()
        paintSfxRef.current = null
      }
      // Pause BG music when leaving the page (but preserve position)
      const bgMusic = getBgMusic()
      if (bgMusic && !bgMusic.paused) {
        bgMusic.pause()
      }
    }
  }, [])

  return {
    startBgMusic,
    pauseBgMusic,
    stopBgMusic,
    playPaintSfx,
    enableAudioOnInteraction,
    isAudioEnabled,
    isBgMusicPlaying,
  }
}


