/**
 * utils.js — Shared UI helpers used across all pages.
 * Fully integrated with Lucide icons (no emojis) and Auth Guards.
 */

/* ── Lucide Icon Re-initialization ───────────────────────── */
export function renderLucide() {
  if (typeof window !== "undefined" && window.lucide && typeof window.lucide.createIcons === "function") {
    window.lucide.createIcons();
  }
}

/* ── Authentication Guards & User session ───────────────── */
export function requireAuth() {
  const token = localStorage.getItem("stocksense_token");
  if (!token) {
    window.location.href = "login.html";
    return false;
  }
  return true;
}

export function logout() {
  localStorage.removeItem("stocksense_token");
  localStorage.removeItem("stocksense_email");
  window.location.href = "login.html";
}

export function initAuthHeader() {
  const profileArea = document.getElementById("sidebar-profile");
  if (!profileArea) return;

  const email = localStorage.getItem("stocksense_email") || "user";
  profileArea.innerHTML = `
    <a class="sidebar-link" href="profile.html" title="My Profile"><i data-lucide="user-round"></i><span class="nav-label">My Profile</span></a>
    <div class="sidebar-user" title="${email}"><i data-lucide="circle-user-round"></i><span class="nav-label">${email}</span></div>
    <button class="sidebar-link sidebar-logout" id="logout-btn" title="Logout"><i data-lucide="log-out"></i><span class="nav-label">Logout</span></button>
  `;
  document.getElementById("logout-btn")?.addEventListener("click", logout);
  renderLucide();
}

/* ── Toast notification ──────────────────────────────────── */
let toastTimer = null;

export function showToast(message, type = "info") {
  const el = document.getElementById("toast");
  if (!el) return;
  el.textContent = "";

  const iconName = {
    success: "check-circle-2",
    error: "alert-circle",
    warn: "alert-triangle",
    info: "info"
  }[type] ?? "info";

  const iconEl = document.createElement("i");
  iconEl.setAttribute("data-lucide", iconName);
  iconEl.className = "toast-icon";

  const msgSpan = document.createElement("span");
  msgSpan.textContent = message;

  el.append(iconEl, msgSpan);
  el.className = `show ${type}`;

  renderLucide();

  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    el.className = "";
  }, 3500);
}

