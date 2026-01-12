/**
 * Vehicle Data Structure
 * 
 * Contains the vehicle model data, customization categories, and parts.
 * This is the single source of truth for all configurator data.
 */

// Vehicle data structure for Nebula car configurator
export const mockVehicle = {
  id: "nebula-apex",
  name: "Nebula Apex",
  basePrice: 245000,
  categories: [
    {
      id: "paint",
      name: "Exterior Color",
      icon: "🎨",
      parts: [
        {
          id: "paint-1",
          name: "Racing Red",
          cost: 0,
          visualKey: "racing-red",
          description: "Classic racing red",
          hex: "#DC2626"
        },
        {
          id: "paint-2",
          name: "Midnight Black",
          cost: 0,
          visualKey: "midnight-black",
          description: "Deep midnight black",
          hex: "#000000"
        },
        {
          id: "paint-3",
          name: "Pearl White",
          cost: 1200,
          visualKey: "pearl-white",
          description: "Premium pearl white finish",
          hex: "#FFFFFF"
        },
        {
          id: "paint-4",
          name: "Ocean Blue",
          cost: 1500,
          visualKey: "ocean-blue",
          description: "Vibrant ocean blue metallic",
          hex: "#2563EB"
        },
        {
          id: "paint-5",
          name: "Silver Metallic",
          cost: 800,
          visualKey: "silver-metallic",
          description: "Elegant silver metallic",
          hex: "#94A3B8"
        },
        {
          id: "paint-6",
          name: "Sunset Orange",
          cost: 1800,
          visualKey: "sunset-orange",
          description: "Bold sunset orange",
          hex: "#F97316"
        },
        {
          id: "paint-7",
          name: "Electric Green",
          cost: 2000,
          visualKey: "electric-green",
          description: "Vibrant electric green",
          hex: "#10B981"
        }
      ],
    },
    {
      id: "rims",
      name: "Rims",
      icon: "⭕",
      parts: [
        {
          id: "rim-1",
          name: "Sport 19\"",
          cost: 0,
          visualKey: "sport",
          description: "Standard sport rims",
        },
        {
          id: "rim-2",
          name: "Performance 20\"",
          cost: 2500,
          visualKey: "performance",
          description: "High-performance lightweight rims",
        },
        {
          id: "rim-3",
          name: "Premium 21\"",
          cost: 4800,
          visualKey: "premium",
          description: "Premium carbon fiber rims",
        },
        {
          id: "rim-4",
          name: "Racing Pro",
          cost: 6500,
          visualKey: "racing-pro",
          description: "Track-focused racing rims",
        },
      ],
    },
    {
      id: "interior",
      name: "Interior",
      icon: "🪑",
      parts: [
        {
          id: "interior-1",
          name: "Black Leather",
          cost: 0,
          visualKey: "black",
          description: "Premium black leather",
        },
        {
          id: "interior-2",
          name: "Beige Leather",
          cost: 2000,
          visualKey: "beige",
          description: "Luxury beige leather",
        },
        {
          id: "interior-3",
          name: "Red Sport",
          cost: 2500,
          visualKey: "red",
          description: "Sporty red leather with carbon accents",
        },
        {
          id: "interior-4",
          name: "Carbon Fiber",
          cost: 4000,
          visualKey: "carbon",
          description: "Carbon fiber racing seats",
        },
      ],
    },
    {
      id: "spoilers",
      name: "Spoilers",
      icon: "🛫",
      parts: [
        {
          id: "spoiler-1",
          name: "None",
          cost: 0,
          visualKey: "none",
          description: "No spoiler",
        },
        {
          id: "spoiler-2",
          name: "Lip Spoiler",
          cost: 1200,
          visualKey: "lip-spoiler",
          description: "Subtle lip spoiler",
        },
        {
          id: "spoiler-3",
          name: "Carbon Wing",
          cost: 3500,
          visualKey: "carbon-wing",
          description: "Aggressive carbon fiber wing",
        },
        {
          id: "spoiler-4",
          name: "GT Wing",
          cost: 4500,
          visualKey: "gt-wing",
          description: "High downforce GT wing",
        },
      ],
    },
    {
      id: "bumpers",
      name: "Bumpers",
      icon: "🚗",
      parts: [
        {
          id: "bumper-1",
          name: "Stock",
          cost: 0,
          visualKey: "stock",
          description: "Standard bumper",
        },
        {
          id: "bumper-2",
          name: "Sport",
          cost: 1800,
          visualKey: "sport",
          description: "Aerodynamic sport bumper",
        },
        {
          id: "bumper-3",
          name: "Carbon Fiber",
          cost: 3500,
          visualKey: "carbon-fiber",
          description: "Lightweight carbon fiber",
        },
      ],
    },
    {
      id: "exhaust",
      name: "Exhaust",
      icon: "💨",
      parts: [
        {
          id: "exhaust-1",
          name: "Stock",
          cost: 0,
          visualKey: "stock",
          description: "Factory exhaust",
        },
        {
          id: "exhaust-2",
          name: "Sport",
          cost: 2200,
          visualKey: "sport",
          description: "Enhanced sport exhaust",
        },
        {
          id: "exhaust-3",
          name: "Titanium",
          cost: 4500,
          visualKey: "titanium",
          description: "Lightweight titanium system",
        },
      ],
    },
  ],
};

/**
 * Default Configuration
 * 
 * Initial configuration state when the configurator loads.
 * Uses the first/default option from each category.
 */
export const defaultConfiguration = {
  paint: "racing-red",
  rims: "sport",
  interior: "black",
  spoilers: "none",
  bumpers: "stock",
  exhaust: "stock",
};
