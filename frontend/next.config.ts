import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Enable standalone output for Docker deployment
  output: "standalone",
  
  // Disable powered by header for security
  poweredByHeader: false,
};

export default nextConfig;
