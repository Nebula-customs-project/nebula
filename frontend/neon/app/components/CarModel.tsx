"use client";

import React, { useRef, useEffect, useState } from "react";
import { useFrame } from "@react-three/fiber";
import { useGLTF } from "@react-three/drei";
import * as THREE from "three";
import FallbackCar from "./FallbackCar";

interface CarModelProps {
  configuration: Record<string, string>;
  paintMaterial: {
    color: THREE.Color;
    metalness: number;
    roughness: number;
  };
  onError: () => void;
}

const CarModel: React.FC<CarModelProps> = ({ configuration, paintMaterial, onError }) => {
  const groupRef = useRef<THREE.Group>(null);
  const [hasError, setHasError] = useState(false);

  // Load the 3D model - useGLTF handles errors internally
  const { scene } = useGLTF("/models/car.glb", true, true, (loader) => {
    loader.manager.onError = (url) => {
      console.error("Failed to load model:", url);
      setHasError(true);
      onError();
    };
  });

  // Apply materials to the car
  useEffect(() => {
    if (scene) {
      // Debug: Log all mesh names to help identify parts
      console.log("=== CAR MODEL PARTS ANALYSIS ===");
      let hasSeparateInterior = false;
      let hasSeparateWheels = false;
      let hasSeparateSpoiler = false;
      let hasBodyParts = false;
      
      scene.traverse((child: any) => {
        if (child.isMesh) {
          const name = child.name.toLowerCase();
          console.log(`Part: ${child.name}, Material: ${child.material?.name || 'unnamed'}, Type: ${child.material?.type}`);
          
          // Check what customization is possible
          if (name.includes("interior") || name.includes("seat") || name.includes("dashboard")) {
            hasSeparateInterior = true;
          }
          if (name.includes("wheel") || name.includes("rim") || name.includes("tire")) {
            hasSeparateWheels = true;
          }
          if (name.includes("spoiler") || name.includes("wing")) {
            hasSeparateSpoiler = true;
          }
          if (name.includes("body") || name.includes("door") || name.includes("hood")) {
            hasBodyParts = true;
          }
        }
      });
      
      console.log("\n=== CUSTOMIZATION POSSIBILITIES ===");
      console.log(`✅ Exterior Paint: ${hasBodyParts ? "YES" : "MAYBE (all parts)"}`);
      console.log(`${hasSeparateInterior ? "✅" : "❌"} Interior Color: ${hasSeparateInterior ? "YES" : "NO"}`);
      console.log(`${hasSeparateWheels ? "✅" : "❌"} Wheels: ${hasSeparateWheels ? "YES" : "NO"}`);
      console.log(`${hasSeparateSpoiler ? "✅" : "❌"} Spoiler: ${hasSeparateSpoiler ? "NO" : "NO"}`);
      console.log("✅ Window Tint: YES (glass detection)");
      console.log("✅ Ride Height: YES (position adjustment)");
      console.log("===================================\n");

      scene.traverse((child: any) => {
        if (child.isMesh) {
          // Create PBR material for car body
          const material = new THREE.MeshStandardMaterial({
            color: paintMaterial.color,
            metalness: paintMaterial.metalness,
            roughness: paintMaterial.roughness,
            envMapIntensity: 1.5,
            side: THREE.FrontSide,
          });

          // Get mesh name and check original material properties
          const name = child.name.toLowerCase();
          const originalMat = child.material;
          
          // Detect glass by material properties (transparency, transmission)
          const isGlass = originalMat && (
            originalMat.transparent === true || 
            originalMat.transmission > 0 ||
            originalMat.opacity < 0.9 ||
            name.includes("window") || 
            name.includes("glass") || 
            name.includes("windshield") ||
            name.includes("vitre") // French for glass
          );

          // Detect dark/black parts (likely interior, tires, trim)
          const isDark = originalMat && originalMat.color && (
            (originalMat.color.r < 0.2 && originalMat.color.g < 0.2 && originalMat.color.b < 0.2)
          );

          // Detect metallic/chrome parts (mirrors, trim)
          const isChrome = originalMat && originalMat.metalness > 0.7 && !name.includes("body");
          
          if (isGlass) {
            // Keep glass transparent - DON'T change color
            child.material = new THREE.MeshPhysicalMaterial({
              color: originalMat.color || 0x88ccff,
              metalness: 0.1,
              roughness: 0.05,
              transmission: 0.9,
              transparent: true,
              opacity: 0.3,
              ior: 1.5,
              thickness: 0.5,
            });
            child.castShadow = false;
            child.receiveShadow = true;
          } else if (isDark && (name.includes("interior") || name.includes("seat") || name.includes("dashboard"))) {
            // Keep interior dark - DON'T change color
            child.material = new THREE.MeshStandardMaterial({
              color: originalMat.color,
              metalness: 0.1,
              roughness: 0.7,
            });
          } else if (name.includes("tire") || name.includes("tyre") || name.includes("wheel_")) {
          } else if (name.includes("tire") || name.includes("tyre") || name.includes("wheel_")) {
            // Tires - keep black rubber
            if (isDark || name.includes("tire") || name.includes("tyre")) {
              child.material = new THREE.MeshStandardMaterial({
                color: 0x1a1a1a,
                metalness: 0.1,
                roughness: 0.9,
              });
            } else {
              // Chrome rims
              child.material = new THREE.MeshStandardMaterial({
                color: 0xcccccc,
                metalness: 0.95,
                roughness: 0.1,
                envMapIntensity: 2.0,
              });
            }
            child.castShadow = true;
            child.receiveShadow = true;
          } else if (name.includes("light") || name.includes("headlight") || name.includes("taillight") || name.includes("lamp")) {
            // Lights - keep original emissive
            const isHeadlight = name.includes("head") || name.includes("front");
            child.material = new THREE.MeshStandardMaterial({
              color: isHeadlight ? 0xffffee : 0xff3333,
              emissive: isHeadlight ? 0xffffaa : 0xff0000,
              emissiveIntensity: 0.5,
              metalness: 0.1,
              roughness: 0.2,
            });
          } else if (isChrome || name.includes("mirror") || name.includes("chrome") || name.includes("trim")) {
            // Chrome details - keep shiny
            child.material = new THREE.MeshStandardMaterial({
              color: 0xdddddd,
              metalness: 1.0,
              roughness: 0.05,
              envMapIntensity: 2.5,
            });
            child.castShadow = true;
          } else if (name.includes("body") || name.includes("paint") || name.includes("hood") || name.includes("door") || name.includes("roof") || name.includes("fender") || name.includes("bumper")) {
            // Main car body - use paint material
            child.material = material;
            child.castShadow = true;
            child.receiveShadow = true;
          } else {
            // Default: If it's a large colored mesh, it's probably body paint
            // If it's small/dark, keep original
            if (originalMat && !isDark && !isGlass) {
              child.material = material;
              child.castShadow = true;
              child.receiveShadow = true;
            } else if (originalMat) {
              // Keep original material for dark/special parts
              child.castShadow = true;
              child.receiveShadow = true;
            }
          }
        }
      });
    }
  }, [scene, paintMaterial]);

  // Subtle rotation animation
  useFrame((state) => {
    if (groupRef.current) {
      // Very subtle idle animation
      groupRef.current.position.y = Math.sin(state.clock.elapsedTime * 0.5) * 0.02;
    }
  });

  // If no model found, use fallback geometric car
  if (hasError || !scene) {
    return <FallbackCar paintMaterial={paintMaterial} />;
  }

  return (
    <group ref={groupRef}>
      <primitive
        object={scene}
        scale={1}
        position={[0, -0.5, 0]}
        rotation={[0, Math.PI / 4, 0]}
      />
    </group>
  );
};

// Preload models
useGLTF.preload("/models/car.glb");

export default CarModel;
