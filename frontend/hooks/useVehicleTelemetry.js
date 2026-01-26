"use client";

import { useState, useEffect, useRef } from "react";
import { useAuth } from "./useAuth";
import { getAccessToken } from "../lib/api";

/**
 * Custom hook for connecting to the vehicle telemetry WebSocket.
 * Provides real-time vehicle location and fuel data.
 * 
 * @returns {{ telemetry: object|null, isConnected: boolean, error: Error|null }}
 */
export function useVehicleTelemetry() {
    const { user } = useAuth();
    const [telemetry, setTelemetry] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState(null);
    const wsRef = useRef(null);
    const reconnectTimeoutRef = useRef(null);
    const shouldReconnectRef = useRef(true);

    // Get WebSocket URL from environment or use default
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || "ws://localhost:8080/ws/vehicle-telemetry";

    useEffect(() => {
        if (!user?.id) {
            return;
        }

        shouldReconnectRef.current = true;

        const connect = () => {
            // Clear any existing connection
            if (wsRef.current) {
                wsRef.current.close();
            }

            try {
                // Get JWT token from in-memory auth state
                // WebSocket requires token in query param because it can't rely on cookies for cross-origin
                const authToken = getAccessToken();

                if (!authToken) {
                    // Try to wait? Or just fail?
                    // If we just logged in, it should be there.
                    // If we refreshed page, we might not have it yet...
                    // ERROR: If page refresh, validateSession runs but might not populate accessToken if it just does apiClient.get()
                    // apiClient.get will trigger refresh if needed, setting accessToken.
                    console.warn("[WebSocket] No auth token available, cannot connect");
                    setError(new Error("Authentication required"));
                    return;
                }

                // Create WebSocket connection with token in query param
                // Gateway will validate JWT and inject X-User-Id header for downstream service
                const wsUrlWithToken = `${wsUrl}?token=${encodeURIComponent(authToken)}`;
                const ws = new WebSocket(wsUrlWithToken);
                wsRef.current = ws;

                ws.onopen = () => {
                    console.log("[WebSocket] Connected to vehicle telemetry");
                    setIsConnected(true);
                    setError(null);
                };

                ws.onmessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        // Map backend response to frontend format
                        setTelemetry({
                            vehicleName: data.vehicleName,
                            carLocation: data.location ? {
                                lat: data.location.lat,
                                lng: data.location.lng,
                            } : null,
                            fuelLevel: data.fuel ? parseFloat(data.fuel) : null,
                            timestamp: data.timestamp,
                        });
                    } catch (parseError) {
                        console.error("[WebSocket] Failed to parse message:", parseError);
                    }
                };

                ws.onerror = (event) => {
                    console.error("[WebSocket] Error:", event);
                    setError(new Error("WebSocket connection error"));
                    setIsConnected(false);
                };

                ws.onclose = (event) => {
                    console.log("[WebSocket] Connection closed:", event.code, event.reason);
                    setIsConnected(false);
                    wsRef.current = null;

                    // Attempt to reconnect after 5 seconds if not intentionally closed
                    if (event.code !== 1000 && shouldReconnectRef.current) {
                        reconnectTimeoutRef.current = setTimeout(() => {
                            console.log("[WebSocket] Attempting to reconnect...");
                            connect();
                        }, 5000);
                    }
                };
            } catch (err) {
                console.error("[WebSocket] Failed to initialize:", err);
                setError(err);
                setIsConnected(false);
            }
        };

        connect();

        // Cleanup on unmount
        return () => {
            shouldReconnectRef.current = false;
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
            if (wsRef.current) {
                wsRef.current.close(1000, "Component unmounted");
            }
        };
    }, [user?.id, wsUrl]);

    return { telemetry, isConnected, error };
}

export default useVehicleTelemetry;