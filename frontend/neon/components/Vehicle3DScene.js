'use client'

import React, { Suspense, useState } from 'react'
import { Canvas } from '@react-three/fiber'
import { OrbitControls, Environment, ContactShadows, PerspectiveCamera, Html } from '@react-three/drei'
import CarModel from './CarModel'
import * as THREE from 'three'

// Loading component for 3D scene - adapted to Nebula red theme
function Loader() {
  return (
    <Html center>
      <div className="flex flex-col items-center gap-4">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin"></div>
          <div className="absolute inset-0 w-16 h-16 border-4 border-red-400/20 border-b-red-400 rounded-full animate-spin" style={{ animationDirection: 'reverse', animationDuration: '1.5s' }}></div>
        </div>
        <div className="bg-black/90 backdrop-blur-md px-6 py-3 rounded-xl border border-red-500/50">
          <p className="text-white text-sm font-semibold">Loading 3D Model...</p>
          <p className="text-gray-400 text-xs mt-1">Preparing realistic rendering</p>
        </div>
      </div>
    </Html>
  )
}

// Fallback component when no model is available
function NoModelFallback() {
  return (
    <Html center>
      <div className="bg-black/95 backdrop-blur-md px-8 py-6 rounded-2xl border-2 border-yellow-500/50 max-w-md">
        <div className="text-center">
          <div className="text-6xl mb-4">🚗</div>
          <h3 className="text-white text-xl font-bold mb-3">3D Model Not Found</h3>
          <p className="text-gray-300 text-sm mb-4 leading-relaxed">
            Please download a 3D car model and place it at:
          </p>
          <code className="block bg-gray-900 text-green-400 px-4 py-2 rounded text-xs mb-4">
            /public/models/furarri.glb
          </code>
          <p className="text-gray-400 text-xs">
            📖 See documentation for download instructions
          </p>
        </div>
      </div>
    </Html>
  )
}

// Scene lighting setup
function SceneLighting() {
  return (
    <>
      {/* Ambient light for overall illumination */}
      <ambientLight intensity={0.4} />
      
      {/* Main key light (front-right) */}
      <directionalLight
        position={[5, 8, 5]}
        intensity={1.5}
        castShadow
        shadow-mapSize-width={2048}
        shadow-mapSize-height={2048}
      />
      
      {/* Fill light (front-left) */}
      <directionalLight
        position={[-5, 4, 3]}
        intensity={0.8}
        color="#ffffff"
      />
      
      {/* Back rim light for edge definition */}
      <directionalLight
        position={[0, 3, -5]}
        intensity={0.5}
        color="#ff4040"
      />
      
      {/* Spotlight for dramatic effect */}
      <spotLight
        position={[10, 15, 10]}
        angle={0.3}
        penumbra={1}
        intensity={1}
        castShadow
      />
    </>
  )
}

export default function Vehicle3DScene({ vehicleName, configuration }) {
  const [modelError, setModelError] = useState(false)

  // Get paint color from configuration
  const paintColor = configuration.paint || 'racing-red'
  
  // Color mapping - adapted for Nebula colors
  const colorMap = {
    'racing-red': { color: new THREE.Color(0xdc2626), metalness: 0.9, roughness: 0.1 },
    'midnight-black': { color: new THREE.Color(0x0a0a0a), metalness: 0.8, roughness: 0.2 },
    'pearl-white': { color: new THREE.Color(0xf5f5f5), metalness: 0.7, roughness: 0.2 },
    'ocean-blue': { color: new THREE.Color(0x2563eb), metalness: 0.9, roughness: 0.1 },
    'silver-metallic': { color: new THREE.Color(0xc0c0c0), metalness: 0.95, roughness: 0.05 },
    'sunset-orange': { color: new THREE.Color(0xf97316), metalness: 0.9, roughness: 0.1 },
    'electric-green': { color: new THREE.Color(0x10b981), metalness: 0.88, roughness: 0.12 },
  }

  const currentMaterial = colorMap[paintColor] || colorMap['racing-red']

  return (
    <div className="w-full h-full relative">
      {/* 3D Canvas */}
      <Canvas
        shadows
        gl={{
          antialias: true,
          toneMapping: THREE.ACESFilmicToneMapping,
          toneMappingExposure: 1.2,
          outputColorSpace: THREE.SRGBColorSpace,
        }}
        className="bg-gradient-to-b from-gray-900 via-gray-800 to-black"
      >
        {/* Camera setup */}
        <PerspectiveCamera makeDefault position={[4, 2, 6]} fov={50} />

        {/* Scene lighting */}
        <SceneLighting />

        {/* Environment map for realistic reflections */}
        <Environment
          preset="city"
          background={false}
          blur={0.8}
        />

        {/* Suspense boundary for async model loading */}
        <Suspense fallback={<Loader />}>
          {/* Main car model (with automatic fallback) */}
          <CarModel
            configuration={configuration}
            paintMaterial={currentMaterial}
            onError={() => setModelError(true)}
          />
          
          {/* Contact shadows for realism */}
          <ContactShadows
            position={[0, -0.5, 0]}
            opacity={0.5}
            scale={15}
            blur={2}
            far={4}
          />

          {/* Orbit controls for 360° viewing */}
          <OrbitControls
            enablePan={true}
            enableZoom={true}
            enableRotate={true}
            minDistance={3}
            maxDistance={15}
            minPolarAngle={Math.PI / 6}
            maxPolarAngle={Math.PI / 2}
            autoRotate={false}
            autoRotateSpeed={0.5}
            dampingFactor={0.05}
            rotateSpeed={0.5}
            target={[0, 0.5, 0]}
          />
        </Suspense>
      </Canvas>

      {/* Status badges overlay - Red theme */}
      <div className="absolute top-4 left-4 bg-black/70 backdrop-blur-md px-4 py-2.5 rounded-full border border-gray-600 flex items-center gap-2 shadow-lg pointer-events-none">
        <span className="relative flex h-3 w-3">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3 w-3 bg-red-500"></span>
        </span>
        <span className="text-gray-200 text-xs font-bold">3D REAL-TIME</span>
      </div>

      <div className="absolute top-4 right-4 bg-black/70 backdrop-blur-md px-4 py-2 rounded-full border border-red-500/50 pointer-events-none">
        <span className="text-red-300 text-xs font-medium">
          {paintColor.replace(/-/g, ' ').toUpperCase()}
        </span>
      </div>

      {/* Controls hint */}
      <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 bg-black/60 backdrop-blur-md px-6 py-2 rounded-full border border-gray-700 pointer-events-none">
        <p className="text-gray-300 text-xs">
          🖱️ <strong>Drag</strong> to rotate • <strong>Scroll</strong> to zoom • <strong>Right-click</strong> to pan
        </p>
      </div>
    </div>
  )
}
