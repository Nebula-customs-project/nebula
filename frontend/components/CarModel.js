"use client";

import React, { useRef, useEffect, useState } from "react";
import { useFrame } from "@react-three/fiber";
import { useGLTF } from "@react-three/drei";
import * as THREE from "three";

import { carModelPropTypes, carModelDefaultProps } from "./CarModel.propTypes";

// --- Material Detection Helpers ---
const isGlassMaterial = (originalMat, name) =>
  originalMat &&
  (originalMat.transparent === true ||
    originalMat.transmission > 0 ||
    originalMat.opacity < 0.9 ||
    name.includes("window") ||
    name.includes("glass") ||
    name.includes("windshield") ||
    name.includes("vitre"));

const isDarkMaterial = (originalMat) =>
  originalMat &&
  originalMat.color &&
  originalMat.color.r < 0.2 &&
  originalMat.color.g < 0.2 &&
  originalMat.color.b < 0.2;

const isChromeMaterial = (originalMat, name) =>
  originalMat && originalMat.metalness > 0.7 && !name.includes("body");

const isInteriorPart = (name) =>
  name.includes("interior") ||
  name.includes("seat") ||
  name.includes("dashboard");

const isTirePart = (name) =>
  name.includes("tire") || name.includes("tyre") || name.includes("wheel_");

const isLightPart = (name) =>
  name.includes("light") ||
  name.includes("headlight") ||
  name.includes("taillight") ||
  name.includes("lamp");

const isChromePart = (name) =>
  name.includes("mirror") || name.includes("chrome") || name.includes("trim");

const isBodyPart = (name) =>
  name.includes("body") ||
  name.includes("paint") ||
  name.includes("hood") ||
  name.includes("door") ||
  name.includes("roof") ||
  name.includes("fender") ||
  name.includes("bumper");

// --- Material Creation Helpers ---
const createGlassMaterial = (originalMat) =>
  new THREE.MeshPhysicalMaterial({
    color: originalMat.color || 0x88ccff,
    metalness: 0.1,
    roughness: 0.05,
    transmission: 0.9,
    transparent: true,
    opacity: 0.3,
    ior: 1.5,
    thickness: 0.5,
  });

const createInteriorMaterial = (originalMat) =>
  new THREE.MeshStandardMaterial({
    color: originalMat.color,
    metalness: 0.1,
    roughness: 0.7,
  });

const createTireMaterial = () =>
  new THREE.MeshStandardMaterial({
    color: 0x1a1a1a,
    metalness: 0.1,
    roughness: 0.9,
  });

const createRimMaterial = () =>
  new THREE.MeshStandardMaterial({
    color: 0xcccccc,
    metalness: 0.95,
    roughness: 0.1,
    envMapIntensity: 2,
  });

const createLightMaterial = (isHeadlight) =>
  new THREE.MeshStandardMaterial({
    color: isHeadlight ? 0xffffee : 0xff3333,
    emissive: isHeadlight ? 0xffffaa : 0xff0000,
    emissiveIntensity: 0.5,
    metalness: 0.1,
    roughness: 0.2,
  });

const createChromeMaterial = () =>
  new THREE.MeshStandardMaterial({
    color: 0xdddddd,
    metalness: 1,
    roughness: 0.05,
    envMapIntensity: 2.5,
  });

// --- Main Material Application ---
const applyCarMaterial = (child, bodyMaterial) => {
  if (!child.isMesh) return;

  const name = child.name.toLowerCase();
  const originalMat = child.material;
  const isGlass = isGlassMaterial(originalMat, name);
  const isDark = isDarkMaterial(originalMat);
  const isChrome = isChromeMaterial(originalMat, name);

  if (isGlass) {
    child.material = createGlassMaterial(originalMat);
    child.castShadow = false;
    child.receiveShadow = true;
    return;
  }

  if (isDark && isInteriorPart(name)) {
    child.material = createInteriorMaterial(originalMat);
    return;
  }

  if (isTirePart(name)) {
    child.material =
      isDark || name.includes("tire") || name.includes("tyre")
        ? createTireMaterial()
        : createRimMaterial();
    child.castShadow = true;
    child.receiveShadow = true;
    return;
  }

  if (isLightPart(name)) {
    const isHeadlight = name.includes("head") || name.includes("front");
    child.material = createLightMaterial(isHeadlight);
    return;
  }

  if (isChrome || isChromePart(name)) {
    child.material = createChromeMaterial();
    child.castShadow = true;
    return;
  }

  if (isBodyPart(name)) {
    child.material = bodyMaterial;
    child.castShadow = true;
    child.receiveShadow = true;
    return;
  }

  // Default handling: apply body material to non-dark, non-glass meshes
  if (originalMat && !isDark && !isGlass) {
    child.material = bodyMaterial;
    child.castShadow = true;
    child.receiveShadow = true;
  } else if (originalMat) {
    child.castShadow = true;
    child.receiveShadow = true;
  }
};

/**
 * CarModel Component
 *
 * Loads and renders a 3D car model with material customization.
 */
export default function CarModel({
  modelPath,
  configuration,
  paintMaterial,
  onError,
  onLoad,
}) {
  const groupRef = useRef(null);
  const [hasError, setHasError] = useState(false);

  // Load the 3D model - useGLTF handles errors internally
  const { scene } = useGLTF(
    modelPath || "/models/furarri.glb",
    true,
    true,
    (loader) => {
      loader.manager.onError = (url) => {
        console.error("Failed to load model:", url);
        setHasError(true);
        if (onError) onError();
      };
    },
  );

  // Notify when model is loaded
  useEffect(() => {
    if (scene && !hasError) {
      // Small delay to ensure model is fully rendered
      const timer = setTimeout(() => {
        if (onLoad) onLoad();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [scene, hasError, onLoad, modelPath]);

  // Apply materials to the car - Memoized to prevent unnecessary recalculations
  useEffect(() => {
    if (!scene || !paintMaterial) return;

    // Create material once outside the loop for better performance
    const bodyMaterial = new THREE.MeshStandardMaterial({
      color: paintMaterial.color.clone(), // Clone to avoid reference issues
      metalness: paintMaterial.metalness,
      roughness: paintMaterial.roughness,
      envMapIntensity: 1.5,
      side: THREE.FrontSide,
    });

    scene.traverse((child) => applyCarMaterial(child, bodyMaterial));
  }, [scene, paintMaterial, configuration]);

  // Subtle rotation animation - Optimized to only run when needed
  useFrame((state) => {
    if (groupRef.current) {
      // Very subtle idle animation - reduced frequency for better performance
      groupRef.current.position.y =
        Math.sin(state.clock.elapsedTime * 0.3) * 0.015;
    }
  });

  // If no model found, render nothing
  if (hasError || !scene) {
    return null;
  }

  // NO-SONAR: object, position, rotation are valid React Three Fiber props
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
}

CarModel.propTypes = carModelPropTypes;
CarModel.defaultProps = carModelDefaultProps;
