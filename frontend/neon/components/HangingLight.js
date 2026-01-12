'use client'

import React, { useRef } from 'react'
import * as THREE from 'three'

export default function HangingLight({ position, target = [0, 0, 0] }) {
  const lightRef = useRef()
  const fixtureRef = useRef()
  const targetRef = useRef()

  // White light material for the fixture
  const fixtureMaterial = new THREE.MeshStandardMaterial({
    color: 0xffffff,
    metalness: 0.8,
    roughness: 0.2,
    emissive: 0xffffff,
    emissiveIntensity: 0.3,
  })

  // Glass material for the light cover
  const glassMaterial = new THREE.MeshPhysicalMaterial({
    color: 0xffffff,
    transparent: true,
    opacity: 0.7,
    roughness: 0.1,
    metalness: 0.0,
    transmission: 0.9,
    thickness: 0.5,
  })

  return (
    <group ref={lightRef} position={position}>
      {/* Ceiling mount / Chain */}
      <mesh position={[0, 0.5, 0]} castShadow>
        <cylinderGeometry args={[0.02, 0.02, 1, 8]} />
        <meshStandardMaterial color={0x333333} metalness={0.7} roughness={0.3} />
      </mesh>

      {/* Light fixture body */}
      <group ref={fixtureRef} position={[0, -0.5, 0]}>
        {/* Main fixture body (cylindrical) */}
        <mesh castShadow>
          <cylinderGeometry args={[0.15, 0.15, 0.2, 16]} />
          <primitive object={fixtureMaterial} attach="material" />
        </mesh>

        {/* Glass cover (slightly larger, transparent) */}
        <mesh position={[0, -0.1, 0]}>
          <cylinderGeometry args={[0.16, 0.16, 0.05, 16]} />
          <primitive object={glassMaterial} attach="material" />
        </mesh>

        {/* Bottom rim */}
        <mesh position={[0, -0.2, 0]} castShadow>
          <torusGeometry args={[0.16, 0.01, 8, 16]} />
          <primitive object={fixtureMaterial} attach="material" />
        </mesh>
      </group>

      {/* Spotlight target (invisible object positioned at target) */}
      <group ref={targetRef} position={target} />

      {/* Spotlight pointing down at the car */}
      <spotLight
        position={[0, -0.5, 0]}
        target={targetRef.current}
        angle={Math.PI / 4}
        penumbra={0.3}
        intensity={2.5}
        distance={20}
        decay={2}
        castShadow
        shadow-mapSize-width={2048}
        shadow-mapSize-height={2048}
        shadow-camera-near={0.1}
        shadow-camera-far={20}
        shadow-camera-fov={45}
        color={0xffffff}
      />
    </group>
  )
}
