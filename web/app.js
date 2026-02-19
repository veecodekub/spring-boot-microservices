/* ==============================================
   Microservices Dashboard — SPA Application Logic
   ============================================== */

const API_BASE = "http://localhost:8080";

// ── State ───────────────────────────────────────
let currentPage = "dashboard";
let authMode = "login"; // 'login' | 'register'
let profileExists = false;
let currentOrderId = null;
let productsPage = 0;
const productsSize = 9;
let allProductsCache = []; // cache for order product dropdown
let ordersPage = 0;
const ordersSize = 10;

// ── Token Management ────────────────────────────
function getToken() {
  return localStorage.getItem("token");
}

function setToken(token) {
  localStorage.setItem("token", token);
}

function removeToken() {
  localStorage.removeItem("token");
}

function getUsername() {
  const token = getToken();
  if (!token) return "";
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.sub || "";
  } catch {
    return "";
  }
}

function isLoggedIn() {
  return !!getToken();
}

function getUserRoles() {
  const token = getToken();
  if (!token) return [];
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const scope = payload.scope || "";
    return scope.split(" ").filter((s) => s.startsWith("ROLE_"));
  } catch {
    return [];
  }
}

function hasRole(role) {
  return getUserRoles().includes(role);
}

function isAdmin() {
  return hasRole("ROLE_ADMIN");
}

function applyRoleVisibility() {
  const roles = getUserRoles();
  document.querySelectorAll("[data-role]").forEach((el) => {
    const requiredRole = el.getAttribute("data-role");
    if (roles.includes(requiredRole)) {
      el.style.display = "";
    } else {
      el.style.display = "none";
    }
  });
}

// ── API Helper ──────────────────────────────────
async function api(path, options = {}, base = API_BASE) {
  const url = `${base}${path}`;
  const headers = { "Content-Type": "application/json", ...options.headers };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  try {
    const res = await fetch(url, { ...options, headers });
    if (res.status === 401 || res.status === 403) {
      toast("Session expired. Please login again.", "error");
      logout();
      return null;
    }
    const text = await res.text();
    let data;
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
    if (!res.ok) {
      // Parse structured API error response
      const errObj = typeof data === "object" && data !== null ? data : {};
      const msg =
        errObj.message || errObj.error || `Request failed (${res.status})`;
      // Show toast with message + details directly
      toast(msg, "error", errObj.details);
      return null;
    }
    return data;
  } catch (err) {
    if (err.message === "Failed to fetch") {
      toast("Network error — is the API gateway running?", "error");
      return null;
    }
    toast(err.message, "error");
    return null;
  }
}

// ── Toast Notifications ─────────────────────────
function toast(message, type = "info", details = null) {
  const container = document.getElementById("toast-container");
  const el = document.createElement("div");
  el.className = `toast ${type === "error" ? "toast-error" : type === "success" ? "toast-success" : ""}`;

  // Title line
  const title = document.createElement("div");
  title.style.fontWeight = "700";
  title.textContent = message;
  el.appendChild(title);

  // Detail fields from API error (e.g. { password: "must be 8-16 chars" })
  if (details && typeof details === "object") {
    const detailsDiv = document.createElement("div");
    detailsDiv.style.marginTop = "6px";
    Object.entries(details).forEach(([field, msg]) => {
      const line = document.createElement("div");
      line.style.fontSize = "0.8rem";
      line.style.opacity = "0.9";
      line.style.marginTop = "2px";
      line.textContent = `• ${field}: ${msg}`;
      detailsDiv.appendChild(line);
    });
    el.appendChild(detailsDiv);
  }

  container.appendChild(el);
  const duration = details ? 5000 : 3500;
  setTimeout(() => el.remove(), duration);
}

// ── Theme Toggle ────────────────────────────────
function toggleTheme() {
  const html = document.documentElement;
  const current = html.getAttribute("data-theme");
  const next = current === "dark" ? "light" : "dark";
  html.setAttribute("data-theme", next);
  localStorage.setItem("theme", next);
  document.getElementById("theme-toggle-btn").textContent =
    next === "dark" ? "☀️" : "🌙";
}

