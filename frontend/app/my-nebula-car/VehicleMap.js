"use client";

import { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { Crosshair } from "lucide-react";

export default function VehicleMap({ location, vehicleName }) {
  const mapRef = useRef(null);
  const containerRef = useRef(null);
  const markerRef = useRef(null);
  const locationRef = useRef(location);

  // Keep location ref updated
  useEffect(() => {
    locationRef.current = location;
  }, [location]);

  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    // Default to a central location if no location provided yet
    const initialLat = location?.lat || 51.505;
    const initialLng = location?.lng || -0.09;

    mapRef.current = L.map(containerRef.current, {
      center: [initialLat, initialLng],
      zoom: 15,
      zoomControl: false,
      attributionControl: false,
    });

    // Dark/Night mode map tiles for premium feel
    L.tileLayer(
      "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png",
      {
        attribution:
          '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
        subdomains: "abcd",
        maxZoom: 20,
      },
    ).addTo(mapRef.current);

    // Initial Marker
    if (location) {
      updateMarker(location.lat, location.lng);
    }

    // Cleanup
    return () => {
      if (mapRef.current) {
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, []); // Run once on mount

  // Update marker when location changes
  useEffect(() => {
    if (location && mapRef.current) {
      updateMarker(location.lat, location.lng);
      mapRef.current.setView(
        [location.lat, location.lng],
        mapRef.current.getZoom(),
        {
          animate: true,
          pan: { duration: 1 },
        },
      );
    }
  }, [location]);

  // Find car button handler
  const handleFindCar = () => {
    if (locationRef.current && mapRef.current) {
      mapRef.current.flyTo(
        [locationRef.current.lat, locationRef.current.lng],
        16,
        {
          animate: true,
          duration: 1.5,
        },
      );
      // Flash the marker
      if (markerRef.current) {
        markerRef.current.openPopup();
      }
    }
  };

  const updateMarker = (lat, lng) => {
    if (!mapRef.current) return;

    const carIconHtml = `
      <div style="
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 60px;
      ">
        <div style="
          position: absolute;
          width: 100%;
          height: 100%;
          background: rgba(239, 68, 68, 0.2);
          border-radius: 50%;
          animation: pulse 2s infinite;
        "></div>
        <div style="
          width: 40px;
          height: 40px;
          background: #ef4444;
          border: 3px solid #fff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 4px 12px rgba(239, 68, 68, 0.5);
          z-index: 2;
        ">
          <span style="font-size: 20px;">🚗</span>
        </div>
      </div>
      <style>
        @keyframes pulse {
          0% { transform: scale(1); opacity: 0.8; }
          100% { transform: scale(2); opacity: 0; }
        }
      </style>
    `;

    const carIcon = L.divIcon({
      className: "custom-car-marker",
      html: carIconHtml,
      iconSize: [60, 60],
      iconAnchor: [30, 30],
    });

    if (markerRef.current) {
      markerRef.current.setLatLng([lat, lng]);
    } else {
      markerRef.current = L.marker([lat, lng], { icon: carIcon }).addTo(
        mapRef.current,
      );
    }

    // Bind popup
    markerRef.current.bindPopup(`
      <div class="p-2 text-center">
        <strong class="text-gray-900">${vehicleName || "Your Vehicle"}</strong><br/>
        <span class="text-xs text-gray-500">Live Location</span>
      </div>
    `);
  };

  return (
    <div className="relative w-full h-full rounded-2xl overflow-hidden border border-white/10 shadow-2xl">
      <div ref={containerRef} className="w-full h-full z-0" />

      {/* Map Overlay Controls - Right Side */}
      <div className="absolute top-4 right-4 z-[400] flex flex-col gap-2">
        <button
          onClick={() => mapRef.current?.zoomIn()}
          className="w-8 h-8 bg-black/80 backdrop-blur text-white rounded-lg flex items-center justify-center hover:bg-neutral-800 transition border border-white/10"
        >
          +
        </button>
        <button
          onClick={() => mapRef.current?.zoomOut()}
          className="w-8 h-8 bg-black/80 backdrop-blur text-white rounded-lg flex items-center justify-center hover:bg-neutral-800 transition border border-white/10"
        >
          -
        </button>
      </div>

      {/* Find My Car Button */}
      <button
        onClick={handleFindCar}
        className="absolute bottom-4 right-4 z-[400] flex items-center gap-2 px-4 py-2.5 
                    bg-gradient-to-r from-red-600 to-red-500 hover:from-red-500 hover:to-red-400
                    text-white rounded-xl shadow-lg shadow-red-500/30
                    transition-all duration-300 hover:scale-105 active:scale-95
                    border border-red-400/30"
      >
        <Crosshair className="w-4 h-4" />
        <span className="text-sm font-semibold">Find My Car</span>
      </button>

      {/* Live Tracking Badge */}
      <div className="absolute bottom-4 left-4 z-[400] flex items-center gap-2 bg-black/80 backdrop-blur px-3 py-1.5 rounded-lg border border-white/10">
        <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
        <span className="text-xs text-gray-400">Live Tracking Active</span>
      </div>
    </div>
  );
}
