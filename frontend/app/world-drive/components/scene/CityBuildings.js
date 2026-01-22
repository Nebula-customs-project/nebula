'use client'

import { useMemo } from 'react'
import * as THREE from 'three'

// Seeded random number generator for consistent buildings
function seededRandom(seed) {
    const x = Math.sin(seed) * 10000
    return x - Math.floor(x)
}

export default function CityBuildings({ waypoints = [], routeWidth = 20 }) {
    const buildings = useMemo(() => {
        if (waypoints.length < 2) return []

        const buildingList = []
        let buildingId = 0

        // Generate buildings along the route
        for (let i = 0; i < waypoints.length - 1; i++) {
            const start = waypoints[i]
            const end = waypoints[i + 1]

            // Direction vector
            const dx = end[0] - start[0]
            const dz = end[2] - start[2]
            const length = Math.sqrt(dx * dx + dz * dz)

            if (length < 1) continue

            // Perpendicular direction for building placement
            const perpX = -dz / length
            const perpZ = dx / length

            // Number of buildings based on segment length
            const numBuildings = Math.floor(length / 8)

            for (let j = 0; j < numBuildings; j++) {
                const t = (j + 0.5) / numBuildings
                const baseX = start[0] + dx * t
                const baseZ = start[2] + dz * t

                // Buildings on both sides
                for (const side of [-1, 1]) {
                    const seed = buildingId * 1000 + i * 100 + j * 10 + side

                    // Random offset from road
                    const offsetDistance = routeWidth + 5 + seededRandom(seed) * 15

                    const x = baseX + perpX * offsetDistance * side
                    const z = baseZ + perpZ * offsetDistance * side

                    // Random building properties
                    const height = 3 + seededRandom(seed + 1) * 12
                    const width = 2 + seededRandom(seed + 2) * 4
                    const depth = 2 + seededRandom(seed + 3) * 4

                    buildingList.push({
                        id: buildingId++,
                        position: [x, height / 2, z],
                        size: [width, height, depth],
                        rotation: seededRandom(seed + 4) * Math.PI * 0.1,
                    })
                }
            }
        }

        return buildingList
    }, [waypoints, routeWidth])

    if (buildings.length === 0) {
        // Generate some default buildings around origin
        const defaultBuildings = []
        for (let i = 0; i < 30; i++) {
            const angle = (i / 30) * Math.PI * 2
            const distance = 30 + seededRandom(i) * 40
            const height = 5 + seededRandom(i + 100) * 15

            defaultBuildings.push({
                id: i,
                position: [
                    Math.cos(angle) * distance,
                    height / 2,
                    Math.sin(angle) * distance
                ],
                size: [3 + seededRandom(i + 200) * 5, height, 3 + seededRandom(i + 300) * 5],
                rotation: seededRandom(i + 400) * Math.PI * 0.2
            })
        }

        return (
            <group>
                {defaultBuildings.map(building => (
                    <Building key={building.id} {...building} />
                ))}
            </group>
        )
    }

    return (
        <group>
            {buildings.map(building => (
                <Building key={building.id} {...building} />
            ))}
        </group>
    )
}

function Building({ position, size, rotation }) {
    return (
        <group position={position} rotation={[0, rotation, 0]}>
            {/* Main building body */}
            <mesh castShadow receiveShadow>
                <boxGeometry args={size} />
                <meshStandardMaterial
                    color="#1a1a2e"
                    roughness={0.8}
                    metalness={0.2}
                />
            </mesh>

            {/* Edge highlight */}
            <lineSegments>
                <edgesGeometry args={[new THREE.BoxGeometry(...size)]} />
                <lineBasicMaterial color="#2a2a4e" transparent opacity={0.5} />
            </lineSegments>

            {/* Window lights (random pattern) */}
            {size[1] > 5 && (
                <WindowLights size={size} />
            )}
        </group>
    )
}

function WindowLights({ size }) {
    const windows = useMemo(() => {
        const list = []
        const floors = Math.floor(size[1] / 2)
        const windowsPerFloor = Math.floor(size[0] / 1.5)

        for (let floor = 0; floor < floors; floor++) {
            for (let w = 0; w < windowsPerFloor; w++) {
                // Random chance of window being lit
                if (Math.random() > 0.6) {
                    const x = (w - windowsPerFloor / 2 + 0.5) * 1.2
                    const y = (floor - floors / 2 + 0.5) * 2

                    list.push({
                        position: [x, y, size[2] / 2 + 0.01],
                        color: Math.random() > 0.3 ? '#3a3a5e' : '#5a5a8e'
                    })
                }
            }
        }

        return list
    }, [size])

    return (
        <>
            {windows.map((window, i) => (
                <mesh key={i} position={window.position}>
                    <planeGeometry args={[0.8, 1.2]} />
                    <meshBasicMaterial color={window.color} />
                </mesh>
            ))}
        </>
    )
}
