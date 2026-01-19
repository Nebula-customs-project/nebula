"use client";

import { useState, useEffect, useMemo, useCallback } from "react";
import { vehicleServiceApi } from "../lib/api";

/**
 * useVehicleService Hook
 *
 * Handles vehicle fetching, service health monitoring, and retry logic.
 * Extracted from page.js for better maintainability.
 */
export function useVehicleService() {
  const [vehicles, setVehicles] = useState([]);
  const [currentVehicleId, setCurrentVehicleId] = useState(null);
  const [serviceStatus, setServiceStatus] = useState("loading");
  const [serviceMessage, setServiceMessage] = useState("");

  // Get current vehicle data
  const currentVehicle = useMemo(() => {
    if (!currentVehicleId || !vehicles.length) return null;
    const vehicle =
      vehicles.find((v) => v.vehicleId === currentVehicleId) || vehicles[0];
    return vehicle
      ? {
          id: vehicle.vehicleId,
          name: vehicle.carName || vehicle.name,
          modelPath: vehicle.modelPath,
        }
      : null;
  }, [currentVehicleId, vehicles]);

  // Fetch vehicles on mount with retry logic
  useEffect(() => {
    let isMounted = true;
    let retryInterval = null;

    const checkServiceAndFetch = async () => {
      try {
        setServiceStatus("loading");
        setServiceMessage("Connecting to vehicle service...");

        const result = await vehicleServiceApi.getAllVehicles();

        if (!isMounted) return;

        if (result.error) {
          setServiceStatus("error");
          setServiceMessage(result.message);
          setVehicles([]);

          // Retry every 5 seconds on error
          if (!retryInterval && isMounted) {
            retryInterval = setInterval(() => {
              if (isMounted) checkServiceAndFetch();
            }, 5000);
          }
          return;
        }

        const fetchedVehicles = result.vehicles;

        if (!fetchedVehicles || fetchedVehicles.length === 0) {
          setServiceStatus("error");
          setServiceMessage(
            "No vehicles found in database. Please seed the database.",
          );
          setVehicles([]);
          return;
        }

        setVehicles(fetchedVehicles);
        setServiceStatus("connected");
        setServiceMessage("");

        // Set first vehicle as default
        if (fetchedVehicles.length > 0 && !currentVehicleId) {
          setCurrentVehicleId(fetchedVehicles[0].vehicleId);
        }

        // Clear retry interval on success
        if (retryInterval) {
          clearInterval(retryInterval);
          retryInterval = null;
        }
      } catch (error) {
        if (!isMounted) return;
        console.error("Unexpected error:", error);
        setServiceStatus("error");
        setServiceMessage("An unexpected error occurred. Please try again.");
        setVehicles([]);
      }
    };

    checkServiceAndFetch();

    return () => {
      isMounted = false;
      if (retryInterval) clearInterval(retryInterval);
    };
  }, [currentVehicleId]);

  // Reset service state (for retry button)
  const resetServiceState = useCallback(() => {
    setServiceStatus("loading");
    setCurrentVehicleId(null);
  }, []);

  return {
    vehicles,
    currentVehicle,
    currentVehicleId,
    setCurrentVehicleId,
    serviceStatus,
    serviceMessage,
    resetServiceState,
  };
}
