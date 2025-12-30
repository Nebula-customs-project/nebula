// MINIMAL Realistic Vehicle Data - 4 Customization Options
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
  id: "custom-car",
  name: "Custom Vehicle",
  basePrice: 45000,
  categories: [
    // === OPTION 1: EXTERIOR PAINT ===
    {
      id: "paint",
      name: "Exterior Paint",
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
          description: "Pure alpine white",
        },
        {
          id: "paint-3",
          name: "Jamaica Blue Met",
          cost: 800,
          visualKey: "jamaica-blue-met",
          description: "Metallic ocean blue",
        },
        {
          id: "paint-4",
          name: "Rallye Red",
          cost: 1200,
          visualKey: "rallye-red",
          description: "Racing red",
        },
        {
          id: "paint-5",
          name: "Vitamin C Met",
          cost: 1500,
          visualKey: "vitamin-c-met",
          description: "Bold orange metallic",
        },
        {
          id: "paint-6",
          name: "Midnight Blue Poly",
          cost: 1000,
          visualKey: "midnight-blue-poly",
          description: "Deep midnight blue",
        },
        {
          id: "paint-7",
          name: "Silver Met",
          cost: 800,
          visualKey: "silver-met",
          description: "Metallic silver",
        },
        {
          id: "paint-8",
          name: "Lime Met Green",
          cost: 1200,
          visualKey: "lime-met-green",
          description: "Lime green metallic",
        },
        {
          id: "paint-9",
          name: "Scorch Red",
          cost: 1000,
          visualKey: "scorch-red",
          description: "Deep red metallic",
        },
        {
          id: "paint-10",
          name: "Panther Pink",
          cost: 1500,
          visualKey: "panther-pink",
          description: "Hot pink metallic",
        },
        {
          id: "paint-11",
          name: "Orange",
          cost: 900,
          visualKey: "orange",
          description: "Bright orange",
        },
        {
          id: "paint-12",
          name: "Green Go",
          cost: 1100,
          visualKey: "green-go",
          description: "Racing green",
        },
      ],
    },
    
    // === OPTION 2: INTERIOR COLOR ===
    {
      id: "interior",
      name: "Interior Color",
      icon: "🪑",
      parts: [
        {
          id: "interior-1",
          name: "Black Leather",
          cost: 0,
          visualKey: "black",
          description: "Classic black interior",
        },
        {
          id: "interior-2",
          name: "Tan Leather",
          cost: 1500,
          visualKey: "tan",
          description: "Luxury tan leather",
        },
        {
          id: "interior-3",
          name: "Red Leather",
          cost: 2000,
          visualKey: "red",
          description: "Sporty red interior",
        },
        {
          id: "interior-4",
          name: "White Leather",
          cost: 2500,
          visualKey: "white",
          description: "Premium white leather",
        },
        {
          id: "interior-5",
          name: "Blue Leather",
          cost: 2000,
          visualKey: "blue",
          description: "Cool blue interior",
        },
      ],
    },
    
    // === OPTION 3: WINDOW TINT ===
    {
      id: "tint",
      name: "Window Tint",
      icon: "🪟",
      parts: [
        {
          id: "tint-1",
          name: "None (Clear)",
          cost: 0,
          visualKey: "none",
          description: "Factory clear windows",
        },
        {
          id: "tint-2",
          name: "Light Tint",
          cost: 200,
          visualKey: "light",
          description: "35% tint - street legal",
        },
        {
          id: "tint-3",
          name: "Medium Tint",
          cost: 350,
          visualKey: "medium",
          description: "20% tint - privacy",
        },
        {
          id: "tint-4",
          name: "Dark Tint",
          cost: 500,
          visualKey: "dark",
          description: "5% tint - limo dark",
        },
        {
          id: "tint-5",
          name: "Black Out",
          cost: 800,
          visualKey: "blackout",
          description: "Full blackout - stealth",
        },
      ],
    },
    
    // === OPTION 4: RIDE HEIGHT ===
    {
      id: "stance",
      name: "Ride Height",
      icon: "⬇️",
      parts: [
        {
          id: "stance-1",
          name: "Stock Height",
          cost: 0,
          visualKey: "stock",
          description: "Factory ride height",
        },
        {
          id: "stance-2",
          name: "Lowered (-1 inch)",
          cost: 800,
          visualKey: "low-1",
          description: "Sport lowered",
        },
        {
          id: "stance-3",
          name: "Lowered (-2 inches)",
          cost: 1500,
          visualKey: "low-2",
          description: "Aggressive stance",
        },
        {
          id: "stance-4",
          name: "Slammed (-3 inches)",
          cost: 2500,
          visualKey: "slammed",
          description: "Race slammed",
        },
        {
          id: "stance-5",
          name: "Air Ride",
          cost: 5000,
          visualKey: "air-ride",
          description: "Adjustable air suspension",
        },
      ],
    },
  ],
};

// Default configuration
export const defaultConfiguration: Record<string, string> = {
  paint: "black",
  interior: "black",
  tint: "none",
  stance: "stock",
};
