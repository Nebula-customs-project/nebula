// Shared vehicle API for frontend pages
import { API_ERROR_TYPES } from '../car-configurator/lib/api';

const VEHICLE_SERVICE_URL = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080';
const API_TIMEOUT_MS = 5000;

const createApiError = (type, message, originalError = null) => ({
  error: true,
  type,
  message,
  originalError,
});

export async function fetchAllVehicles() {
  try {
    const res = await fetch(`${VEHICLE_SERVICE_URL}/api/v1/vehicles/?size=100`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      signal: AbortSignal.timeout(API_TIMEOUT_MS),
    });
    if (!res.ok) {
      if ([500, 502, 503].includes(res.status)) {
        return createApiError(API_ERROR_TYPES.SERVER_ERROR, 'Vehicle service is not running.');
      }
      if (res.status === 404) {
        return createApiError(API_ERROR_TYPES.NOT_FOUND, 'Vehicle API endpoint not found.');
      }
      return createApiError(API_ERROR_TYPES.UNKNOWN, `Unexpected error: ${res.status} ${res.statusText}`);
    }
    const data = await res.json();
    return { vehicles: data.vehicles || [] };
  } catch (error) {
    if (error.name === 'AbortError') {
      return createApiError(API_ERROR_TYPES.TIMEOUT, 'Vehicle service is not responding.');
    }
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      return createApiError(API_ERROR_TYPES.NETWORK, 'Cannot connect to gateway.');
    }
    return createApiError(API_ERROR_TYPES.UNKNOWN, 'An unexpected error occurred.');
  }
}
