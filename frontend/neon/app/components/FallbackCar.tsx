"use client";

import React, { useRef } from "react";
import { useFrame } from "@react-three/fiber";
import * as THREE from "three";

interface FallbackCarProps {
  paintMaterial: {
    color: THREE.Color;
    metalness: number;
    roughness: number;
  };
}

// Simple geometric car built from primitives - fallback when no model available
const FallbackCar: React.FC<FallbackCarProps> = ({ paintMaterial }) => {
  const groupRef = useRef<THREE.Group>(null);

  // Subtle idle animation
  useFrame((state) => {
    if (groupRef.current) {
      groupRef.current.position.y = Math.sin(state.clock.elapsedTime * 0.5) * 0.02;
      groupRef.current.rotation.y = Math.sin(state.clock.elapsedTime * 0.2) * 0.05;
    }
  });

  const bodyMaterial = new THREE.MeshStandardMaterial({
    color: paintMaterial.color,
    metalness: paintMaterial.metalness,
    roughness: paintMaterial.roughness,
    envMapIntensity: 1.5,
  });

  const glassMaterial = new THREE.MeshPhysicalMaterial({
    color: 0x88ccff,
    metalness: 0.1,
    roughness: 0.05,
    transmission: 0.9,
    transparent: true,
    opacity: 0.4,
  });

  const chromeMaterial = new THREE.MeshStandardMaterial({
    color: 0xcccccc,
    metalness: 1.0,
    roughness: 0.05,
    envMapIntensity: 2.0,
  });

  const tireMaterial = new THREE.MeshStandardMaterial({
    color: 0x1a1a1a,
    metalness: 0.1,
    roughness: 0.9,
  });

  const headlightMaterial = new THREE.MeshStandardMaterial({
    color: 0xffffee,
    emissive: 0xffffaa,
    emissiveIntensity: 0.6,
  });

  return (
    <group ref={groupRef} position={[0, 0, 0]}>
      {/* Main body - lower section */}
      <mesh position={[0, 0.3, 0]} castShadow receiveShadow>
        <boxGeometry args={[2.5, 0.4, 1.2]} />
        <primitive object={bodyMaterial} attach="material" />
      </mesh>

      {/* Cabin - upper section */}
      <mesh position={[0, 0.7, 0]} castShadow receiveShadow>
        <boxGeometry args={[1.5, 0.5, 1.0]} />
        <primitive object={bodyMaterial} attach="material" />
      </mesh>

      {/* Hood */}
      <mesh position={[0.8, 0.35, 0]} castShadow receiveShadow>
        <boxGeometry args={[0.8, 0.3, 1.1]} />
        <primitive object={bodyMaterial} attach="material" />
      </mesh>

      {/* Windshield */}
      <mesh position={[0.2, 0.75, 0]} rotation={[0, 0, -0.2]}>
        <boxGeometry args={[0.6, 0.5, 0.95]} />
        <primitive object={glassMaterial} attach="material" />
      </mesh>

      {/* Rear window */}
      <mesh position={[-0.5, 0.75, 0]} rotation={[0, 0, 0.2]}>
        <boxGeometry args={[0.5, 0.4, 0.95]} />
        <primitive object={glassMaterial} attach="material" />
      </mesh>

      {/* Side windows */}
      <mesh position={[0, 0.7, 0.53]}>
        <boxGeometry args={[1.4, 0.45, 0.02]} />
        <primitive object={glassMaterial} attach="material" />
      </mesh>
      <mesh position={[0, 0.7, -0.53]}>
        <boxGeometry args={[1.4, 0.45, 0.02]} />
        <primitive object={glassMaterial} attach="material" />
      </mesh>

      {/* Front bumper */}
      <mesh position={[1.35, 0.15, 0]} castShadow>
        <boxGeometry args={[0.2, 0.2, 1.0]} />
        <primitive object={bodyMaterial} attach="material" />
      </mesh>

      {/* Rear bumper */}
      <mesh position={[-1.35, 0.15, 0]} castShadow>
        <boxGeometry args={[0.2, 0.2, 1.0]} />
        <primitive object={bodyMaterial} attach="material" />
      </mesh>

      {/* Headlights */}
      <mesh position={[1.35, 0.25, 0.4]}>
        <sphereGeometry args={[0.1, 16, 16]} />
        <primitive object={headlightMaterial} attach="material" />
      </mesh>
      <mesh position={[1.35, 0.25, -0.4]}>
        <sphereGeometry args={[0.1, 16, 16]} />
        <primitive object={headlightMaterial} attach="material" />
      </mesh>

      {/* Taillights */}
      <mesh position={[-1.35, 0.25, 0.4]}>
        <sphereGeometry args={[0.08, 16, 16]} />
        <meshStandardMaterial color={0xff3333} emissive={0xff0000} emissiveIntensity={0.5} />
      </mesh>
      <mesh position={[-1.35, 0.25, -0.4]}>
        <sphereGeometry args={[0.08, 16, 16]} />
        <meshStandardMaterial color={0xff3333} emissive={0xff0000} emissiveIntensity={0.5} />
      </mesh>

      {/* Wheels */}
      {/* Front Right */}
      <group position={[0.7, 0, 0.65]}>
        <mesh rotation={[0, 0, Math.PI / 2]} castShadow>
          <cylinderGeometry args={[0.35, 0.35, 0.25, 32]} />
          <primitive object={tireMaterial} attach="material" />
        </mesh>
        <mesh rotation={[0, 0, Math.PI / 2]}>
          <cylinderGeometry args={[0.25, 0.25, 0.26, 32]} />
          <primitive object={chromeMaterial} attach="material" />
        </mesh>
      </group>

      {/* Front Left */}
      <group position={[0.7, 0, -0.65]}>
        <mesh rotation={[0, 0, Math.PI / 2]} castShadow>
          <cylinderGeometry args={[0.35, 0.35, 0.25, 32]} />
          <primitive object={tireMaterial} attach="material" />
        </mesh>
        <mesh rotation={[0, 0, Math.PI / 2]}>
          <cylinderGeometry args={[0.25, 0.25, 0.26, 32]} />
          <primitive object={chromeMaterial} attach="material" />
        </mesh>
      </group>

      {/* Rear Right */}
      <group position={[-0.7, 0, 0.65]}>
        <mesh rotation={[0, 0, Math.PI / 2]} castShadow>
          <cylinderGeometry args={[0.35, 0.35, 0.25, 32]} />
          <primitive object={tireMaterial} attach="material" />
        </mesh>
        <mesh rotation={[0, 0, Math.PI / 2]}>
          <cylinderGeometry args={[0.25, 0.25, 0.26, 32]} />
          <primitive object={chromeMaterial} attach="material" />
        </mesh>
      </group>

      {/* Rear Left */}
      <group position={[-0.7, 0, -0.65]}>
        <mesh rotation={[0, 0, Math.PI / 2]} castShadow>
          <cylinderGeometry args={[0.35, 0.35, 0.25, 32]} />
          <primitive object={tireMaterial} attach="material" />
        </mesh>
        <mesh rotation={[0, 0, Math.PI / 2]}>
          <cylinderGeometry args={[0.25, 0.25, 0.26, 32]} />
          <primitive object={chromeMaterial} attach="material" />
        </mesh>
      </group>

      {/* Spoiler */}
      <mesh position={[-1.2, 0.7, 0]} castShadow>
        <boxGeometry args={[0.15, 0.05, 1.0]} />
        <primitive object={chromeMaterial} attach="material" />
      </mesh>
      <mesh position={[-1.15, 0.55, 0.4]} castShadow>
        <boxGeometry args={[0.05, 0.3, 0.05]} />
        <primitive object={chromeMaterial} attach="material" />
      </mesh>
      <mesh position={[-1.15, 0.55, -0.4]} castShadow>
        <boxGeometry args={[0.05, 0.3, 0.05]} />
        <primitive object={chromeMaterial} attach="material" />
      </mesh>

      {/* Side mirrors */}
      <mesh position={[0.3, 0.8, 0.55]} castShadow>
        <boxGeometry args={[0.1, 0.08, 0.15]} />
        <primitive object={chromeMaterial} attach="material" />
      </mesh>
      <mesh position={[0.3, 0.8, -0.55]} castShadow>
        <boxGeometry args={[0.1, 0.08, 0.15]} />
        <primitive object={chromeMaterial} attach="material" />
      </mesh>
    </group>
  );
};

export default FallbackCar;
