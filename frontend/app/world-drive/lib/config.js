'use client'

/**
 * Configuration for World Drive API and MQTT connections.
 * 
 * Required environment variables:
 * - NEXT_PUBLIC_API_URL: World-view service URL (default: http://localhost:8082)
 * - NEXT_PUBLIC_MQTT_URL: MQTT WebSocket URL (default: ws://localhost:15675/ws)
 * - NEXT_PUBLIC_MQTT_USERNAME: MQTT username (required for authentication)
 * - NEXT_PUBLIC_MQTT_PASSWORD: MQTT password (required for authentication)
 * 
 * Set these in your .env.local file (not committed to git).
 */

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8082'
export const MQTT_BROKER_URL = process.env.NEXT_PUBLIC_MQTT_URL || 'ws://localhost:15675/ws'
export const MQTT_USERNAME = process.env.NEXT_PUBLIC_MQTT_USERNAME
export const MQTT_PASSWORD = process.env.NEXT_PUBLIC_MQTT_PASSWORD