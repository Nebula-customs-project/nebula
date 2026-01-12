'use client'

import React, { Suspense } from 'react'
import { useGLTF } from '@react-three/drei'
import * as THREE from 'three'

// Garage environment component - loads a .glb garage model
function GarageModel() {
  // Load garage model - useGLTF will handle errors gracefully
  const { scene } = useGLTF('/models/garage.glb', true, true, (loader) => {
    loader.manager.onError = (url) => {
      console.warn('Garage model not found at:', url)
      console.info('💡 Place a garage.glb file in /public/models/ for full garage environment')
      console.info('📖 See GARAGE_SETUP.md for instructions')
    }
  })

  // If no garage model, return null (we'll use fallback floor)
  if (!scene) {
    return null
  }

  // Clone the scene to avoid modifying the original
  const garageScene = scene.clone()

  // Optimize materials for better performance
  garageScene.traverse((child) => {
    if (child.isMesh) {
      child.castShadow = true
      child.receiveShadow = true
      
      // Enhance materials if needed
      if (child.material) {
        if (Array.isArray(child.material)) {
          child.material.forEach((mat) => {
            if (mat.isMeshStandardMaterial || mat.isMeshPhysicalMaterial) {
              mat.needsUpdate = true
            }
          })
        } else if (child.material.isMeshStandardMaterial || child.material.isMeshPhysicalMaterial) {
          child.material.needsUpdate = true
        }
      }
    }
  })

  return (
    <primitive
      object={garageScene}
      scale={1}
      position={[0, 0, 0]}
      rotation={[0, 0, 0]}
    />
  )
}

// Wrapper with Suspense for async loading
export default function GarageEnvironment() {
  return (
    <Suspense fallback={null}>
      <GarageModel />
    </Suspense>
  )
}

