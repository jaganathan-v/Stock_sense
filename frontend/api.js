/**
 * api.js — Centralised API client for StockSense frontend.
 * Seamlessly connects to Spring Boot backend (port 8080).
 */

const BASE = (typeof window !== "undefined" && window.location.origin && window.location.origin.startsWith("http"))
  ? (window.location.port === "8080" ? window.location.origin : "http://localhost:8080")
  : "http://localhost:8080";

const headers = () => ({ "Content-Type": "application/json" });

async function request(method, path, body = null) {
  const opts = { method, headers: headers() };
  if (body !== null) opts.body = JSON.stringify(body);
  const res = await fetch(`${BASE}${path}`, opts);
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    // surface the backend's detail message when available
    const msg = data?.detail ?? data?.message ?? `HTTP ${res.status}: ${res.statusText}`;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  // Products
  getProducts: ()          => request("GET",  "/products"),
  createProduct: (body)    => request("POST", "/products", body),

  // Moves
  receipt:  (body) => request("POST", "/moves/receipt",  body),
  delivery: (body) => request("POST", "/moves/delivery", body),

  // Dashboard
  getDashboard: () => request("GET", "/dashboard"),
};