function initTheme() {
  const saved = localStorage.getItem("theme") || "light";
  document.documentElement.setAttribute("data-theme", saved);
  document.getElementById("theme-toggle-btn").textContent =
    saved === "dark" ? "☀️" : "🌙";
}

// ── Modals ──────────────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add("active");
}

function closeModal(id) {
  document.getElementById(id).classList.remove("active");
}

// ── Navigation / Router ─────────────────────────
function navigateTo(page) {
  window.location.hash = page;
}

function handleRoute() {
  const hash = window.location.hash.replace("#", "") || "dashboard";
  if (!isLoggedIn()) {
    showAuth();
    return;
  }
  showApp();
  setActivePage(hash);
}

function setActivePage(page) {
  currentPage = page;
  // Hide all pages
  document
    .querySelectorAll(".page")
    .forEach((p) => p.classList.remove("active"));
  // Show target
  const target = document.getElementById(`page-${page}`);
  if (target) {
    target.classList.add("active");
  } else {
    document.getElementById("page-dashboard").classList.add("active");
    currentPage = "dashboard";
  }
  // Update nav links
  document.querySelectorAll(".navbar-nav a").forEach((a) => {
    a.classList.toggle("active", a.getAttribute("data-page") === currentPage);
  });
  // Load page data
  onPageLoad(currentPage);
}

function onPageLoad(page) {
  switch (page) {
    case "dashboard":
      loadDashboard();
      break;
    case "profile":
      loadProfile();
      break;
    case "products":
      loadProducts();
      break;
    case "orders":
      loadMyOrders();
      break; // also loaded on demand
  }
}

function showAuth() {
  document.getElementById("auth-page").classList.add("active");
  document.getElementById("main-app").classList.add("hidden");
}

function showApp() {
  document.getElementById("auth-page").classList.remove("active");
  document.getElementById("main-app").classList.remove("hidden");
  document.getElementById("dashboard-username").textContent = getUsername();

  // Display role badge in navbar
  const roles = getUserRoles();
  const roleLabel =
    roles.map((r) => r.replace("ROLE_", "")).join(", ") || "USER";
  const navbarRoleBadge = document.getElementById("navbar-role-badge");
  if (navbarRoleBadge) {
    navbarRoleBadge.textContent = roleLabel;
    navbarRoleBadge.className =
      "role-badge" + (isAdmin() ? " role-admin" : " role-user");
  }

  applyRoleVisibility();
}

// ── Auth ────────────────────────────────────────
function toggleAuthMode() {
  authMode = authMode === "login" ? "register" : "login";
  const isLogin = authMode === "login";
  document.getElementById("auth-title").textContent = isLogin
    ? "Sign In"
    : "Sign Up";
  document.getElementById("auth-subtitle").textContent = isLogin
    ? "Welcome back to your dashboard"
    : "Create a new account";
  document.getElementById("auth-submit").textContent = isLogin
    ? "Sign In"
    : "Sign Up";
  document.getElementById("auth-toggle-text").textContent = isLogin
    ? "Don't have an account?"
    : "Already have an account?";
  document.getElementById("auth-toggle-link").textContent = isLogin
    ? "Sign Up"
    : "Sign In";
  document.getElementById("auth-role-group").style.display = isLogin
    ? "none"
    : "";
}

