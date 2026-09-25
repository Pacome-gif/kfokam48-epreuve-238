import type { NextConfig } from "next";

// Le navigateur appelle /api/... sur le même domaine ; Next.js relaie vers le backend Spring Boot.
// Pas de CORS à gérer. BACKEND_URL est lu au build (docker compose : http://backend:8080).
const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  output: "standalone",
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${backendUrl}/api/:path*` }];
  },
};

export default nextConfig;
