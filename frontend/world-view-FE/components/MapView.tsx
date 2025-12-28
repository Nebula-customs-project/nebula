'use client';

import { useEffect, useRef, useState, useMemo } from 'react';
import L from 'leaflet';
import { MapContainer, TileLayer, Polyline, Marker, Popup, useMap } from 'react-leaflet';
import { motion, AnimatePresence } from 'framer-motion';
import { MapPosition, JourneyStatus } from '@/types';

interface MapViewProps {
  currentPosition: MapPosition | null;
  destination: MapPosition;
  startPoint: MapPosition;
  waypoints: MapPosition[];
  status: JourneyStatus;
  carRotation?: number;
}

// Custom hook to animate the map to follow the car
function MapFollower({ position, shouldFollow }: { position: MapPosition | null; shouldFollow: boolean }) {
  const map = useMap();
  
  useEffect(() => {
    if (position && shouldFollow) {
      map.setView([position.lat, position.lng], map.getZoom(), {
        animate: true,
        duration: 0.5,
      });
    }
  }, [position, shouldFollow, map]);
  
  return null;
}

// Create custom car icon
const createCarIcon = (rotation: number = 0) => {
  return L.divIcon({
    className: 'car-marker',
    html: `
      <div style="
        transform: rotate(${rotation}deg);
        font-size: 36px;
        filter: drop-shadow(0 4px 8px rgba(0,0,0,0.4));
        transition: transform 0.3s ease;
      ">
        🏎️
      </div>
    `,
    iconSize: [40, 40],
    iconAnchor: [20, 20],
  });
};

// Create dealership icon (Porsche logo placeholder)
const dealershipIcon = L.divIcon({
  className: 'dealership-marker',
  html: `
    <div style="
      background: linear-gradient(135deg, #D5001C, #8B0000);
      border-radius: 50%;
      width: 50px;
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 20px rgba(213, 0, 28, 0.5);
      border: 3px solid white;
      font-size: 24px;
    ">
      🏁
    </div>
  `,
  iconSize: [50, 50],
  iconAnchor: [25, 25],
});

// Create start point icon
const startIcon = L.divIcon({
  className: 'start-marker',
  html: `
    <div style="
      background: linear-gradient(135deg, #10B981, #059669);
      border-radius: 50%;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
      border: 2px solid white;
      font-size: 16px;
    ">
      📍
    </div>
  `,
  iconSize: [30, 30],
  iconAnchor: [15, 15],
});

