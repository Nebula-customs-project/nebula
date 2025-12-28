'use client';

import { motion } from 'framer-motion';
import { JourneyStatus } from '@/types';
import { 
  Play, 
  Pause, 
  Square, 
  RotateCcw, 
  Navigation,
  Gauge,
  Timer,
  MapPin
} from 'lucide-react';

interface JourneyControlsProps {
  status: JourneyStatus;
  progress: number;
  speedKmh: number;
  distanceRemaining: number;
  estimatedTimeRemaining: number;
  routeName: string;
  onStart: () => void;
  onPause: () => void;
  onResume: () => void;
  onStop: () => void;
  onReset: () => void;
  isLoading: boolean;
}

export default function JourneyControls({
  status,
  progress,
  speedKmh,
  distanceRemaining,
  estimatedTimeRemaining,
  routeName,
  onStart,
  onPause,
  onResume,
  onStop,
  onReset,
  isLoading,
}: JourneyControlsProps) {
  
  // Format distance
  const formatDistance = (meters: number) => {
    if (meters >= 1000) {
      return `${(meters / 1000).toFixed(1)} km`;
    }
    return `${Math.round(meters)} m`;
  };

  // Format time
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    if (mins >= 60) {
      const hrs = Math.floor(mins / 60);
      const remainingMins = mins % 60;
      return `${hrs}h ${remainingMins}m`;
    }
    return `${mins}m ${secs}s`;
  };

  // Get status badge class
  const getStatusBadgeClass = () => {
    switch (status) {
      case 'NOT_STARTED':
        return 'status-badge status-not-started';
      case 'IN_PROGRESS':
        return 'status-badge status-in-progress';
      case 'PAUSED':
        return 'status-badge status-paused';
      case 'COMPLETED':
        return 'status-badge status-completed';
      default:
        return 'status-badge status-not-started';
    }
  };

  return (
    <motion.div
      className="info-card w-full max-w-md"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Navigation className="w-5 h-5 text-blue-400" />
          <h2 className="text-lg font-bold text-white">Journey Control</h2>
        </div>
        <span className={getStatusBadgeClass()}>
          {status.replace('_', ' ')}
        </span>
      </div>

      {/* Route name */}
      <div className="flex items-center gap-2 mb-4 text-gray-300">
        <MapPin className="w-4 h-4 text-porsche-red" />
        <span className="text-sm truncate">{routeName}</span>
      </div>

      {/* Progress bar */}
      <div className="mb-4">
        <div className="flex justify-between text-sm text-gray-400 mb-1">
          <span>Progress</span>
          <span>{progress.toFixed(1)}%</span>
        </div>
        <div className="h-3 bg-gray-700 rounded-full overflow-hidden">
          <motion.div
            className="h-full progress-bar rounded-full"
            initial={{ width: 0 }}
            animate={{ width: `${progress}%` }}
            transition={{ duration: 0.5, ease: 'easeOut' }}
          />
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-3 gap-3 mb-4">
        <div className="bg-gray-800/50 rounded-lg p-3 text-center">
          <Gauge className="w-5 h-5 text-green-400 mx-auto mb-1" />
          <div className="text-lg font-bold text-white">{speedKmh.toFixed(0)}</div>
          <div className="text-xs text-gray-400">km/h</div>
        </div>
        <div className="bg-gray-800/50 rounded-lg p-3 text-center">
          <Navigation className="w-5 h-5 text-blue-400 mx-auto mb-1" />
          <div className="text-lg font-bold text-white">{formatDistance(distanceRemaining)}</div>
          <div className="text-xs text-gray-400">remaining</div>
        </div>
        <div className="bg-gray-800/50 rounded-lg p-3 text-center">
          <Timer className="w-5 h-5 text-yellow-400 mx-auto mb-1" />
          <div className="text-lg font-bold text-white">{formatTime(estimatedTimeRemaining)}</div>
          <div className="text-xs text-gray-400">ETA</div>
        </div>
      </div>

      {/* Control buttons */}
      <div className="flex gap-2">
        {status === 'NOT_STARTED' && (
          <motion.button
            className="btn-primary flex-1 flex items-center justify-center gap-2"
            onClick={onStart}
            disabled={isLoading}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <Play className="w-5 h-5" />
            Start Journey
          </motion.button>
        )}

        {status === 'IN_PROGRESS' && (
          <>
            <motion.button
              className="btn-secondary flex-1 flex items-center justify-center gap-2"
              onClick={onPause}
              disabled={isLoading}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <Pause className="w-5 h-5" />
              Pause
            </motion.button>
            <motion.button
              className="btn-primary px-4 flex items-center justify-center"
              onClick={onStop}
              disabled={isLoading}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              title="Stop Journey"
            >
              <Square className="w-5 h-5" />
            </motion.button>
          </>
        )}

        {status === 'PAUSED' && (
          <>
            <motion.button
              className="btn-primary flex-1 flex items-center justify-center gap-2"
              onClick={onResume}
              disabled={isLoading}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <Play className="w-5 h-5" />
              Resume
            </motion.button>
            <motion.button
              className="btn-secondary px-4 flex items-center justify-center"
              onClick={onStop}
              disabled={isLoading}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              title="Stop Journey"
            >
              <Square className="w-5 h-5" />
            </motion.button>
          </>
        )}

        {status === 'COMPLETED' && (
          <motion.button
            className="btn-primary flex-1 flex items-center justify-center gap-2"
            onClick={onReset}
            disabled={isLoading}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <RotateCcw className="w-5 h-5" />
            New Journey
          </motion.button>
        )}
      </div>

      {/* Loading indicator */}
      {isLoading && (
        <div className="mt-3 flex items-center justify-center gap-2 text-gray-400">
          <div className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm">Processing...</span>
        </div>
      )}
    </motion.div>
  );
}
