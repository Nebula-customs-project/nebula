'use client'

import { Canvas } from '@react-three/fiber'
import { OrbitControls, PerspectiveCamera } from '@react-three/drei'
import { Suspense, useMemo, useRef } from 'react'
import { useFrame } from '@react-three/fiber'
import Ground from './Ground'
import Car3D from './Car3D'
import CityBuildings from './CityBuildings'
import GlowingRoute from './GlowingRoute'

// Convert lat/lng to local 3D coordinates
// Using simple mercator projection scaled to reasonable units
function latLngTo3D(lat, lng, originLat, originLng) {
    const scale = 10000 // meters per unit
    const x = (lng - originLng) * 111320 * Math.cos(originLat * Math.PI / 180) / scale
    const z = -(lat - originLat) * 110540 / scale
    return [x, 0, z]
}

function CameraController({ carPosition, isFollowing }) {
    const cameraRef = useRef()
    const targetPosition = useRef([0, 20, 30])

    useFrame(() => {
        if (cameraRef.current && isFollowing && carPosition) {
            // Follow car from behind and above
            const targetX = carPosition[0]
            const targetZ = carPosition[2] + 25
            const targetY = 15

            // Smooth camera movement
            cameraRef.current.position.x += (targetX - cameraRef.current.position.x) * 0.05
            cameraRef.current.position.z += (targetZ - cameraRef.current.position.z) * 0.05
            cameraRef.current.position.y += (targetY - cameraRef.current.position.y) * 0.05

            // Look at car
            cameraRef.current.lookAt(carPosition[0], 0, carPosition[2])
        }
    })

    return (
        <PerspectiveCamera
            ref={cameraRef}
            makeDefault
            position={[0, 20, 30]}
            fov={60}
        />
    )
}

function Scene({ carPosition, waypoints3D, progress, speed, isFollowing }) {
    return (
        <>
            <CameraController carPosition={carPosition} isFollowing={isFollowing} />

            {!isFollowing && (
                <OrbitControls
                    enablePan={true}
                    enableZoom={true}
                    enableRotate={true}
                    maxPolarAngle={Math.PI / 2.2}
                    minDistance={10}
                    maxDistance={100}
                />
            )}

            {/* Lighting */}
            <ambientLight intensity={0.3} />
            <directionalLight
                position={[50, 100, 50]}
                intensity={0.8}
                castShadow
                shadow-mapSize={[2048, 2048]}
            />
            <pointLight position={[0, 50, 0]} intensity={0.5} color="#4488ff" />

            {/* Scene elements */}
            <Ground />
            <GlowingRoute waypoints={waypoints3D} progress={progress} />
            <CityBuildings waypoints={waypoints3D} />
            <Car3D position={carPosition} speed={speed} />
        </>
    )
}

export default function WorldDriveScene({
    currentPosition,
    waypoints = [],
    progress = 0,
    speed = 0,
    isFollowing = true,
    onFollowingChange
}) {
    // Calculate origin from first waypoint or current position
    const origin = useMemo(() => {
        if (waypoints.length > 0) {
            return { lat: waypoints[0].lat, lng: waypoints[0].lng }
        }
        if (currentPosition) {
            return { lat: currentPosition.lat, lng: currentPosition.lng }
        }
        return { lat: 48.8354, lng: 9.152 } // Default Stuttgart
    }, [waypoints, currentPosition])

    // Convert waypoints to 3D coordinates
    const waypoints3D = useMemo(() => {
        return waypoints.map(wp => latLngTo3D(wp.lat, wp.lng, origin.lat, origin.lng))
    }, [waypoints, origin])

    // Convert current position to 3D
    const carPosition = useMemo(() => {
        if (currentPosition) {
            return latLngTo3D(currentPosition.lat, currentPosition.lng, origin.lat, origin.lng)
        }
        return [0, 0, 0]
    }, [currentPosition, origin])

    return (
        <div className="h-full w-full bg-[#0a0a0f]">
            <Canvas shadows>
                <Suspense fallback={null}>
                    <Scene
                        carPosition={carPosition}
                        waypoints3D={waypoints3D}
                        progress={progress}
                        speed={speed}
                        isFollowing={isFollowing}
                    />
                </Suspense>
                <fog attach="fog" args={['#0a0a0f', 30, 150]} />
            </Canvas>
        </div>
    )
}
