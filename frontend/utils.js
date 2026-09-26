/**
 * utils.js — Shared UI helpers used across all pages.
 * Fully integrated with Lucide icons (no emojis) and Auth Guards.
 */

/* ── Lucide Icon Re-initialization ───────────────────────── */
export function renderLucide() {
  if (typeof document === "undefined") return;
  // Render icons locally so a blocked third-party CDN cannot leave empty boxes.
  const paths = {
    package: '<path d="m16.5 9.4-9-5.19M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5M12 22V12"/>',
    boxes: '<path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z"/><path d="m12 12 8-4.5M12 12v9m0-9L4 7.5M8 5.25l8 4.5"/>',
    'alert-triangle': '<path d="m10.3 3.9-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.7-3.1l-8-14a2 2 0 0 0-3.4 0Z"/><path d="M12 9v4m0 4h.01"/>',
    'alert-octagon': '<path d="M7.9 2h8.2L22 7.9v8.2L16.1 22H7.9L2 16.1V7.9L7.9 2Z"/><path d="M12 8v5m0 4h.01"/>',
    'alert-circle': '<circle cx="12" cy="12" r="10"/><path d="M12 8v4m0 4h.01"/>',
    'arrow-down-left': '<path d="M17 7 7 17M17 17H7V7"/>',
    'arrow-up-right': '<path d="M7 17 17 7M7 7h10v10"/>',
    'arrow-down-to-dot': '<path d="M12 2v14m-5-5 5 5 5-5"/><circle cx="12" cy="21" r="1"/>',
    'arrow-up-from-dot': '<circle cx="12" cy="3" r="1"/><path d="M12 7v15m5-5-5-5-5 5"/>',
    'arrow-left-right': '<path d="M17 8 21 4l-4-4M3 4h18M7 16l-4 4 4 4m14-4H3"/>',
    'layout-dashboard': '<rect x="3" y="3" width="8" height="8" rx="1"/><rect x="13" y="3" width="8" height="5" rx="1"/><rect x="13" y="10" width="8" height="11" rx="1"/><rect x="3" y="13" width="8" height="8" rx="1"/>',
    'bar-chart-2': '<path d="M3 3v18h18M18 17V9m-5 8V5M8 17v-3"/>',
    'bar-chart-3': '<path d="M3 3v18h18M18 17V9m-5 8V5M8 17v-3"/>',
    'check': '<path d="m5 12 4 4L19 6"/>',
    'check-circle': '<circle cx="12" cy="12" r="10"/><path d="m8 12 3 3 5-6"/>',
    'check-circle-2': '<path d="M21 11.1V12a9 9 0 1 1-5.3-8.2"/><path d="m9 11 3 3L22 4"/>',
    'clipboard-check': '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 4.5V3h6v1.5M9 13l2 2 4-4"/>',
    'clock': '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
    'database': '<ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14c0 1.7 4 3 9 3s9-1.3 9-3V5M3 12c0 1.7 4 3 9 3s9-1.3 9-3"/>',
    'file-down': '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8Z"/><path d="M14 2v6h6m-8 3v7m-3-3 3 3 3-3"/>',
    'file-text': '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8Z"/><path d="M14 2v6h6M8 13h8m-8 4h8"/>',
    'history': '<path d="M3 12a9 9 0 1 0 2.6-6.4L3 8"/><path d="M3 3v5h5m4-1v5l3 2"/>',
    'inbox': '<path d="M4 4h16l2 11h-6l-2 3h-4l-2-3H2L4 4Z"/><path d="M2 15h6l2 3h4l2-3h6"/>',
    'info': '<circle cx="12" cy="12" r="10"/><path d="M12 11v5m0-9h.01"/>',
    'layers': '<path d="m12 2 10 6-10 6L2 8l10-6Z"/><path d="m2 12 10 6 10-6m-20 5 10 6 10-6"/>',
    'activity': '<path d="M22 12h-4l-3 9L9 3l-3 9H2"/>',
    'bell-ring': '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9m-8 12a2 2 0 0 0 4 0M2 2l2 2m18-2-2 2"/>',
    'workflow': '<rect x="3" y="3" width="6" height="6" rx="1"/><rect x="15" y="15" width="6" height="6" rx="1"/><path d="M9 6h4a4 4 0 0 1 4 4v5M15 18H9a4 4 0 0 1-4-4v-5"/>',
    'user-round': '<circle cx="12" cy="8" r="4"/><path d="M5 21a7 7 0 0 1 14 0"/>',
    'circle-user-round': '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="9" r="3"/><path d="M6.5 19a6 6 0 0 1 11 0"/>',
    'user-plus': '<path d="M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2m6-10a4 4 0 1 0 0-8 4 4 0 0 0 0 8m10-7v6m-3-3h6"/>',
    'contact-round': '<circle cx="12" cy="8" r="3"/><path d="M5 20a7 7 0 0 1 14 0M3 4h2m-2 5h2m-2 5h2"/>',
    'archive': '<path d="M3 4h18v5H3zM5 9v11h14V9m-9 4h4"/>',
    'list': '<path d="M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01"/>',
    'log-in': '<path d="M10 17l5-5-5-5m5 5H3m9-9h7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-7"/>',
    'pencil': '<path d="m16 4 4 4L9 19l-5 1 1-5L16 4Z"/><path d="m14 6 4 4"/>',
    'shield-check': '<path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z"/><path d="m9 12 2 2 4-4"/>',
    'shield-alert': '<path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z"/><path d="M12 8v4m0 4h.01"/>',
    'scale': '<path d="m16 16 3-8 3 8a5 5 0 0 1-6 0ZM2 16l3-8 3 8a5 5 0 0 1-6 0Zm8-12h4m-2 0v16m-7 0h14"/>',
    'map-pin-plus': '<path d="M18 8a6 6 0 0 0-12 0c0 4 6 9 6 9s6-5 6-9Z"/><circle cx="12" cy="8" r="2"/><path d="M19 19h5m-2.5-2.5v5"/>',
    'loader-2': '<path d="M21 12a9 9 0 1 1-6.2-8.6"/>',
    'menu': '<path d="M4 6h16M4 12h16M4 18h16"/>',
    'map-pin': '<path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/>',
    'map-pinned': '<path d="M18 8a6 6 0 0 0-12 0c0 4 6 9 6 9s6-5 6-9Z"/><circle cx="12" cy="8" r="2"/><path d="m3 16-1 5 7-2m6 0 7 2-1-5"/>',
    'plus': '<path d="M12 5v14m-7-7h14"/>',
    'plus-circle': '<circle cx="12" cy="12" r="10"/><path d="M12 8v8m-4-4h8"/>',
    'refresh-cw': '<path d="M20 7v5h-5M4 17v-5h5"/><path d="M5.6 9A7 7 0 0 1 18 6l2 6M4 12l2 6a7 7 0 0 0 12.4-3"/>',
    'save': '<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2Z"/><path d="M17 21v-8H7v8M7 3v5h8"/>',
    'search': '<circle cx="11" cy="11" r="7"/><path d="m20 20-4-4"/>',
    'settings-2': '<path d="M20 7h-9m-4 0H4m16 10h-9m-4 0H4"/><circle cx="14" cy="7" r="3"/><circle cx="10" cy="17" r="3"/>',
    'sliders': '<path d="M4 21v-7m0-4V3m8 18v-9m0-4V3m8 18v-5m0-4V3M2 14h4m4-6h4m4 8h4"/>',
    'trash-2': '<path d="M3 6h18m-2 0-1 14H6L5 6m4 0V4h6v2m-5 4v6m4-6v6"/>',
    'truck': '<path d="M3 6h12v12H3zM15 10h4l3 3v5h-7z"/><circle cx="7.5" cy="18.5" r="2"/><circle cx="18.5" cy="18.5" r="2"/>',
    'warehouse': '<path d="m3 10 9-7 9 7v11H3V10Z"/><path d="M9 21v-7h6v7M7 10h2m6 0h2"/>',
    'zap': '<path d="m13 2-3 9h7L9 22l2-9H4l9-11Z"/>',
    'chevron-down': '<path d="m6 9 6 6 6-6"/>',
    'panel-left-close': '<rect x="3" y="3" width="18" height="18" rx="2"/><path d="M9 3v18m7-12-3 3 3 3"/>',
  };
  document.querySelectorAll("[data-lucide]").forEach((placeholder) => {
    const name = placeholder.getAttribute("data-lucide");
    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.setAttribute("width", "24");
    svg.setAttribute("height", "24");
    svg.setAttribute("fill", "none");
    svg.setAttribute("stroke", "currentColor");
    svg.setAttribute("stroke-width", "2");
    svg.setAttribute("stroke-linecap", "round");
    svg.setAttribute("stroke-linejoin", "round");
    svg.setAttribute("aria-hidden", "true");
    svg.setAttribute("focusable", "false");
    svg.setAttribute("class", `lucide lucide-${name} ${placeholder.getAttribute("class") || ""}`.trim());
    if (placeholder.getAttribute("style")) svg.setAttribute("style", placeholder.getAttribute("style"));
    svg.innerHTML = paths[name] || '<circle cx="12" cy="12" r="9"/><path d="M8 12h8m-4-4v8"/>';
    placeholder.replaceWith(svg);
  });
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
