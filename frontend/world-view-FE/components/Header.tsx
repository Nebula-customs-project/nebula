'use client';

import { motion } from 'framer-motion';
import { Car, MapPin, Fuel } from 'lucide-react';

interface HeaderProps {
  journeyActive: boolean;
}

export default function Header({ journeyActive }: HeaderProps) {
  return (
    <motion.header
      className="fixed top-0 left-0 right-0 z-50 glass"
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.5, ease: 'easeOut' }}
    >
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-porsche-red rounded-lg flex items-center justify-center">
            <Car className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-white">World View</h1>
            <p className="text-xs text-gray-400">Porsche Zentrum Stuttgart</p>
          </div>
        </div>

        {/* Center - Journey indicator */}
        {journeyActive && (
          <motion.div
            className="hidden md:flex items-center gap-2 bg-blue-600/20 px-4 py-2 rounded-full border border-blue-500/50"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.8 }}
          >
            <div className="w-2 h-2 bg-blue-500 rounded-full animate-pulse" />
            <span className="text-sm text-blue-300">Journey Active</span>
          </motion.div>
        )}

        {/* Right side - Destination */}
        <div className="flex items-center gap-2 text-gray-300">
          <MapPin className="w-4 h-4 text-porsche-red" />
          <span className="text-sm hidden sm:inline">Stuttgart, Germany</span>
        </div>
      </div>
    </motion.header>
  );
}
