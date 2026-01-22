'use client'

import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'

export default function Car3D({ position = [0, 0, 0], rotation = 0, speed = 0 }) {
    const groupRef = useRef()
    const targetPosition = useRef(new THREE.Vector3(...position))
    const targetRotation = useRef(rotation)
    const wheelsRef = useRef([])

    // Update target position/rotation when props change
    useMemo(() => {
        targetPosition.current.set(position[0], position[1], position[2])
        targetRotation.current = rotation
    }, [position, rotation])

    // Smooth interpolation
    useFrame((state, delta) => {
        if (groupRef.current) {
            // Lerp position
            groupRef.current.position.lerp(targetPosition.current, 0.1)

            // Lerp rotation
            const currentY = groupRef.current.rotation.y
            const diff = targetRotation.current - currentY
            groupRef.current.rotation.y += diff * 0.1

            // Rotate wheels based on speed
            wheelsRef.current.forEach(wheel => {
                if (wheel) {
                    wheel.rotation.x += speed * delta * 2
                }
            })
        }
    })

    const wheelPositions = [
        [-0.7, 0.2, 0.9],   // front left
        [0.7, 0.2, 0.9],    // front right
        [-0.7, 0.2, -0.9],  // back left
        [0.7, 0.2, -0.9],   // back right
    ]

    return (
        <group ref={groupRef} position={position}>
            {/* Car body - main chassis */}
            <mesh position={[0, 0.5, 0]} castShadow>
                <boxGeometry args={[1.6, 0.4, 3.5]} />
                <meshStandardMaterial color="#c41e3a" roughness={0.3} metalness={0.8} />
            </mesh>

            {/* Car body - cabin */}
            <mesh position={[0, 0.85, -0.2]} castShadow>
                <boxGeometry args={[1.4, 0.35, 1.8]} />
                <meshStandardMaterial color="#c41e3a" roughness={0.3} metalness={0.8} />
            </mesh>

            {/* Windshield */}
            <mesh position={[0, 0.85, 0.75]} rotation={[0.3, 0, 0]}>
                <boxGeometry args={[1.3, 0.02, 0.6]} />
                <meshStandardMaterial color="#1a1a2e" roughness={0.1} metalness={0.9} />
            </mesh>

            {/* Rear window */}
            <mesh position={[0, 0.85, -1.1]} rotation={[-0.3, 0, 0]}>
                <boxGeometry args={[1.3, 0.02, 0.5]} />
                <meshStandardMaterial color="#1a1a2e" roughness={0.1} metalness={0.9} />
            </mesh>

            {/* Headlights */}
            <mesh position={[-0.5, 0.45, 1.76]}>
                <boxGeometry args={[0.3, 0.15, 0.05]} />
                <meshStandardMaterial
                    color="#ffffff"
                    emissive="#ffffff"
                    emissiveIntensity={2}
                />
            </mesh>
            <mesh position={[0.5, 0.45, 1.76]}>
                <boxGeometry args={[0.3, 0.15, 0.05]} />
                <meshStandardMaterial
                    color="#ffffff"
                    emissive="#ffffff"
                    emissiveIntensity={2}
                />
            </mesh>

            {/* Taillights */}
            <mesh position={[-0.55, 0.45, -1.76]}>
                <boxGeometry args={[0.25, 0.1, 0.05]} />
                <meshStandardMaterial
                    color="#ff0033"
                    emissive="#ff0033"
                    emissiveIntensity={1.5}
                />
            </mesh>
            <mesh position={[0.55, 0.45, -1.76]}>
                <boxGeometry args={[0.25, 0.1, 0.05]} />
                <meshStandardMaterial
                    color="#ff0033"
                    emissive="#ff0033"
                    emissiveIntensity={1.5}
                />
            </mesh>

            {/* Wheels */}
            {wheelPositions.map((pos, i) => (
                <group key={i} position={pos}>
                    <mesh
                        ref={el => wheelsRef.current[i] = el}
                        rotation={[0, 0, Math.PI / 2]}
                        castShadow
                    >
                        <cylinderGeometry args={[0.25, 0.25, 0.2, 16]} />
                        <meshStandardMaterial color="#1a1a1a" roughness={0.8} />
                    </mesh>
                    {/* Wheel rim */}
                    <mesh rotation={[0, 0, Math.PI / 2]}>
                        <cylinderGeometry args={[0.15, 0.15, 0.22, 8]} />
                        <meshStandardMaterial color="#888888" metalness={0.9} roughness={0.2} />
                    </mesh>
                </group>
            ))}

            {/* Point light for headlight glow effect */}
            <pointLight position={[0, 0.5, 2.5]} intensity={0.5} color="#ffffff" distance={10} />
        </group>
    )
}
