const API_BASE = "http://localhost:8080";
const PAYMENT_BASE = "http://localhost:8086";

const state = {
  token: localStorage.getItem("token") || "",
  userId: localStorage.getItem("userId") || "",
  username: localStorage.getItem("username") || "",
  currentPage: "auth",
};

// ===== Utility Functions =====
function showToast(message, type = "info") {
  const container = document.getElementById("toastContainer");
  const toast = document.createElement("div");
  toast.className = `toast toast-${type}`;
  const icons = { success: "✓", error: "✕", info: "ℹ" };
  toast.innerHTML = `<span>${icons[type] || "ℹ"}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = "0";
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function setLoading(btn, loading) {
  if (loading) {
    btn.dataset.originalText = btn.innerHTML;
    btn.innerHTML = '<span class="spinner"></span> Loading...';
    btn.disabled = true;
  } else {
    btn.innerHTML = btn.dataset.originalText || btn.innerHTML;
    btn.disabled = false;
  }
}

async function apiCall(url, options = {}) {
  const headers = { "Content-Type": "application/json", ...options.headers };
  if (state.token) headers["Authorization"] = `Bearer ${state.token}`;
  if (state.userId) headers["X-User-Id"] = state.userId;
  const res = await fetch(url, { ...options, headers });
  const text = await res.text();
  let data;
  try {
    data = JSON.parse(text);
  } catch {
    data = text;
  }
  if (!res.ok)
    throw new Error(
      data?.message || data?.error || `Request failed (${res.status})`,
    );
  return data;
}

function formatPrice(price) {
  return new Intl.NumberFormat("th-TH", {
    style: "currency",
    currency: "THB",
  }).format(price);
}

function getStatusBadge(status) {
  const map = {
    PENDING: "warning",
    PAID: "success",
    COMPLETED: "success",
    CANCELLED: "danger",
    REFUNDED: "purple",
    PROCESSING: "info",
  };
  return `<span class="badge badge-${map[status] || "info"}">${status}</span>`;
}

// ===== Navigation =====
function navigate(page) {
  state.currentPage = page;
  document
    .querySelectorAll(".nav-item")
    .forEach((el) => el.classList.remove("active"));
  document
    .querySelectorAll(".section-page")
    .forEach((el) => el.classList.remove("active"));
  const navEl = document.querySelector(`[data-page="${page}"]`);
  const pageEl = document.getElementById(`page-${page}`);
  if (navEl) navEl.classList.add("active");
  if (pageEl) {
    pageEl.classList.add("active");
    pageEl.style.animation = "none";
    pageEl.offsetHeight;
    pageEl.style.animation = "fadeInUp 0.4s ease";
  }
  updateUserBar();
  if (page === "products") loadProducts();
}

function updateUserBar() {
  const bar = document.getElementById("userBar");
  if (state.token && state.username) {
    bar.style.display = "flex";
    bar.innerHTML = `
      <div class="user-info"><div class="user-avatar">${state.username[0].toUpperCase()}</div>
      <span>Logged in as <strong>${state.username}</strong> (ID: ${state.userId})</span></div>
      <button class="btn btn-sm btn-outline" onclick="logout()">Logout</button>`;
  } else {
    bar.style.display = "none";
  }
}

function logout() {
  state.token = "";
  state.userId = "";
  state.username = "";
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("username");
  updateUserBar();
  navigate("auth");
  showToast("Logged out successfully", "info");
}

// ===== Auth =====
function switchAuthTab(tab) {
  document
    .querySelectorAll(".auth-tab")
    .forEach((el) => el.classList.remove("active"));
  document
    .querySelectorAll(".auth-form")
    .forEach((el) => el.classList.remove("active"));
  document.querySelector(`[data-auth-tab="${tab}"]`).classList.add("active");
  document.getElementById(`form-${tab}`).classList.add("active");
}

async function handleRegister(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const data = {
      username: document.getElementById("regUsername").value,
      password: document.getElementById("regPassword").value,
    };
    await apiCall(`${API_BASE}/auth/register`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    showToast("Registration successful! Please login.", "success");
    switchAuthTab("login");
    document.getElementById("loginUsername").value = data.username;
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const username = document.getElementById("loginUsername").value;
    const data = {
      username,
      password: document.getElementById("loginPassword").value,
    };
    const res = await apiCall(`${API_BASE}/auth/login`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    state.token = res.token || res.accessToken || res;
    state.username = username;
    state.userId = res.userId || res.id || "1";
    localStorage.setItem("token", state.token);
    localStorage.setItem("userId", state.userId);
    localStorage.setItem("username", state.username);
    showToast(`Welcome back, ${username}!`, "success");
    updateUserBar();
    navigate("products");
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

// ===== User Profile =====
async function handleCreateProfile(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const data = {
      name: document.getElementById("profileName").value,
      address: document.getElementById("profileAddress").value,
      phone: document.getElementById("profilePhone").value,
    };
    await apiCall(`${API_BASE}/users/profile`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    showToast("Profile created successfully!", "success");
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

async function loadProfile() {
  const container = document.getElementById("profileResult");
  try {
    const profile = await apiCall(`${API_BASE}/users/profile`);
    container.innerHTML = `
      <div class="order-detail-grid">
        <div class="detail-item"><div class="detail-label">Name</div><div class="detail-value">${profile.name || "-"}</div></div>
        <div class="detail-item"><div class="detail-label">Phone</div><div class="detail-value">${profile.phone || "-"}</div></div>
        <div class="detail-item"><div class="detail-label">Address</div><div class="detail-value">${profile.address || "-"}</div></div>
      </div>`;
    document.getElementById("editName").value = profile.name || "";
    document.getElementById("editAddress").value = profile.address || "";
    document.getElementById("editPhone").value = profile.phone || "";
    showToast("Profile loaded", "success");
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>${err.message}</p></div>`;
    showToast(err.message, "error");
  }
}

