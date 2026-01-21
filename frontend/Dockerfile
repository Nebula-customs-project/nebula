# =============================================================================
# Nebula Frontend Dockerfile
# Multi-stage build for optimized Next.js production image
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Dependencies
# -----------------------------------------------------------------------------
FROM node:20-alpine AS deps

WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# -----------------------------------------------------------------------------
# Stage 2: Builder
# -----------------------------------------------------------------------------
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependencies from deps stage
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Build arguments for environment variables (passed at build time)
ARG NEXT_PUBLIC_GATEWAY_URL=http://gateway-service:8080
ARG NEXT_PUBLIC_MQTT_URL=ws://rabbitmq:15675/ws
ARG NEXT_PUBLIC_MQTT_USERNAME=nebula
ARG NEXT_PUBLIC_MQTT_PASSWORD=nebula@2025

# Set environment variables for build
ENV NEXT_PUBLIC_GATEWAY_URL=$NEXT_PUBLIC_GATEWAY_URL
ENV NEXT_PUBLIC_MQTT_URL=$NEXT_PUBLIC_MQTT_URL
ENV NEXT_PUBLIC_MQTT_USERNAME=$NEXT_PUBLIC_MQTT_USERNAME
ENV NEXT_PUBLIC_MQTT_PASSWORD=$NEXT_PUBLIC_MQTT_PASSWORD

# Build the application
RUN npm run build

# -----------------------------------------------------------------------------
# Stage 3: Runner (Production)
# -----------------------------------------------------------------------------
FROM node:20-alpine AS runner

LABEL maintainer="Nebula Team"
LABEL service="frontend"
LABEL version="1.0.0"

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S nebula && \
    adduser -u 1001 -S nebula -G nebula

# Set production environment
ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

# Copy built application
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

# Change ownership
RUN chown -R nebula:nebula /app

# Switch to non-root user
USER nebula

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:3000 || exit 1

# Start the application
CMD ["node", "server.js"]
