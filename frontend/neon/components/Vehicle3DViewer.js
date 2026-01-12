'use client'

import React from 'react'
import Vehicle3DScene from './Vehicle3DScene'

export default function Vehicle3DViewer({ vehicleName, configuration }) {
  return (
    <Vehicle3DScene
      vehicleName={vehicleName}
      configuration={configuration}
    />
  )
}
