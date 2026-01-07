'use client'

import { useState, useEffect } from 'react'

export function useMQTT(topic) {
  const [data, setData] = useState(null)
  
  useEffect(() => {
    // Simulate MQTT data updates
    const interval = setInterval(() => {
      if (topic === 'supercar/location') {
        setData({
          lat: 48.7758 + (Math.random() - 0.5) * 0.1,
          lng: 9.1829 + (Math.random() - 0.5) * 0.1,
          timestamp: Date.now()
        })
      } else if (topic === 'mycar/fuel') {
        setData(prev => Math.max(10, Math.min(100, (prev || 75) + (Math.random() - 0.5) * 2)))
      } else if (topic === 'mycar/location') {
        setData({
          lat: 48.7758 + (Math.random() - 0.5) * 0.05,
          lng: 9.1829 + (Math.random() - 0.5) * 0.05
        })
      }
    }, 3000)
    
    return () => clearInterval(interval)
  }, [topic])
  
  return data
}