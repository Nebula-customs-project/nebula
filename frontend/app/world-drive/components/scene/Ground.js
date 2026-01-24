'use client'

import { useRef } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'

export default function Ground() {
    const gridRef = useRef()

    // Subtle animation for grid
    useFrame((state) => {
        if (gridRef.current) {
            gridRef.current.material.opacity = 0.15 + Math.sin(state.clock.elapsedTime * 0.5) * 0.05
        }
    })

    return (
        <group>
            {/* Main ground plane */}
            <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.01, 0]} receiveShadow>
                <planeGeometry args={[500, 500]} />
                <meshStandardMaterial color="#0a0a0f" roughness={0.9} metalness={0.1} />
            </mesh>

            {/* Grid overlay */}
            <gridHelper
                ref={gridRef}
                args={[500, 100, '#1a1a3e', '#1a1a3e']}
                position={[0, 0, 0]}
            />

            {/* Fog effect for depth */}
            <fog attach="fog" args={['#0a0a0f', 50, 200]} />
        </group>
    )
}
