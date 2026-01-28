
import type { NextConfig } from "next";

// Server-side API URL for rewrites (used by Next.js server to proxy API requests)
// In Docker: set to http://gateway-service:8080 (internal Docker network)
// Local dev: defaults to http://localhost:8080
const serverApiUrl = process.env.SERVER_API_URL || "http://localhost:8080";

const nextConfig: NextConfig = {
  // Enable standalone output for Docker deployment
  output: "standalone",
  // Disable powered by header for security
  poweredByHeader: false,
  turbopack: {
    resolveAlias: {
      "@": "./",
    },
  },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${serverApiUrl}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
