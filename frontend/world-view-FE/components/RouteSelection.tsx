'use client';

import { motion } from 'framer-motion';
import { RouteDto } from '@/types';
import { MapPin, Navigation, Clock, Ruler } from 'lucide-react';

interface RouteSelectionProps {
  routes: RouteDto[];
  selectedRouteId: string | null;
  onSelectRoute: (routeId: string) => void;
  isLoading: boolean;
}

export default function RouteSelection({
  routes,
  selectedRouteId,
  onSelectRoute,
  isLoading,
}: RouteSelectionProps) {
  
  // Format distance
  const formatDistance = (meters: number) => {
    return `${(meters / 1000).toFixed(1)} km`;
  };

  // Format time
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    if (mins >= 60) {
      const hrs = Math.floor(mins / 60);
      const remainingMins = mins % 60;
      return `${hrs}h ${remainingMins}m`;
    }
    return `${mins} min`;
  };

  if (isLoading) {
    return (
      <div className="info-card w-full max-w-md">
        <div className="flex items-center justify-center py-8">
          <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
        </div>
      </div>
    );
  }

  return (
    <motion.div
      className="info-card w-full max-w-md"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      {/* Header */}
      <div className="flex items-center gap-2 mb-4">
        <Navigation className="w-5 h-5 text-blue-400" />
        <h2 className="text-lg font-bold text-white">Select Route</h2>
      </div>

      {/* Route list */}
      <div className="space-y-3 max-h-80 overflow-y-auto">
        {routes.map((route, index) => (
          <motion.button
            key={route.id}
            className={`w-full text-left p-4 rounded-lg border transition-all ${
              selectedRouteId === route.id
                ? 'bg-blue-600/20 border-blue-500'
                : 'bg-gray-800/50 border-gray-700 hover:bg-gray-700/50 hover:border-gray-600'
            }`}
            onClick={() => onSelectRoute(route.id)}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.1 }}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <div className="flex items-start justify-between mb-2">
              <div className="flex items-center gap-2">
                <MapPin className={`w-4 h-4 ${selectedRouteId === route.id ? 'text-blue-400' : 'text-gray-400'}`} />
                <span className="font-semibold text-white">{route.name}</span>
              </div>
              {selectedRouteId === route.id && (
                <span className="text-xs bg-blue-500 text-white px-2 py-1 rounded-full">
                  Selected
                </span>
              )}
            </div>
            
            <p className="text-sm text-gray-400 mb-2 line-clamp-2">{route.description}</p>
            
            <div className="flex items-center gap-4 text-xs text-gray-500">
              <div className="flex items-center gap-1">
                <Ruler className="w-3 h-3" />
                <span>{formatDistance(route.total_distance_meters)}</span>
              </div>
              <div className="flex items-center gap-1">
                <Clock className="w-3 h-3" />
                <span>{formatTime(route.estimated_duration_seconds)}</span>
              </div>
            </div>
          </motion.button>
        ))}
      </div>

      {routes.length === 0 && (
        <div className="text-center py-8 text-gray-400">
          <Navigation className="w-12 h-12 mx-auto mb-2 opacity-50" />
          <p>No routes available</p>
        </div>
      )}
    </motion.div>
  );
}
