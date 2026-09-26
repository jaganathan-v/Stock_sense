/**
 * utils.js — Shared UI helpers used across all pages.
 */

/* ── Toast notification ──────────────────────────────────── */
let toastTimer = null;

export function showToast(message, type = "info") {
  const el = document.getElementById("toast");
  if (!el) return;
  el.textContent = "";

  const icon = { success: "✅", error: "❌", info: "ℹ️" }[type] ?? "ℹ️";
  const iconSpan = Object.assign(document.createElement("span"), { textContent: icon });
  const msgSpan  = Object.assign(document.createElement("span"), { textContent: message });

  el.append(iconSpan, msgSpan);
  el.className = `show ${type}`;

  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    el.className = "";
  }, 3500);
}

/* ── Inline alert banner ─────────────────────────────────── */
export function showAlert(el, message, type = "info") {
  if (!el) return;
  el.className = `alert alert-${type}`;
  el.innerHTML = `<span>${iconFor(type)}</span><span>${message}</span>`;
  el.classList.remove("hidden");
  el.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

export function hideAlert(el) {
  if (el) el.classList.add("hidden");
}

function iconFor(type) {
  return { success: "✅", error: "🚫", warn: "⚠️", info: "ℹ️" }[type] ?? "";
}

/* ── Button loading state ────────────────────────────────── */
export function setLoading(btn, loading) {
  btn.disabled = loading;
  btn._originalText ??= btn.innerHTML;
  btn.innerHTML = loading
    ? `<span class="spinner"></span> ${btn._originalText.replace(/<[^>]+>/g, "")}`
    : btn._originalText;
}

/* ── Populate a <select> with products ───────────────────── */
export function populateProductSelect(selectEl, products, placeholder = "— Select a product —") {
  selectEl.innerHTML = `<option value="">  ${placeholder}</option>`;
  products.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.name}  (SKU: ${p.sku})  •  ${p.current_stock} ${p.unit_of_measure}`;
    opt.dataset.stock = p.current_stock;
    opt.dataset.uom   = p.unit_of_measure;
    selectEl.appendChild(opt);
  });
}

/* ── Stock badge helper ──────────────────────────────────── */
export function stockBadge(qty) {
  if (qty === 0)  return `<span class="badge badge-red">🚨 Out of Stock</span>`;
  if (qty < 10)   return `<span class="badge badge-orange">⚠️ Low: ${qty}</span>`;
  return              `<span class="badge badge-green">✅ ${qty}</span>`;
}

/* ── Mark active nav link ────────────────────────────────── */
export function markActiveNav() {
  const path = window.location.pathname.split("/").pop() || "index.html";
  document.querySelectorAll(".nav-links a").forEach(a => {
    const href = a.getAttribute("href").split("/").pop();
    if (href === path) a.classList.add("active");
  });
}

/* ── Mobile hamburger toggle ─────────────────────────────── */
export function initNavToggle() {
  const btn   = document.getElementById("nav-toggle");
  const links = document.getElementById("nav-links");
  if (btn && links) {
    btn.addEventListener("click", () => links.classList.toggle("open"));
  }
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
