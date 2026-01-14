'use client'

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'
export const MQTT_BROKER_URL = process.env.NEXT_PUBLIC_MQTT_URL || 'ws://localhost:15675/ws'
export const MQTT_USERNAME = process.env.NEXT_PUBLIC_MQTT_USERNAME
export const MQTT_PASSWORD = process.env.NEXT_PUBLIC_MQTT_PASSWORD
