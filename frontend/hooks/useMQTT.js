"use client";

import { useState, useEffect, useCallback } from "react";
import mqtt from "mqtt";

/**
 * Custom hook for subscribing to MQTT topics
 * @param {string} topic - MQTT topic to subscribe to
 * @returns {any} - Latest message received on the topic
 */
export function useMQTT(topic) {
  const [data, setData] = useState(null);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    // Get MQTT configuration from environment variables only (no hardcoded fallbacks)
    const mqttUrl = process.env.NEXT_PUBLIC_MQTT_URL;
    const username = process.env.NEXT_PUBLIC_MQTT_USERNAME;
    const password = process.env.NEXT_PUBLIC_MQTT_PASSWORD;

    // Don't attempt connection if credentials are missing
    if (!mqttUrl || !username || !password) {
      console.warn(
        "[MQTT] Missing configuration - check environment variables",
      );
      return;
    }

    let client = null;

    try {
      client = mqtt.connect(mqttUrl, {
        username,
        password,
        reconnectPeriod: 5000,
        connectTimeout: 30000,
      });

      client.on("connect", () => {
        setIsConnected(true);
        console.log(`[MQTT] Connected, subscribing to: ${topic}`);
        client.subscribe(topic, (err) => {
          if (err) {
            console.error(`[MQTT] Subscribe error for ${topic}:`, err);
          }
        });
      });

      client.on("message", (receivedTopic, message) => {
        if (receivedTopic === topic) {
          try {
            const parsed = JSON.parse(message.toString());
            setData(parsed);
          } catch {
            // If not JSON, use raw value
            const rawValue = message.toString();
            const numValue = parseFloat(rawValue);
            setData(isNaN(numValue) ? rawValue : numValue);
          }
        }
      });

      client.on("error", (err) => {
        console.error("[MQTT] Connection error:", err);
        setIsConnected(false);
      });

      client.on("close", () => {
        setIsConnected(false);
      });
    } catch (err) {
      console.error("[MQTT] Failed to initialize:", err);
    }

    // Cleanup on unmount
    return () => {
      if (client) {
        client.unsubscribe(topic);
        client.end();
      }
    };
  }, [topic]);

  return data;
}

export default useMQTT;
