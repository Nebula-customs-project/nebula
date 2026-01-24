'use client'

import { useMemo, useRef } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'

export default function GlowingRoute({ waypoints = [], progress = 0 }) {
    const tubeRef = useRef()
    const glowRef = useRef()

    // Create curve from waypoints
    const { mainCurve, completedCurve } = useMemo(() => {
        if (waypoints.length < 2) {
            // Default demo path
            const defaultPoints = [
                new THREE.Vector3(-30, 0.1, 30),
                new THREE.Vector3(-15, 0.1, 15),
                new THREE.Vector3(0, 0.1, 0),
                new THREE.Vector3(15, 0.1, -15),
                new THREE.Vector3(30, 0.1, -30),
            ]
            return {
                mainCurve: new THREE.CatmullRomCurve3(defaultPoints),
                completedCurve: null
            }
        }

        const points = waypoints.map(wp => new THREE.Vector3(wp[0], 0.1, wp[2]))
        const curve = new THREE.CatmullRomCurve3(points)

        // Create completed portion curve
        if (progress > 0 && progress < 100) {
            const progressT = progress / 100
            const completedPoints = []
            const numPoints = Math.ceil(progressT * 20) + 1

            for (let i = 0; i <= numPoints; i++) {
                const t = (i / numPoints) * progressT
                completedPoints.push(curve.getPoint(t))
            }

            if (completedPoints.length >= 2) {
                return {
                    mainCurve: curve,
                    completedCurve: new THREE.CatmullRomCurve3(completedPoints)
                }
            }
        }

        return { mainCurve: curve, completedCurve: null }
    }, [waypoints, progress])

    // Animate glow effect
    useFrame((state) => {
        if (glowRef.current) {
            glowRef.current.material.opacity = 0.3 + Math.sin(state.clock.elapsedTime * 2) * 0.1
        }
    })

    return (
        <group>
            {/* Main route tube - remaining portion */}
            <mesh ref={tubeRef}>
                <tubeGeometry args={[mainCurve, 64, 0.5, 8, false]} />
                <meshStandardMaterial
                    color="#00d4ff"
                    emissive="#00d4ff"
                    emissiveIntensity={0.5}
                    transparent
                    opacity={0.8}
                    roughness={0.3}
                    metalness={0.7}
                />
            </mesh>

            {/* Outer glow tube */}
            <mesh ref={glowRef}>
                <tubeGeometry args={[mainCurve, 64, 1.0, 8, false]} />
                <meshBasicMaterial
                    color="#00d4ff"
                    transparent
                    opacity={0.2}
                    side={THREE.BackSide}
                />
            </mesh>

            {/* Completed portion - different color */}
            {completedCurve && (
                <mesh>
                    <tubeGeometry args={[completedCurve, 32, 0.6, 8, false]} />
                    <meshStandardMaterial
                        color="#00ff88"
                        emissive="#00ff88"
                        emissiveIntensity={0.8}
                        transparent
                        opacity={0.9}
                        roughness={0.2}
                        metalness={0.8}
                    />
                </mesh>
            )}

            {/* Route markers at waypoints */}
            {waypoints.length >= 2 && (
                <>
                    {/* Start marker */}
                    <mesh position={[waypoints[0][0], 0.5, waypoints[0][2]]}>
                        <sphereGeometry args={[0.8, 16, 16]} />
                        <meshStandardMaterial
                            color="#00ff88"
                            emissive="#00ff88"
                            emissiveIntensity={1}
                        />
                    </mesh>

                    {/* End marker */}
                    <mesh position={[waypoints[waypoints.length - 1][0], 0.5, waypoints[waypoints.length - 1][2]]}>
                        <sphereGeometry args={[0.8, 16, 16]} />
                        <meshStandardMaterial
                            color="#ff4444"
                            emissive="#ff4444"
                            emissiveIntensity={1}
                        />
                    </mesh>
                </>
            )}
        </group>
    )
}
