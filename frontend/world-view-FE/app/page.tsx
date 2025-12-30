'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import dynamic from 'next/dynamic';
import { motion, AnimatePresence } from 'framer-motion';
import { worldViewApi } from '@/lib/api';
import { JourneyStateDto, RouteDto, CoordinateUpdateDto, MapPosition, JourneyStatus } from '@/types';
import JourneyControls from '@/components/JourneyControls';
import RouteSelection from '@/components/RouteSelection';
import Header from '@/components/Header';

// Dynamically import MapView to avoid SSR issues with Leaflet
const MapView = dynamic(() => import('@/components/MapView'), {
  ssr: false,
  loading: () => (
    <div className="w-full h-full bg-gray-900 flex items-center justify-center">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-gray-400">Loading map...</p>
      </div>
    </div>
  ),
});

// Porsche Zentrum Stuttgart coordinates
const DEALERSHIP_LOCATION: MapPosition = {
  lat: 48.8354,
  lng: 9.152,
};

export default function WorldViewPage() {
  // State
  const [routes, setRoutes] = useState<RouteDto[]>([]);
  const [selectedRouteId, setSelectedRouteId] = useState<string | null>(null);
  const [journeyState, setJourneyState] = useState<JourneyStateDto | null>(null);
  const [currentPosition, setCurrentPosition] = useState<MapPosition | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isRoutesLoading, setIsRoutesLoading] = useState(true);

  // SSE connection cleanup ref
  const cleanupRef = useRef<(() => void) | null>(null);

  // Calculate derived values
  const status: JourneyStatus = journeyState?.status || 'NOT_STARTED';
  const progress = journeyState?.progress_percentage || 0;
  const speedKmh = (journeyState?.speed_meters_per_second || 0) * 3.6;
  
  const selectedRoute = routes.find(r => r.id === selectedRouteId);
  const distanceRemaining = selectedRoute 
    ? selectedRoute.total_distance_meters * (1 - progress / 100)
    : 0;
  const estimatedTimeRemaining = speedKmh > 0 
    ? (distanceRemaining / 1000) / speedKmh * 3600 
    : 0;

  // Get waypoints from selected route
  const waypoints: MapPosition[] = selectedRoute?.waypoints.map(wp => ({
    lat: wp.latitude,
    lng: wp.longitude,
  })) || [];

  const startPoint: MapPosition = selectedRoute?.start_point 
    ? { lat: selectedRoute.start_point.latitude, lng: selectedRoute.start_point.longitude }
    : { lat: 48.78, lng: 9.18 }; // Default Stuttgart area

  // Fetch routes on mount
  useEffect(() => {
    const fetchRoutes = async () => {
      try {
        setIsRoutesLoading(true);
        const routeData = await worldViewApi.getAllRoutes();
        setRoutes(routeData);
        if (routeData.length > 0) {
          setSelectedRouteId(routeData[0].id);
          // Set initial position to start point
          setCurrentPosition({
            lat: routeData[0].start_point.latitude,
            lng: routeData[0].start_point.longitude,
          });
        }
      } catch (err) {
        console.error('Failed to fetch routes:', err);
        setError('Failed to load routes. Is the backend running?');
      } finally {
        setIsRoutesLoading(false);
      }
    };

    fetchRoutes();
  }, []);

  // Update current position when route changes
  useEffect(() => {
    if (selectedRoute && !journeyState) {
      setCurrentPosition({
        lat: selectedRoute.start_point.latitude,
        lng: selectedRoute.start_point.longitude,
      });
    }
  }, [selectedRoute, journeyState]);

  // Handle SSE updates
  const handleCoordinateUpdate = useCallback((update: CoordinateUpdateDto) => {
    setCurrentPosition({
      lat: update.coordinate.latitude,
      lng: update.coordinate.longitude,
    });
    
    setJourneyState(prev => {
      if (!prev) return prev;
      return {
        ...prev,
        current_position: update.coordinate,
        current_waypoint_index: update.current_waypoint_index,
        progress_percentage: update.progress_percentage,
        status: update.status,
      };
    });

    // Check if journey completed
    if (update.status === 'COMPLETED') {
      cleanupConnection();
    }
  }, []);

  // Cleanup SSE/MQTT connection
  const cleanupConnection = useCallback(() => {
    if (cleanupRef.current) {
      cleanupRef.current();
      cleanupRef.current = null;
    }
  }, []);

  // Start journey
  const handleStart = async () => {
    if (!selectedRouteId) {
      setError('Please select a route first');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const journeyId = `journey-${Date.now()}`;
      const response = await worldViewApi.startJourney({
        journey_id: journeyId,
        route_id: selectedRouteId,
        speed_meters_per_second: 13.89, // ~50 km/h
      });

      setJourneyState(response);
      setCurrentPosition({
        lat: response.current_position.latitude,
        lng: response.current_position.longitude,
      });

      // Subscribe to real-time updates
      cleanupConnection();
      cleanupRef.current = worldViewApi.subscribeToJourney(
        journeyId,
        handleCoordinateUpdate,
        {
          onError: (error) => {
            console.error('Connection error:', error);
            setError('Connection lost. Trying to reconnect...');
          }
        }
      );
    } catch (err) {
      console.error('Failed to start journey:', err);
      setError('Failed to start journey');
    } finally {
      setIsLoading(false);
    }
  };

  // Pause journey
  const handlePause = async () => {
    if (!journeyState) return;
    
    setIsLoading(true);
    try {
      const response = await worldViewApi.pauseJourney(journeyState.journey_id);
      setJourneyState(response);
    } catch (err) {
      console.error('Failed to pause journey:', err);
      setError('Failed to pause journey');
    } finally {
      setIsLoading(false);
    }
  };

  // Resume journey
  const handleResume = async () => {
    if (!journeyState) return;
    
    setIsLoading(true);
    try {
      const response = await worldViewApi.resumeJourney(journeyState.journey_id);
      setJourneyState(response);
    } catch (err) {
      console.error('Failed to resume journey:', err);
      setError('Failed to resume journey');
    } finally {
      setIsLoading(false);
    }
  };

  // Stop journey
  const handleStop = async () => {
    if (!journeyState) return;
    
    setIsLoading(true);
    try {
      await worldViewApi.stopJourney(journeyState.journey_id);
      cleanupConnection();
      setJourneyState(null);
      // Reset to start position
      if (selectedRoute) {
        setCurrentPosition({
          lat: selectedRoute.start_point.latitude,
          lng: selectedRoute.start_point.longitude,
        });
      }
    } catch (err) {
      console.error('Failed to stop journey:', err);
      setError('Failed to stop journey');
    } finally {
      setIsLoading(false);
    }
  };

  // Reset/New journey
  const handleReset = () => {
    cleanupConnection();
    setJourneyState(null);
    if (selectedRoute) {
      setCurrentPosition({
        lat: selectedRoute.start_point.latitude,
        lng: selectedRoute.start_point.longitude,
      });
    }
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      cleanupConnection();
    };
  }, [cleanupConnection]);

  return (
    <div className="min-h-screen bg-gray-950 flex flex-col">
      {/* Header */}
      <Header journeyActive={status === 'IN_PROGRESS'} />

      {/* Main content */}
      <main className="flex-1 pt-16 flex flex-col lg:flex-row">
        {/* Map area */}
        <div className="flex-1 relative min-h-[50vh] lg:min-h-0">
          <MapView
            currentPosition={currentPosition}
            destination={DEALERSHIP_LOCATION}
            startPoint={startPoint}
            waypoints={waypoints}
            status={status}
          />

          {/* Error toast */}
          <AnimatePresence>
            {error && (
              <motion.div
                className="absolute top-4 left-4 right-4 md:left-auto md:right-4 md:w-96 bg-red-900/90 border border-red-700 text-white p-4 rounded-lg shadow-lg z-[1000]"
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
              >
                <div className="flex items-start justify-between">
                  <p className="text-sm">{error}</p>
                  <button 
                    onClick={() => setError(null)}
                    className="text-red-300 hover:text-white ml-2"
                  >
                    ✕
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* Control panel */}
        <div className="lg:w-96 bg-gray-900/50 backdrop-blur-md p-4 lg:p-6 space-y-4 border-t lg:border-t-0 lg:border-l border-gray-800">
          {/* Route selection (only show when journey not active) */}
          {!journeyState && (
            <RouteSelection
              routes={routes}
              selectedRouteId={selectedRouteId}
              onSelectRoute={setSelectedRouteId}
              isLoading={isRoutesLoading}
            />
          )}

          {/* Journey controls */}
          <JourneyControls
            status={status}
            progress={progress}
            speedKmh={speedKmh}
            distanceRemaining={distanceRemaining}
            estimatedTimeRemaining={estimatedTimeRemaining}
            routeName={selectedRoute?.name || 'Select a route'}
            onStart={handleStart}
            onPause={handlePause}
            onResume={handleResume}
            onStop={handleStop}
            onReset={handleReset}
            isLoading={isLoading}
          />

          {/* Destination info card */}
          <motion.div
            className="info-card"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <h3 className="text-sm font-semibold text-gray-400 mb-2">Destination</h3>
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-porsche-red/20 rounded-lg flex items-center justify-center text-2xl">
                🏁
              </div>
              <div>
                <p className="font-bold text-white">Porsche Zentrum Stuttgart</p>
                <p className="text-sm text-gray-400">Porscheplatz 1, Stuttgart</p>
              </div>
            </div>
          </motion.div>
        </div>
      </main>
    </div>
  );
}
