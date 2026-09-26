/**
 * api.js — Centralised API client for StockSense frontend.
 * Seamlessly connects to Spring Boot backend (port 8080) with JWT Auth.
 */

const BASE = (typeof window !== "undefined" && window.location.origin && window.location.origin.startsWith("http"))
  ? (window.location.port === "8080" ? window.location.origin : "http://localhost:8080")
  : "http://localhost:8080";

function getHeaders() {
  const h = { "Content-Type": "application/json" };
  const token = localStorage.getItem("stocksense_token");
  if (token) {
    h["Authorization"] = `Bearer ${token}`;
  }
  return h;
}

async function request(method, path, body = null) {
  const opts = { method, headers: getHeaders() };
  if (body !== null) opts.body = JSON.stringify(body);
  const res = await fetch(`${BASE}${path}`, opts);
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    // If unauthorized, clear invalid token and redirect if not already on auth page
    if (res.status === 401 && !path.startsWith("/auth/login") && !path.startsWith("/auth/signup")) {
      localStorage.removeItem("stocksense_token");
      localStorage.removeItem("stocksense_email");
      if (!window.location.pathname.endsWith("login.html") && !window.location.pathname.endsWith("signup.html")) {
        window.location.href = "login.html";
      }
    }
    const msg = data?.detail ?? data?.message ?? `HTTP ${res.status}: ${res.statusText}`;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  // Auth
  signup: (body)   => request("POST", "/auth/signup", body),
  login:  (body)   => request("POST", "/auth/login",  body),
  getMe:  ()       => request("GET",  "/auth/me"),

  // Products
  getProducts: ()          => request("GET",    "/products"),
  createProduct: (body)    => request("POST",   "/products", body),
  deleteProduct: (id)      => request("DELETE", `/products/${id}`),

  // Locations
  getLocations: ()         => request("GET",    "/locations"),

  // Moves
  receipt:  (body) => request("POST", "/moves/receipt",  body),
  delivery: (body) => request("POST", "/moves/delivery", body),
  transfer: (body) => request("POST", "/moves/transfer", body),
  adjustment: (body) => request("POST", "/moves/adjustment", body),

  // Recent & All Moves
  getRecentMoves: (params = {}) => {
    const q = new URLSearchParams();
    if (params.limit) q.set("limit", params.limit);
    if (params.type) q.set("type", params.type);
    const qs = q.toString() ? `?${q.toString()}` : "";
    return request("GET", `/moves/recent${qs}`);
  },
  getAllMoves: () => request("GET", "/moves/all"),

  // Dashboard
  getDashboard: () => request("GET", "/dashboard"),
};
