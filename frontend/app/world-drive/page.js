'use client'

import { useState, useEffect, useRef, useCallback } from 'react'
import dynamic from 'next/dynamic'
import { worldDriveApi } from './lib/api'

// Dynamic imports for UI components
const MapView = dynamic(() => import('./components/MapView'), { ssr: false })
const JourneyPanel = dynamic(() => import('./components/hud/JourneyPanel'), { ssr: false })
const Speedometer = dynamic(() => import('./components/hud/Speedometer'), { ssr: false })
const ProgressBar3D = dynamic(() => import('./components/hud/ProgressBar3D'), { ssr: false })
const MapControls = dynamic(() => import('./components/hud/MapControls'), { ssr: false })

// Dealership coordinates
const DEALERSHIP_LOCATION = {
  lat: 48.8354,
  lng: 9.152,
}

// Polling interval for checking current journey (in ms)
const JOURNEY_POLL_INTERVAL = 2000

export default function WorldDrivePage() {
  // State
  const [journeyState, setJourneyState] = useState(null)
  const [currentPosition, setCurrentPosition] = useState(null)
  const [isConnecting, setIsConnecting] = useState(true)
  const [error, setError] = useState(null)
  const [mqttError, setMqttError] = useState(null)
  const [isMapVisible, setIsMapVisible] = useState(false)
  const [isFollowing, setIsFollowing] = useState(true)

  // Refs
  const videoRef = useRef(null)
  const cleanupRef = useRef(null)
  const pollIntervalRef = useRef(null)
  const currentJourneyIdRef = useRef(null)

  // Calculate derived values
  const status = journeyState?.status || 'WAITING'
  const progress = journeyState?.progress_percentage || 0
  const speedKmh = (journeyState?.speed_meters_per_second || 0) * 3.6

  const currentRoute = journeyState?.route || null
  const distanceRemaining = currentRoute
    ? currentRoute.total_distance_meters * (1 - progress / 100)
    : 0
  const estimatedTimeRemaining = speedKmh > 0
    ? (distanceRemaining / 1000) / speedKmh * 3600
    : 0

  // Get waypoints from current route
  const waypoints = currentRoute?.waypoints?.map(wp => ({
    lat: wp.latitude,
    lng: wp.longitude,
  })) || []

  const startPoint = currentRoute?.start_point
    ? { lat: currentRoute.start_point.latitude, lng: currentRoute.start_point.longitude }
    : DEALERSHIP_LOCATION

  // Video loop logic: handle loop from 8 seconds and delay map visibility
  useEffect(() => {
    const video = videoRef.current
    if (!video) return

    const handleTimeUpdate = () => {
      // Show map when interior is visible (after 3 seconds)
      if (video.currentTime >= 3 && !isMapVisible) {
        setIsMapVisible(true)
      } else if (video.currentTime < 3 && isMapVisible) {
        setIsMapVisible(false)
      }
    }

    const handleEnded = () => {
      console.log('Video ended, looping back to 8s')
      video.currentTime = 8
      video.play().catch(err => console.error('Video play error:', err))
    }

    video.addEventListener('timeupdate', handleTimeUpdate)
    video.addEventListener('ended', handleEnded)
    return () => {
      video.removeEventListener('timeupdate', handleTimeUpdate)
      video.removeEventListener('ended', handleEnded)
    }
  }, [isMapVisible])

  // Handle coordinate updates from MQTT
  const handleCoordinateUpdate = useCallback((update) => {
    setCurrentPosition({
      lat: update.coordinate.latitude,
      lng: update.coordinate.longitude,
    })

    setJourneyState(prev => {
      if (!prev) return prev
      return {
        ...prev,
        current_position: update.coordinate,
        current_waypoint_index: update.current_waypoint_index,
        progress_percentage: update.progress_percentage,
        status: update.status,
        speed_meters_per_second: update.speed_meters_per_second || prev.speed_meters_per_second,
      }
    })
  }, [])

  // Cleanup MQTT connection
  const cleanupConnection = useCallback(() => {
    if (cleanupRef.current) {
      cleanupRef.current()
      cleanupRef.current = null
    }
  }, [])

  // Subscribe to journey updates
  const subscribeToJourney = useCallback((journeyId) => {
    if (currentJourneyIdRef.current === journeyId && cleanupRef.current) return

    cleanupConnection()
    currentJourneyIdRef.current = journeyId

    try {
      cleanupRef.current = worldDriveApi.subscribeToJourney(
        journeyId,
        handleCoordinateUpdate,
        {
          onError: (err) => {
            console.error('MQTT connection error:', err)
            setMqttError(err.message || 'Failed to connect to MQTT broker')
          },
          onEvent: (event) => {
            if (event.type === 'mqtt-error') {
              setMqttError(event.message || 'MQTT connection failed')
            } else if (event.type === 'journey-completed') {
              setMqttError(null)
            }
          }
        }
      )
      setMqttError(null)
    } catch (err) {
      setMqttError(err.message || 'Failed to connect to MQTT broker.')
    }
  }, [handleCoordinateUpdate, cleanupConnection])

  // Poll for current journey
  const pollCurrentJourney = useCallback(async () => {
    try {
      const journey = await worldDriveApi.getCurrentJourney()

      if (journey) {
        setJourneyState(journey)
        setCurrentPosition({
          lat: journey.current_position.latitude,
          lng: journey.current_position.longitude,
        })
        subscribeToJourney(journey.journey_id)
      } else {
        if (journeyState?.status !== 'COMPLETED') {
          setJourneyState(null)
          currentJourneyIdRef.current = null
          cleanupConnection()
        }
      }
      setIsConnecting(false)
      setError(null)
    } catch (err) {
      setError(err.message || 'Failed to connect to backend.')
      setIsConnecting(false)
    }
  }, [journeyState?.status, subscribeToJourney, cleanupConnection])

  // Start polling on mount
  useEffect(() => {
    pollCurrentJourney()
    pollIntervalRef.current = setInterval(pollCurrentJourney, JOURNEY_POLL_INTERVAL)
    return () => {
      if (pollIntervalRef.current) clearInterval(pollIntervalRef.current)
      cleanupConnection()
    }
  }, [pollCurrentJourney, cleanupConnection])

  return (
    <div className="relative w-full h-dvh bg-black overflow-hidden font-sans">
      {/* Background Video Dashboard */}
      <video
        ref={videoRef}
        className="absolute inset-0 w-full h-full object-cover"
        src="/videos/dashboard-loop.mp4"
        autoPlay
        muted
        playsInline
      />

      {/* Infotainment Screen Overlay (Map) */}
      <div
        className={`absolute z-10 overflow-hidden bg-gray-900 border border-white/5 transition-opacity duration-1000 ${isMapVisible ? 'opacity-100' : 'opacity-0'}`}
        style={{
          top: '65.4%',
          left: '40.6%',
          width: '27.3%',
          height: '25.9%',
          transform: 'perspective(3000px) rotateX(0deg) rotateY(0deg) skewX(0deg)',
          borderRadius: '3px',
          boxShadow: 'inset 0 0 50px rgba(0,0,0,0.9), 0 0 25px rgba(0,0,0,0.4)',
        }}
      >
        <MapView
          currentPosition={currentPosition}
          destination={DEALERSHIP_LOCATION}
          startPoint={startPoint}
          waypoints={waypoints}
          status={status}
          isEmbedded={true}
        />
        {/* Anti-aliasing / glare overlay */}
        <div className="absolute inset-0 pointer-events-none bg-gradient-to-tr from-white/5 via-transparent to-transparent opacity-40" />
        <div className="absolute inset-0 pointer-events-none ring-1 ring-inset ring-white/10" />
      </div>

      {/* Floating HUD Overlays */}
      <div className={`transition-opacity duration-1000 delay-500 ${isMapVisible ? 'opacity-100' : 'opacity-0'}`}>
        <ProgressBar3D progress={progress} />

        <div className="absolute top-6 right-6 z-20">
          <JourneyPanel
            status={status}
            routeName={currentRoute?.name || 'Waiting for Route...'}
            distanceRemaining={distanceRemaining}
            etaSeconds={estimatedTimeRemaining}
            currentPosition={currentPosition}
          />
        </div>

        <Speedometer speed={speedKmh} />

        <MapControls
          isFollowing={isFollowing}
          onFollowingToggle={() => setIsFollowing(!isFollowing)}
          onZoomIn={() => { }} // Leaflet handles internal zoom with isEmbedded
          onZoomOut={() => { }}
          onSettings={() => { }}
        />
      </div>

      {/* Speedometer Overlay (optional extra visual since video has one) */}
      {/* If the video speedometer doesn't move or the user wants our live data visible elsewhere */}
      {/* But user said "dont change anything in the background... or change the speedometer" */}
      {/* So I'll keep ours hidden to let the video one show, assuming it matches the vibe */}

      {/* Connection / Error Toasts */}
      <div className="absolute bottom-10 left-10 z-30 space-y-4 max-w-sm">
        {error && (
          <div className="bg-red-900/80 backdrop-blur-md border border-red-700 p-4 rounded-xl text-red-100 animate-in fade-in slide-in-from-bottom-4">
            <p className="font-bold text-sm">System Alert</p>
            <p className="text-xs opacity-90">{error}</p>
          </div>
        )}
        {mqttError && (
          <div className="bg-orange-900/80 backdrop-blur-md border border-orange-700 p-4 rounded-xl text-orange-100 animate-in fade-in slide-in-from-bottom-4">
            <p className="font-bold text-sm">Connection Warning</p>
            <p className="text-xs opacity-90">{mqttError}</p>
          </div>
        )}
      </div>

      {/* Loading State */}
      {isConnecting && !journeyState && (
        <div className="absolute inset-0 z-50 bg-black/50 flex items-center justify-center">
          <div className="w-12 h-12 border-4 border-white/20 border-t-white rounded-full animate-spin" />
        </div>
      )}
    </div>
  )
}