document.getElementById("auth-form").addEventListener("submit", async () => {
  const username = document.getElementById("auth-username").value.trim();
  const password = document.getElementById("auth-password").value.trim();
  if (!username || !password) {
    toast("Please fill in all fields", "error");
    return;
  }

  const btn = document.getElementById("auth-submit");
  btn.disabled = true;
  btn.textContent = "Please wait...";

  try {
    if (authMode === "register") {
      const role = document.getElementById("auth-role").value;
      const regResult = await api("/auth/register", {
        method: "POST",
        body: JSON.stringify({ role, username, password }),
      });
      if (regResult === null) return; // error already shown by api()
      toast("Account created! Logging in...", "success");
    }
    // Login
    const data = await api("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });
    if (data === null) return; // error already shown by api()
    if (data.token) {
      setToken(data.token);
      toast(`Welcome, ${username}!`, "success");
      handleRoute();
    } else {
      // Some auth services return token differently
      const token =
        typeof data === "string" ? data : data.accessToken || data.jwt;
      if (token) {
        setToken(token);
        toast(`Welcome, ${username}!`, "success");
        handleRoute();
      } else {
        toast("Login failed — unexpected response", "error");
      }
    }
  } catch (err) {
    toast(err.message, "error");
  } finally {
    btn.disabled = false;
    btn.textContent = authMode === "login" ? "Sign In" : "Sign Up";
  }
});

function logout() {
  removeToken();
  currentOrderId = null;
  window.location.hash = "";
  showAuth();
}

// ── Dashboard ───────────────────────────────────
async function loadDashboard() {
  document.getElementById("dashboard-username").textContent = getUsername();
  // Products count (paginated response)
  try {
    const res = await api("/products?page=0&size=1");
    if (res && res.page) {
      document.getElementById("stat-products").textContent =
        res.page.totalElements;
    } else {
      document.getElementById("stat-products").textContent = "—";
    }
  } catch {
    document.getElementById("stat-products").textContent = "—";
  }

  document.getElementById("stat-orders").textContent = "—";
  // Orders count
  try {
    const ores = await api("/orders?page=0&size=1");
    if (ores) {
      const total = ores.page?.totalElements ?? ores.totalElements ?? null;
      if (total !== null) {
        document.getElementById("stat-orders").textContent = total;
      }
    }
  } catch {}
}

// ── Profile ─────────────────────────────────────
async function loadProfile() {
  const loading = document.getElementById("profile-loading");
  const form = document.getElementById("profile-form");
  loading.classList.remove("hidden");
  form.classList.add("hidden");

  const data = await api("/users/profile");
  document.getElementById("profile-name").value = data.name || "";
  document.getElementById("profile-address").value = data.address || "";
  document.getElementById("profile-phone").value = data.phone || "";
  document.getElementById("profile-save-btn").textContent = "Update Profile";

  loading.classList.add("hidden");
  form.classList.remove("hidden");
}

document.getElementById("profile-form").addEventListener("submit", async () => {
  const name = document.getElementById("profile-name").value.trim();
  const address = document.getElementById("profile-address").value.trim();
  const phone = document.getElementById("profile-phone").value.trim();
  if (!name || !address || !phone) {
    toast("Please fill all fields", "error");
    return;
  }

  try {
    await api("/users/profile", {
      method: "PUT",
      body: JSON.stringify({ name, address, phone }),
    });
    toast("Profile updated!", "success");
  } catch (err) {
    toast(err.message, "error");
  }
});

// ── Products ────────────────────────────────────
async function loadProducts(page = productsPage) {
  productsPage = page;
  const loading = document.getElementById("products-loading");
  const grid = document.getElementById("products-grid");
  const empty = document.getElementById("products-empty");
  const pagination = document.getElementById("products-pagination");
  loading.classList.remove("hidden");
  grid.classList.add("hidden");
  empty.classList.add("hidden");
  pagination.classList.add("hidden");

  try {
    const res = await api(`/products?page=${page}&size=${productsSize}`);
    if (!res) {
      empty.classList.remove("hidden");
      loading.classList.add("hidden");
      return;
    }

    const products = res.content || [];
    const pageInfo = res.page || {};

    if (products.length > 0) {
      grid.innerHTML = products
        .map(
          (p, i) => `
        <div class="product-card" style="animation-delay:${i * 0.05}s" onclick="viewProduct(${p.id})">
          <div class="product-name">${escapeHtml(p.name)}</div>
          <div class="product-desc">${escapeHtml(p.description || "")}</div>
          <div class="product-meta">
            <span class="product-price">${formatPrice(p.price)}</span>
            <span class="product-qty">Qty: ${p.quantity ?? "—"}</span>
          </div>
        </div>
      `,
        )
        .join("");
      grid.classList.remove("hidden");

      // Render pagination
      const totalPages = pageInfo.totalPages || 1;
      if (totalPages > 1) {
        renderPagination(pagination, page, totalPages);
        pagination.classList.remove("hidden");
      }
    } else {
      empty.classList.remove("hidden");
    }
  } catch (err) {
    toast(err.message, "error");
    empty.classList.remove("hidden");
  }

  loading.classList.add("hidden");
}

function renderPagination(
  container,
  current,
  totalPages,
  fnName = "loadProducts",
) {
  let html = "";
  // Previous btn
  html += `<button class="btn btn-outline btn-sm" ${current === 0 ? "disabled" : ""} onclick="${fnName}(${current - 1})">‹ Prev</button>`;
  // Page numbers
  for (let i = 0; i < totalPages; i++) {
    if (
      totalPages > 7 &&
      i > 1 &&
      i < totalPages - 2 &&
      Math.abs(i - current) > 1
    ) {
      if (html.slice(-3) !== "...")
        html += '<span class="pagination-dots">…</span>';
      continue;
    }
    html += `<button class="btn btn-sm ${i === current ? "btn-primary" : "btn-outline"}" onclick="${fnName}(${i})">${i + 1}</button>`;
  }
  // Next btn
  html += `<button class="btn btn-outline btn-sm" ${current >= totalPages - 1 ? "disabled" : ""} onclick="${fnName}(${current + 1})">Next ›</button>`;
  container.innerHTML = html;
}

async function createProduct() {
  const name = document.getElementById("product-name").value.trim();
  const description = document.getElementById("product-desc").value.trim();
  const price = parseFloat(document.getElementById("product-price").value);
  const quantity = parseInt(document.getElementById("product-qty").value, 10);

  if (!name || isNaN(price) || isNaN(quantity)) {
    toast("Please fill all required fields", "error");
    return;
  }

  try {
    await api("/products", {
      method: "POST",
      body: JSON.stringify({ name, description, price, quantity }),
    });
    toast("Product created!", "success");
    closeModal("product-modal");
    // Reset form
    document.getElementById("product-name").value = "";
    document.getElementById("product-desc").value = "";
    document.getElementById("product-price").value = "";
    document.getElementById("product-qty").value = "";
    loadProducts();
  } catch (err) {
    toast(err.message, "error");
  }
}

async function viewProduct(id) {
  try {
    const p = await api(`/products/${id}`);
    if (p) {
      toast(
        `${p.name} — ${formatPrice(p.price)} (Qty: ${p.quantity ?? "—"})`,
        "info",
      );
    }
  } catch (err) {
    toast(err.message, "error");
  }
}

// ── Orders ──────────────────────────────────────

// Fetch all products for the order dropdown (loads all pages)
async function fetchAllProducts() {
  let all = [];
  let page = 0;
  while (true) {
    const res = await api(`/products?page=${page}&size=100`);
    if (!res || !res.content) break;
    all = all.concat(res.content);
    if (!res.page || page >= res.page.totalPages - 1) break;
    page++;
  }
  allProductsCache = all;
  return all;
}

function buildProductOptions() {
  return (
    '<option value="">-- Select Product --</option>' +
    allProductsCache
      .map(
        (p) =>
          `<option value="${p.id}" data-name="${escapeHtml(p.name)}" data-price="${p.price}">${escapeHtml(p.name)} — ${formatPrice(p.price)}</option>`,
      )
      .join("")
  );
}

function onProductSelect(selectEl) {
  const row = selectEl.closest(".order-item-row");
  const opt = selectEl.selectedOptions[0];
  const priceInput = row.querySelector(".order-product-price");
  if (opt && opt.value) {
    priceInput.value = opt.dataset.price || "";
  } else {
    priceInput.value = "";
  }
}

function addOrderItemRow() {
  const container = document.getElementById("order-items-container");
  const index = container.children.length;
  const row = document.createElement("div");
  row.className = "order-item-row";
  row.innerHTML = `
    <div class="form-group" style="flex:2">
      ${index === 0 ? "<label>Product</label>" : ""}
      <select class="form-control order-product-select" onchange="onProductSelect(this)" required>
        ${buildProductOptions()}
      </select>
    </div>
    <div class="form-group">
      ${index === 0 ? "<label>Price</label>" : ""}
      <input type="number" class="form-control order-product-price" placeholder="0.00" step="0.01" min="0" readonly required />
    </div>
    <div class="form-group">
      ${index === 0 ? "<label>Qty</label>" : ""}
      <input type="number" class="form-control order-product-qty" placeholder="1" min="1" value="1" required />
    </div>
    <button class="remove-item-btn" onclick="this.parentElement.remove()" ${index === 0 ? 'style="margin-top:22px"' : ""}>✕</button>
  `;
  container.appendChild(row);
}

async function openOrderModal() {
  // Load products for the dropdown
  if (allProductsCache.length === 0) {
    toast("Loading products...", "info");
    await fetchAllProducts();
  }
  document.getElementById("order-items-container").innerHTML = "";
  addOrderItemRow();
  openModal("order-modal");
}

// ── My Orders List ──────────────────────────────
async function loadMyOrders(page = ordersPage) {
  ordersPage = page;
  const loading = document.getElementById("my-orders-loading");
  const table = document.getElementById("my-orders-table");
  const empty = document.getElementById("my-orders-empty");
  const pagination = document.getElementById("my-orders-pagination");
  loading.classList.remove("hidden");
  table.classList.add("hidden");
  empty.classList.add("hidden");
  pagination.classList.add("hidden");

  try {
    const res = await api(`/orders?page=${page}&size=${ordersSize}`);
    if (!res) {
      empty.classList.remove("hidden");
      loading.classList.add("hidden");
      return;
    }

    const orders = res.content || [];
    const pageInfo = res.page || {};

    if (orders.length > 0) {
      const tbody = document.getElementById("my-orders-body");
      tbody.innerHTML = orders
        .map((o) => {
          const status = o.status || o.orderStatus || "UNKNOWN";
          let badgeClass = "badge";
          if (status === "PAID" || status === "COMPLETED")
            badgeClass += " badge-success";
          else if (status === "PENDING") badgeClass += " badge-warning";
          else if (status === "CANCELLED" || status === "REFUNDED")
            badgeClass += " badge-danger";
          const itemCount = (o.items || o.orderItems || []).length;
          return `
          <tr>
            <td class="text-sm" style="font-family:monospace">${(o.id || o.orderId || "—").toString().slice(0, 8)}…</td>
            <td>${itemCount} item${itemCount !== 1 ? "s" : ""}</td>
            <td>${formatPrice(o.total || 0)}</td>
            <td><span class="${badgeClass}">${status}</span></td>
            <td class="text-sm">${o.paymentMethod || "—"}</td>
            <td><button class="btn btn-outline btn-sm" onclick="viewOrderFromList('${o.id || o.orderId}')">View</button></td>
          </tr>
        `;
        })
        .join("");
      table.classList.remove("hidden");

      const totalPages = pageInfo.totalPages || 1;
      if (totalPages > 1) {
        renderPagination(pagination, page, totalPages, "loadMyOrders");
        pagination.classList.remove("hidden");
      }
    } else {
      empty.classList.remove("hidden");
    }
  } catch (err) {
    toast(err.message, "error");
    empty.classList.remove("hidden");
  }

  loading.classList.add("hidden");
}

function viewOrderFromList(orderId) {
  document.getElementById("order-lookup-id").value = orderId;
  lookupOrder();
}

async function createOrder() {
  const rows = document.querySelectorAll(".order-item-row");
  if (rows.length === 0) {
    toast("Add at least one item", "error");
    return;
  }

  const items = [];
  for (const row of rows) {
    const select = row.querySelector(".order-product-select");
    const productId = parseInt(select.value, 10);
    const opt = select.selectedOptions[0];
    const productName = opt ? opt.dataset.name : "";
    const productPrice = parseFloat(
      row.querySelector(".order-product-price").value,
    );
    const quantity = parseInt(
      row.querySelector(".order-product-qty").value,
      10,
    );
    if (!productId || isNaN(productPrice) || isNaN(quantity)) {
      toast("Please select a product and fill quantity", "error");
      return;
    }
    items.push({ productId, productName, productPrice, quantity });
  }

  const paymentMethod = document.getElementById("order-payment-method").value;
  const paymentCurrency = document.getElementById("order-currency").value;

  try {
    const order = await api("/orders", {
      method: "POST",
      body: JSON.stringify({ items, paymentMethod, paymentCurrency }),
    });
    toast("Order created!", "success");
    closeModal("order-modal");
    document.getElementById("order-items-container").innerHTML = "";
    loadMyOrders(0);
    if (order && order.orderId) {
      document.getElementById("order-lookup-id").value = order.orderId;
      lookupOrder();
    }
  } catch (err) {
    toast(err.message, "error");
  }
}

async function lookupOrder() {
  const orderId = document.getElementById("order-lookup-id").value.trim();
  if (!orderId) {
    toast("Enter an order ID", "error");
    return;
  }

  try {
    const order = await api(`/orders/customer/${orderId}`);
    if (!order) return;
    currentOrderId = orderId;
    displayOrderDetail(order);
  } catch (err) {
    toast(err.message, "error");
    document.getElementById("order-detail").classList.add("hidden");
  }
}

function displayOrderDetail(order) {
  const detail = document.getElementById("order-detail");
  detail.classList.remove("hidden");

  document.getElementById("order-detail-id").textContent =
    order.orderId || order.id || "—";

  const statusBadge = document.getElementById("order-status-badge");
  const status = order.status || order.orderStatus || "UNKNOWN";
  statusBadge.textContent = status;
  statusBadge.className = "badge";
  if (status === "PAID" || status === "COMPLETED")
    statusBadge.classList.add("badge-success");
  else if (status === "PENDING") statusBadge.classList.add("badge-warning");
  else if (status === "CANCELLED" || status === "REFUNDED")
    statusBadge.classList.add("badge-danger");

  const items = order.items || order.orderItems || [];
  const tbody = document.getElementById("order-detail-items");
  tbody.innerHTML = items
    .map(
      (item) => `
    <tr>
      <td>${escapeHtml(item.productName || "Product")}</td>
      <td>${formatPrice(item.productPrice || item.price || 0)}</td>
      <td>${item.quantity}</td>
      <td>${formatPrice((item.productPrice || item.price || 0) * item.quantity)}</td>
    </tr>
  `,
    )
    .join("");

  const total = items.reduce(
    (sum, item) => sum + (item.productPrice || item.price || 0) * item.quantity,
    0,
  );
  document.getElementById("order-detail-total").textContent =
    formatPrice(total);
  document.getElementById("order-detail-payment").textContent =
    `${order.paymentMethod || "—"} / ${order.paymentCurrency || "—"}`;
}

async function payOrder() {
  if (!currentOrderId) return;
  try {
    await api(`/payments/order/${currentOrderId}/pay`, { method: "POST" });
    toast("Payment submitted!", "success");
    lookupOrder();
  } catch (err) {
    toast(err.message, "error");
  }
}

async function cancelOrder() {
  if (!currentOrderId) return;
  try {
    await api(`/payments/order/${currentOrderId}/cancel`, { method: "POST" });
    toast("Order cancelled", "success");
    lookupOrder();
  } catch (err) {
    toast(err.message, "error");
  }
}

async function refundOrder() {
  if (!currentOrderId) return;
  try {
    await api(`/payments/order/${currentOrderId}/refund`, { method: "POST" });
    toast("Refund initiated", "success");
    lookupOrder();
  } catch (err) {
    toast(err.message, "error");
  }
}

// ── Utilities ───────────────────────────────────
function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

function formatPrice(n) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "THB",
    minimumFractionDigits: 2,
  }).format(n || 0);
}

// ── Init ────────────────────────────────────────
function init() {
  initTheme();
  window.addEventListener("hashchange", handleRoute);
  handleRoute();
}

document.addEventListener("DOMContentLoaded", init);