/* ── Inline alert banner ─────────────────────────────────── */
export function showAlert(el, message, type = "info") {
  if (!el) return;
  el.className = `alert alert-${type}`;

  const iconName = {
    success: "check-circle",
    error: "alert-octagon",
    warn: "alert-triangle",
    info: "info"
  }[type] ?? "info";

  el.innerHTML = `<i data-lucide="${iconName}" class="alert-icon"></i><span>${message}</span>`;
  el.classList.remove("hidden");
  renderLucide();
  el.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

export function hideAlert(el) {
  if (el) el.classList.add("hidden");
}

/* ── Button loading state ────────────────────────────────── */
export function setLoading(btn, loading) {
  btn.disabled = loading;
  btn._originalText ??= btn.innerHTML;
  btn.innerHTML = loading
    ? `<span class="spinner"></span> Loading…`
    : btn._originalText;
  renderLucide();
}

/* ── Populate a <select> with products ───────────────────── */
export function populateProductSelect(selectEl, products, placeholder = "— Select a product —") {
  selectEl.innerHTML = `<option value="">${placeholder}</option>`;
  products.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.name} (SKU: ${p.sku}) • ${p.current_stock} ${p.unit_of_measure}`;
    opt.dataset.stock = p.current_stock;
    opt.dataset.uom   = p.unit_of_measure;
    selectEl.appendChild(opt);
  });
}

/* ── Stock badge helper ──────────────────────────────────── */
export function stockBadge(qty) {
  if (qty === 0) {
    return `<span class="badge badge-red"><i data-lucide="alert-octagon" class="badge-icon"></i> Out of Stock</span>`;
  }
  if (qty < 10) {
    return `<span class="badge badge-orange"><i data-lucide="alert-triangle" class="badge-icon"></i> Low: ${qty}</span>`;
  }
  return `<span class="badge badge-green"><i data-lucide="check" class="badge-icon"></i> In Stock: ${qty}</span>`;
}

/* ── Move Type badge helper ──────────────────────────────── */
export function moveTypeBadge(type) {
  const t = (type || "").toLowerCase();
  switch (t) {
    case "receipt":
      return `<span class="badge badge-green"><i data-lucide="arrow-down-left" class="badge-icon"></i> Receipt</span>`;
    case "delivery":
      return `<span class="badge badge-red"><i data-lucide="arrow-up-right" class="badge-icon"></i> Delivery</span>`;
    case "transfer":
      return `<span class="badge badge-blue"><i data-lucide="arrow-left-right" class="badge-icon"></i> Transfer</span>`;
    case "adjustment":
      return `<span class="badge badge-orange"><i data-lucide="sliders" class="badge-icon"></i> Adjustment</span>`;
    default:
      return `<span class="badge badge-gray">${type}</span>`;
  }
}

/* ── Format ISO Date / Timestamp ─────────────────────────── */
export function formatDateTime(isoString) {
  if (!isoString) return "—";
  try {
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return isoString;
    return d.toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  } catch {
    return isoString;
  }
}

/* ── Mark active nav link ────────────────────────────────── */
export function markActiveNav() {
  const path = window.location.pathname.split("/").pop() || "index.html";
  document.querySelectorAll(".sidebar-nav a").forEach(a => {
    const href = a.getAttribute("href").split("/").pop();
    if (href === path) {
      a.classList.add("active");
      a.closest("details")?.setAttribute("open", "");
    }
  });
}

/* ── Mobile hamburger toggle ─────────────────────────────── */
export function initNavToggle() {
  const navbar = document.querySelector(".navbar");
  if (!navbar) return;
  navbar.innerHTML = `
    <div class="sidebar-top">
      <a class="nav-brand" href="index.html" title="StockSense"><div class="logo-icon"><i data-lucide="package"></i></div><span class="nav-label">Stock<em>Sense</em></span></a>
      <button class="sidebar-toggle" id="sidebar-toggle" aria-label="Collapse sidebar" title="Collapse sidebar"><i data-lucide="panel-left-close"></i></button>
    </div>
    <nav class="sidebar-nav" aria-label="Main navigation">
      <a class="sidebar-link" href="index.html" title="Dashboard"><i data-lucide="layout-dashboard"></i><span class="nav-label">Dashboard</span></a>
      <a class="sidebar-link" href="products.html" title="Products"><i data-lucide="boxes"></i><span class="nav-label">Products</span></a>
      <details class="sidebar-operations" id="operations-menu">
        <summary class="sidebar-link" title="Operations"><i data-lucide="workflow"></i><span class="nav-label">Operations</span><i data-lucide="chevron-down" class="ops-chevron"></i></summary>
        <div class="sidebar-subnav">
          <a class="sidebar-link" href="receipt.html" title="Receipts"><i data-lucide="arrow-down-to-dot"></i><span class="nav-label">Receipts</span></a>
          <a class="sidebar-link" href="delivery.html" title="Deliveries"><i data-lucide="arrow-up-from-dot"></i><span class="nav-label">Deliveries</span></a>
          <a class="sidebar-link" href="transfer.html" title="Transfers"><i data-lucide="arrow-left-right"></i><span class="nav-label">Transfers</span></a>
          <a class="sidebar-link" href="adjustment.html" title="Adjustments"><i data-lucide="sliders"></i><span class="nav-label">Adjustments</span></a>
          <a class="sidebar-link" href="history.html" title="Move History"><i data-lucide="history"></i><span class="nav-label">Move History</span></a>
          <a class="sidebar-link" href="pending.html" title="Pending Approvals"><i data-lucide="clipboard-check"></i><span class="nav-label">Pending Approvals</span></a>
        </div>
      </details>
      <a class="sidebar-link" href="settings.html" title="Settings · Warehouse management"><i data-lucide="settings-2"></i><span class="nav-label">Settings</span></a>
    </nav>
    <div class="sidebar-profile" id="sidebar-profile"></div>`;
  const wasCollapsed = localStorage.getItem("stocksense_sidebar_collapsed") === "true";
  document.body.classList.toggle("sidebar-collapsed", wasCollapsed);
  document.getElementById("sidebar-toggle")?.addEventListener("click", () => {
    const collapsed = document.body.classList.toggle("sidebar-collapsed");
    localStorage.setItem("stocksense_sidebar_collapsed", String(collapsed));
    renderLucide();
  });
  markActiveNav();
  renderLucide();
}

/* ── Basic form validation helpers ──────────────────────── */
export function validateRequired(inputEl, errorEl) {
  const val = inputEl.value.trim();
  if (!val) {
    inputEl.classList.add("error");
    if (errorEl) { errorEl.textContent = "This field is required."; errorEl.classList.add("visible"); }
    return false;
  }
  inputEl.classList.remove("error");
  if (errorEl) errorEl.classList.remove("visible");
  return true;
}

export function validatePositiveInt(inputEl, errorEl) {
  const val = parseInt(inputEl.value, 10);
  if (isNaN(val) || val <= 0) {
    inputEl.classList.add("error");
    if (errorEl) { errorEl.textContent = "Must be a positive whole number."; errorEl.classList.add("visible"); }
    return false;
  }
  inputEl.classList.remove("error");
  if (errorEl) errorEl.classList.remove("visible");
  return true;
}