export default function MapView({
  currentPosition,
  destination,
  startPoint,
  waypoints,
  status,
  carRotation = 0,
}: MapViewProps) {
  const [followCar, setFollowCar] = useState(true);
  const mapRef = useRef<L.Map | null>(null);

  // Convert waypoints to Leaflet format
  const routeCoordinates = useMemo(() => {
    return waypoints.map((wp) => [wp.lat, wp.lng] as [number, number]);
  }, [waypoints]);

  // Calculate remaining route (from current position to destination)
  const remainingRoute = useMemo(() => {
    if (!currentPosition) return routeCoordinates;
    
    const currentIdx = waypoints.findIndex(
      (wp) => 
        Math.abs(wp.lat - currentPosition.lat) < 0.0001 && 
        Math.abs(wp.lng - currentPosition.lng) < 0.0001
    );
    
    if (currentIdx === -1) {
      return [[currentPosition.lat, currentPosition.lng], ...routeCoordinates.slice(-1)] as [number, number][];
    }
    
    return routeCoordinates.slice(currentIdx);
  }, [currentPosition, waypoints, routeCoordinates]);

  // Completed route (from start to current position)
  const completedRoute = useMemo(() => {
    if (!currentPosition) return [];
    
    const currentIdx = waypoints.findIndex(
      (wp) => 
        Math.abs(wp.lat - currentPosition.lat) < 0.0001 && 
        Math.abs(wp.lng - currentPosition.lng) < 0.0001
    );
    
    if (currentIdx === -1) return [];
    
    return routeCoordinates.slice(0, currentIdx + 1);
  }, [currentPosition, waypoints, routeCoordinates]);

  // Calculate car rotation based on movement direction
  const calculatedRotation = useMemo(() => {
    if (remainingRoute.length < 2) return carRotation;
    
    const [lat1, lng1] = remainingRoute[0];
    const [lat2, lng2] = remainingRoute[1];
    
    const angle = Math.atan2(lng2 - lng1, lat2 - lat1) * (180 / Math.PI);
    return 90 - angle; // Adjust for emoji orientation
  }, [remainingRoute, carRotation]);

  // Map center - use Stuttgart area as default
  const mapCenter: [number, number] = currentPosition 
    ? [currentPosition.lat, currentPosition.lng]
    : [startPoint.lat, startPoint.lng];

  return (
    <div className="relative w-full h-full">
      <MapContainer
        center={mapCenter}
        zoom={14}
        className="w-full h-full rounded-lg"
        ref={mapRef}
        zoomControl={false}
      >
        {/* Dark themed map tiles */}
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        />

        {/* Follow car hook */}
        <MapFollower position={currentPosition} shouldFollow={followCar && status === 'IN_PROGRESS'} />

        {/* Completed route - green/gray */}
        {completedRoute.length > 1 && (
          <Polyline
            positions={completedRoute}
            pathOptions={{
              color: '#10B981',
              weight: 6,
              opacity: 0.6,
            }}
          />
        )}

        {/* Remaining route - blue animated */}
        {remainingRoute.length > 1 && (
          <Polyline
            positions={remainingRoute}
            pathOptions={{
              color: '#3B82F6',
              weight: 6,
              opacity: 0.9,
              dashArray: '10, 10',
              lineCap: 'round',
              lineJoin: 'round',
            }}
          />
        )}

        {/* Route glow effect */}
        {remainingRoute.length > 1 && (
          <Polyline
            positions={remainingRoute}
            pathOptions={{
              color: '#3B82F6',
              weight: 12,
              opacity: 0.2,
            }}
          />
        )}

        {/* Start point marker */}
        <Marker position={[startPoint.lat, startPoint.lng]} icon={startIcon}>
          <Popup>
            <div className="text-center">
              <strong>🚀 Start Point</strong>
              <br />
              Journey begins here
            </div>
          </Popup>
        </Marker>

        {/* Dealership marker */}
        <Marker position={[destination.lat, destination.lng]} icon={dealershipIcon}>
          <Popup>
            <div className="text-center">
              <strong>🏁 Porsche Zentrum Stuttgart</strong>
              <br />
              Destination
            </div>
          </Popup>
        </Marker>

        {/* Car marker */}
        {currentPosition && (
          <Marker
            position={[currentPosition.lat, currentPosition.lng]}
            icon={createCarIcon(calculatedRotation)}
          >
            <Popup>
              <div className="text-center">
                <strong>🏎️ Your Vehicle</strong>
                <br />
                Status: {status}
              </div>
            </Popup>
          </Marker>
        )}
      </MapContainer>

      {/* Follow car toggle button */}
      <motion.button
        className={`absolute bottom-4 right-4 z-[1000] px-4 py-2 rounded-lg font-semibold text-sm transition-all ${
          followCar 
            ? 'bg-blue-600 text-white' 
            : 'bg-gray-700 text-gray-300'
        }`}
        onClick={() => setFollowCar(!followCar)}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        {followCar ? '📍 Following' : '🗺️ Free View'}
      </motion.button>

      {/* Zoom controls */}
      <div className="absolute top-4 right-4 z-[1000] flex flex-col gap-2">
        <motion.button
          className="w-10 h-10 bg-gray-800 hover:bg-gray-700 rounded-lg flex items-center justify-center text-white font-bold"
          onClick={() => mapRef.current?.zoomIn()}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          +
        </motion.button>
        <motion.button
          className="w-10 h-10 bg-gray-800 hover:bg-gray-700 rounded-lg flex items-center justify-center text-white font-bold"
          onClick={() => mapRef.current?.zoomOut()}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          −
        </motion.button>
      </div>
    </div>
  );
}
