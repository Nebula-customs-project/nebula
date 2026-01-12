'use client'

import React, { useRef, useEffect } from 'react'
import * as THREE from 'three'

export default function GarageFloor() {
  const floorRef = useRef()

  // Create tiled grey floor material
  const floorMaterial = new THREE.MeshStandardMaterial({
    color: 0x6b7280, // Medium grey
    roughness: 0.7,
    metalness: 0.1,
  })

  // Create realistic grey tile texture
  const createTiledFloorTexture = () => {
    const canvas = document.createElement('canvas')
    canvas.width = 1024
    canvas.height = 1024
    const ctx = canvas.getContext('2d')
    
    // Base grey color
    ctx.fillStyle = '#6b7280'
    ctx.fillRect(0, 0, 1024, 1024)
    
    // Tile size (each tile is 128x128 pixels)
    const tileSize = 128
    const tileGap = 2 // Gap between tiles
    
    // Draw individual tiles with slight variation
    for (let y = 0; y < 1024; y += tileSize) {
      for (let x = 0; x < 1024; x += tileSize) {
        // Slight color variation per tile for realism
        const variation = (Math.random() - 0.5) * 15
        const r = Math.max(0, Math.min(255, 107 + variation))
        const g = Math.max(0, Math.min(255, 114 + variation))
        const b = Math.max(0, Math.min(255, 128 + variation))
        
        ctx.fillStyle = `rgb(${r}, ${g}, ${b})`
        ctx.fillRect(x + tileGap, y + tileGap, tileSize - tileGap * 2, tileSize - tileGap * 2)
        
        // Add subtle texture to each tile
        ctx.globalAlpha = 0.1
        for (let i = 0; i < 5; i++) {
          const noiseX = x + tileGap + Math.random() * (tileSize - tileGap * 2)
          const noiseY = y + tileGap + Math.random() * (tileSize - tileGap * 2)
          const radius = 10 + Math.random() * 20
          
          const gradient = ctx.createRadialGradient(noiseX, noiseY, 0, noiseX, noiseY, radius)
          gradient.addColorStop(0, 'rgba(0, 0, 0, 0.2)')
          gradient.addColorStop(1, 'rgba(0, 0, 0, 0)')
          
          ctx.fillStyle = gradient
          ctx.beginPath()
          ctx.arc(noiseX, noiseY, radius, 0, Math.PI * 2)
          ctx.fill()
        }
        ctx.globalAlpha = 1.0
      }
    }
    
    // Draw tile grout lines (darker grey)
    ctx.strokeStyle = '#4b5563'
    ctx.lineWidth = 2
    for (let x = 0; x <= 1024; x += tileSize) {
      ctx.beginPath()
      ctx.moveTo(x, 0)
      ctx.lineTo(x, 1024)
      ctx.stroke()
    }
    for (let y = 0; y <= 1024; y += tileSize) {
      ctx.beginPath()
      ctx.moveTo(0, y)
      ctx.lineTo(1024, y)
      ctx.stroke()
    }
    
    const texture = new THREE.CanvasTexture(canvas)
    texture.wrapS = THREE.RepeatWrapping
    texture.wrapT = THREE.RepeatWrapping
    texture.repeat.set(4, 4)
    texture.anisotropy = 16
    
    return texture
  }

  // Apply texture to material
  useEffect(() => {
    if (floorRef.current) {
      const texture = createTiledFloorTexture()
      floorMaterial.map = texture
      floorMaterial.needsUpdate = true
    }
  }, [])

  return (
    <mesh
      ref={floorRef}
      rotation={[-Math.PI / 2, 0, 0]}
      position={[0, 0, 0]}
      receiveShadow
    >
      <planeGeometry args={[20, 20, 1, 1]} />
      <primitive object={floorMaterial} attach="material" />
    </mesh>
  )
}