async function handleUpdateProfile(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const data = {
      name: document.getElementById("editName").value,
      address: document.getElementById("editAddress").value,
      phone: document.getElementById("editPhone").value,
    };
    await apiCall(`${API_BASE}/users/profile`, {
      method: "PUT",
      body: JSON.stringify(data),
    });
    showToast("Profile updated!", "success");
    loadProfile();
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

// ===== Products =====
async function loadProducts() {
  const grid = document.getElementById("productsGrid");
  grid.innerHTML =
    '<div class="empty-state"><span class="spinner" style="width:32px;height:32px;border-width:3px;display:inline-block;border:3px solid rgba(99,102,241,0.2);border-top-color:#6366f1;border-radius:50%;animation:spin 0.6s linear infinite"></span><p class="mt-16">Loading products...</p></div>';
  try {
    const products = await apiCall(`${API_BASE}/products`);
    const list = Array.isArray(products)
      ? products
      : products.content || products.data || [];
    if (list.length === 0) {
      grid.innerHTML =
        '<div class="empty-state"><div class="empty-icon">📦</div><p>No products yet. Create one!</p></div>';
      return;
    }
    grid.innerHTML = list
      .map(
        (p) => `
      <div class="product-card">
        <div class="product-name">${p.name}</div>
        <div class="product-desc">${p.description || ""}</div>
        <div class="product-price">${formatPrice(p.price)}</div>
        <div class="product-qty">Stock: ${p.quantity ?? "-"}</div>
        <div class="flex items-center justify-between mt-16">
          <span class="badge badge-info">ID: ${p.id}</span>
        </div>
      </div>`,
      )
      .join("");
  } catch (err) {
    grid.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><p>${err.message}</p></div>`;
  }
}

async function handleCreateProduct(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const data = {
      name: document.getElementById("prodName").value,
      description: document.getElementById("prodDesc").value,
      price: parseFloat(document.getElementById("prodPrice").value),
      quantity: parseInt(document.getElementById("prodQty").value),
    };
    await apiCall(`${API_BASE}/products`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    showToast("Product created!", "success");
    e.target.reset();
    loadProducts();
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

async function searchProduct() {
  const id = document.getElementById("searchProductId").value;
  const container = document.getElementById("searchProductResult");
  if (!id) {
    showToast("Enter product ID", "error");
    return;
  }
  try {
    const p = await apiCall(`${API_BASE}/products/${id}`);
    container.innerHTML = `
      <div class="product-card">
        <div class="product-name">${p.name}</div>
        <div class="product-desc">${p.description || ""}</div>
        <div class="product-price">${formatPrice(p.price)}</div>
        <div class="product-qty">Stock: ${p.quantity ?? "-"}</div>
      </div>`;
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>${err.message}</p></div>`;
    showToast(err.message, "error");
  }
}

// ===== Orders =====
let orderItems = [
  { productId: "", productName: "", productPrice: "", quantity: 1 },
];

function renderOrderItems() {
  const list = document.getElementById("orderItemsList");
  list.innerHTML = orderItems
    .map(
      (item, i) => `
    <div class="order-item-row">
      <div class="form-group"><label>Product Name</label><input class="form-control" placeholder="Product name" value="${item.productName}" onchange="orderItems[${i}].productName=this.value"></div>
      <div class="form-group"><label>Price</label><input class="form-control" type="number" step="0.01" placeholder="0.00" value="${item.productPrice}" onchange="orderItems[${i}].productPrice=parseFloat(this.value)"></div>
      <div class="form-group"><label>Qty</label><input class="form-control" type="number" min="1" value="${item.quantity}" onchange="orderItems[${i}].quantity=parseInt(this.value)"></div>
      <button class="remove-item-btn" onclick="removeOrderItem(${i})" ${orderItems.length === 1 ? 'disabled style=\"opacity:0.3\"' : ""}>✕</button>
    </div>`,
    )
    .join("");
}

function addOrderItem() {
  orderItems.push({
    productId: "",
    productName: "",
    productPrice: "",
    quantity: 1,
  });
  renderOrderItems();
}

function removeOrderItem(i) {
  if (orderItems.length > 1) {
    orderItems.splice(i, 1);
    renderOrderItems();
  }
}

async function handleCreateOrder(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  setLoading(btn, true);
  try {
    const items = orderItems.map((it, i) => ({
      productId: parseInt(it.productId) || i + 1,
      productName: it.productName,
      productPrice: parseFloat(it.productPrice) || 0,
      quantity: parseInt(it.quantity) || 1,
    }));
    const data = {
      items,
      paymentMethod: document.getElementById("paymentMethod").value,
      paymentCurrency: document.getElementById("paymentCurrency").value,
    };
    const res = await apiCall(`${API_BASE}/orders`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    showToast("Order created!", "success");
    const orderId = res.orderId || res.id || "";
    if (orderId) document.getElementById("lookupOrderId").value = orderId;
    orderItems = [
      { productId: "", productName: "", productPrice: "", quantity: 1 },
    ];
    renderOrderItems();
  } catch (err) {
    showToast(err.message, "error");
  } finally {
    setLoading(btn, false);
  }
}

async function lookupOrder() {
  const id = document.getElementById("lookupOrderId").value;
  const container = document.getElementById("orderResult");
  if (!id) {
    showToast("Enter Order ID", "error");
    return;
  }
  try {
    const order = await apiCall(`${API_BASE}/orders/customer/${id}`);
    const items = order.items || order.orderItems || [];
    container.innerHTML = `
      <div class="card mt-16">
        <div class="order-detail-grid mb-16">
          <div class="detail-item"><div class="detail-label">Order ID</div><div class="detail-value" style="font-size:12px;word-break:break-all">${order.orderId || order.id || id}</div></div>
          <div class="detail-item"><div class="detail-label">Status</div><div class="detail-value">${getStatusBadge(order.status || order.orderStatus || "UNKNOWN")}</div></div>
          <div class="detail-item"><div class="detail-label">Payment</div><div class="detail-value">${order.paymentMethod || "-"} / ${order.paymentCurrency || "-"}</div></div>
          <div class="detail-item"><div class="detail-label">Total</div><div class="detail-value">${formatPrice(order.totalAmount || order.total || 0)}</div></div>
        </div>
        ${
          items.length
            ? `<table><thead><tr><th>Product</th><th>Price</th><th>Qty</th><th>Subtotal</th></tr></thead><tbody>
          ${items.map((it) => `<tr><td>${it.productName || it.name || "-"}</td><td>${formatPrice(it.productPrice || it.price || 0)}</td><td>${it.quantity}</td><td>${formatPrice((it.productPrice || it.price || 0) * it.quantity)}</td></tr>`).join("")}
        </tbody></table>`
            : ""
        }
        <div class="actions-bar">
          <button class="btn btn-success btn-sm" onclick="payOrder('${order.orderId || order.id || id}')">💳 Pay</button>
          <button class="btn btn-danger btn-sm" onclick="cancelOrder('${order.orderId || order.id || id}')">✕ Cancel</button>
          <button class="btn btn-warning btn-sm" onclick="refundOrder('${order.orderId || order.id || id}')">↩ Refund</button>
        </div>
      </div>`;
  } catch (err) {
    container.innerHTML = `<div class="empty-state mt-16"><p>${err.message}</p></div>`;
    showToast(err.message, "error");
  }
}

async function payOrder(id) {
  try {
    await apiCall(`${PAYMENT_BASE}/payments/order/${id}/pay`, {
      method: "POST",
    });
    showToast("Payment successful!", "success");
    lookupOrder();
  } catch (err) {
    showToast(err.message, "error");
  }
}

async function cancelOrder(id) {
  try {
    await apiCall(`${PAYMENT_BASE}/payments/order/${id}/cancel`, {
      method: "POST",
    });
    showToast("Order cancelled", "info");
    lookupOrder();
  } catch (err) {
    showToast(err.message, "error");
  }
}

async function refundOrder(id) {
  try {
    await apiCall(`${PAYMENT_BASE}/payments/order/${id}/refund`, {
      method: "POST",
    });
    showToast("Refund initiated", "success");
    lookupOrder();
  } catch (err) {
    showToast(err.message, "error");
  }
}

// ===== Init =====
document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".nav-item").forEach((el) => {
    el.addEventListener("click", () => navigate(el.dataset.page));
  });
  renderOrderItems();
  if (state.token) {
    navigate("products");
  } else {
    navigate("auth");
  }
  updateUserBar();
});
