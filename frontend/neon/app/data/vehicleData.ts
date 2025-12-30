// Mock data structure for vehicle customization
export interface Part {
  id: string;
  name: string;
  cost: number;
  visualKey: string;
  description?: string;
}

export interface CustomizationCategory {
  id: string;
  name: string;
  icon: string;
  parts: Part[];
}

export interface Vehicle {
  id: string;
  name: string;
  basePrice: number;
  categories: CustomizationCategory[];
}

export const mockVehicle: Vehicle = {
  id: "neon-turbo",
  name: "Neon Turbo GTX",
  basePrice: 45000,
  categories: [
    {
      id: "paint",
      name: "Paint",
      icon: "🎨",
      parts: [
        {
          id: "paint-1",
          name: "Black",
          cost: 0,
          visualKey: "black",
          description: "Classic glossy black",
        },
        {
          id: "paint-2",
          name: "Alpine White",
          cost: 500,
          visualKey: "alpine-white",
          description: "Pure alpine white finish",
        },
        {
          id: "paint-3",
          name: "Jamaica Blue Met",
          cost: 800,
          visualKey: "jamaica-blue-met",
          description: "Metallic blue with depth",
        },
        {
          id: "paint-4",
          name: "Rallye Red",
          cost: 1200,
          visualKey: "rallye-red",
          description: "Vibrant racing red",
        },
        {
          id: "paint-5",
          name: "Vitamin C Met / Go Mango",
          cost: 1500,
          visualKey: "vitamin-c-met",
          description: "Bold orange-red metallic",
        },
        {
          id: "paint-6",
          name: "Midnight Blue Poly",
          cost: 1000,
          visualKey: "midnight-blue-poly",
          description: "Deep midnight blue with poly finish",
        },
        {
          id: "paint-7",
          name: "Silver Met",
          cost: 800,
          visualKey: "silver-met",
          description: "Metallic silver shine",
        },
        {
          id: "paint-8",
          name: "Lime Met Green",
          cost: 1200,
          visualKey: "lime-met-green",
          description: "Vibrant lime green metallic",
        },
        {
          id: "paint-9",
          name: "Scorch Red",
          cost: 1000,
          visualKey: "scorch-red",
          description: "Intense red metallic",
        },
        {
          id: "paint-10",
          name: "Panther Pink / Moulin Rouge",
          cost: 1500,
          visualKey: "panther-pink",
          description: "Bold pink metallic",
        },
        {
          id: "paint-11",
          name: "Orange",
          cost: 900,
          visualKey: "orange",
          description: "Bright vibrant orange",
        },
        {
          id: "paint-12",
          name: "Green Go / Sassy Green",
          cost: 1100,
          visualKey: "green-go",
          description: "Bright racing green",
        },
      ],
    },
    {
      id: "rims",
      name: "Rims",
      icon: "⭕",
      parts: [
        {
          id: "rim-1",
          name: "Sport A",
          cost: 0,
          visualKey: "sport-a",
          description: "Standard sport rims",
        },
        {
          id: "rim-2",
          name: "Sport B",
          cost: 1500,
          visualKey: "sport-b",
          description: "Five-spoke design",
        },
        {
          id: "rim-3",
          name: "Racing Pro",
          cost: 3000,
          visualKey: "racing-pro",
          description: "Lightweight racing rims",
        },
        {
          id: "rim-4",
          name: "Chrome Luxury",
          cost: 4500,
          visualKey: "chrome-luxury",
          description: "Premium chrome finish",
        },
        {
          id: "rim-5",
          name: "Carbon Fiber",
          cost: 6000,
          visualKey: "carbon-fiber",
          description: "Ultra-light carbon fiber",
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
          cost: 800,
          visualKey: "lip-spoiler",
          description: "Subtle lip spoiler",
        },
        {
          id: "spoiler-3",
          name: "Carbon Wing",
          cost: 2500,
          visualKey: "carbon-wing",
          description: "Aggressive carbon fiber wing",
        },
        {
          id: "spoiler-4",
          name: "GT Wing",
          cost: 3500,
          visualKey: "gt-wing",
          description: "High downforce GT wing",
        },
        {
          id: "spoiler-5",
          name: "Ducktail",
          cost: 2000,
          visualKey: "ducktail",
          description: "Classic ducktail design",
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
          cost: 1200,
          visualKey: "sport",
          description: "Aerodynamic sport bumper",
        },
        {
          id: "bumper-3",
          name: "Carbon Fiber",
          cost: 2800,
          visualKey: "carbon-fiber",
          description: "Lightweight carbon fiber",
        },
        {
          id: "bumper-4",
          name: "Wide Body",
          cost: 4500,
          visualKey: "wide-body",
          description: "Aggressive wide body kit",
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
          cost: 1500,
          visualKey: "sport",
          description: "Enhanced sport exhaust",
        },
        {
          id: "exhaust-3",
          name: "Titanium",
          cost: 3500,
          visualKey: "titanium",
          description: "Lightweight titanium system",
        },
        {
          id: "exhaust-4",
          name: "Racing",
          cost: 5000,
          visualKey: "racing",
          description: "Full racing exhaust system",
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
          name: "Standard Cloth",
          cost: 0,
          visualKey: "standard-cloth",
          description: "Standard cloth seats",
        },
        {
          id: "interior-2",
          name: "Leather Black",
          cost: 2000,
          visualKey: "leather-black",
          description: "Premium black leather",
        },
        {
          id: "interior-3",
          name: "Leather Red",
          cost: 2200,
          visualKey: "leather-red",
          description: "Sporty red leather",
        },
        {
          id: "interior-4",
          name: "Carbon Sport",
          cost: 4000,
          visualKey: "carbon-sport",
          description: "Carbon fiber racing seats",
        },
        {
          id: "interior-5",
          name: "Luxury Tan",
          cost: 3500,
          visualKey: "luxury-tan",
          description: "Premium tan leather",
        },
      ],
    },
    {
      id: "engine",
      name: "Engine",
      icon: "⚙️",
      parts: [
        {
          id: "engine-1",
          name: "Stock",
          cost: 0,
          visualKey: "stock",
          description: "Factory engine",
        },
        {
          id: "engine-2",
          name: "Turbo Kit",
          cost: 5000,
          visualKey: "turbo-kit",
          description: "+50 HP turbo upgrade",
        },
        {
          id: "engine-3",
          name: "Supercharger",
          cost: 8000,
          visualKey: "supercharger",
          description: "+100 HP supercharger",
        },
        {
          id: "engine-4",
          name: "Race Engine",
          cost: 15000,
          visualKey: "race-engine",
          description: "+200 HP full race build",
        },
      ],
    },
    {
      id: "suspension",
      name: "Suspension",
      icon: "🔧",
      parts: [
        {
          id: "suspension-1",
          name: "Stock",
          cost: 0,
          visualKey: "stock",
          description: "Factory suspension",
        },
        {
          id: "suspension-2",
          name: "Lowered",
          cost: 1200,
          visualKey: "lowered",
          description: "Lowered sport suspension",
        },
        {
          id: "suspension-3",
          name: "Coilovers",
          cost: 2500,
          visualKey: "coilovers",
          description: "Adjustable coilover kit",
        },
        {
          id: "suspension-4",
          name: "Air Ride",
          cost: 5000,
          visualKey: "air-ride",
          description: "Adjustable air suspension",
        },
      ],
    },
  ],
};

// Default configuration (using the first part of each category)
export const defaultConfiguration: Record<string, string> = {
  paint: "black",
  rims: "sport-a",
  spoilers: "none",
  bumpers: "stock",
  exhaust: "stock",
  interior: "standard-cloth",
  engine: "stock",
  suspension: "stock",
};
