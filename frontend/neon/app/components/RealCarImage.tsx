"use client";

import React from "react";
import Image from "next/image";

interface RealCarImageProps {
  configuration: Record<string, string>;
  colorFilter: string;
  colorHex: string;
}

/**
 * This component will display a real car photo when available
 * Falls back to SVG placeholder if no photo is found
 */
const RealCarImage: React.FC<RealCarImageProps> = ({
  configuration,
  colorFilter,
  colorHex,
}) => {
  const [hasRealImage, setHasRealImage] = React.useState(false);
  const [imageLoaded, setImageLoaded] = React.useState(false);

  // Check if real car image exists
  React.useEffect(() => {
    // Try to load the image
    const img = new window.Image();
    img.src = "/cars/base/car-side.png";
    img.onload = () => {
      setHasRealImage(true);
      setImageLoaded(true);
    };
    img.onerror = () => {
      // Try JPG format
      const jpgImg = new window.Image();
      jpgImg.src = "/cars/base/car-side.jpg";
      jpgImg.onload = () => {
        setHasRealImage(true);
        setImageLoaded(true);
      };
      jpgImg.onerror = () => {
        setHasRealImage(false);
        setImageLoaded(true);
      };
    };
  }, []);

  if (!imageLoaded) {
    // Loading state
    return (
      <div className="w-full h-full flex items-center justify-center">
        <div className="text-white text-lg animate-pulse">Loading...</div>
      </div>
    );
  }

  if (hasRealImage) {
    // Real car image found!
    return (
      <div className="relative w-full h-full">
        <Image
          src="/cars/base/car-side.png"
          alt="Custom Vehicle"
          fill
          className="object-contain"
          style={{ filter: colorFilter }}
          priority
          onError={() => {
            // Fallback to JPG if PNG fails
            const img = document.querySelector(
              'img[alt="Custom Vehicle"]'
            ) as HTMLImageElement;
            if (img) img.src = "/cars/base/car-side.jpg";
          }}
        />
        {/* Color overlay for extra realism */}
        <div
          className="absolute inset-0 mix-blend-overlay opacity-20 pointer-events-none"
          style={{ backgroundColor: colorHex }}
        ></div>
      </div>
    );
  }

  // Fallback SVG placeholder
  return (
    <div className="w-full h-full flex items-center justify-center">
      <div className="text-center p-8 max-w-md bg-gray-800/50 rounded-xl border border-dashed border-gray-600">
        <div className="text-5xl mb-3">🚗</div>
        <h4 className="text-white font-bold text-lg mb-2">
          Add Your Real Car Photo
        </h4>
        <p className="text-gray-300 text-sm mb-4">
          Save your car image (like the Mustang) to:
        </p>
        <code className="block bg-gray-900 text-green-300 px-3 py-2 rounded text-xs mb-4">
          public/cars/base/car-side.png
        </code>
        <p className="text-gray-400 text-xs">
          Then refresh to see your real car with dynamic color changes!
        </p>
      </div>
    </div>
  );
};

export default RealCarImage;
