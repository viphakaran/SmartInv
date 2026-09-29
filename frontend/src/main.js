import { ApiService } from './services/api.js';

// Application State
const state = {
  currentTab: 'dashboard',
  currentRole: 'STORE_MANAGER', // STORE_MANAGER, PROCUREMENT_OFFICER, SUPPLIER, ADMIN
  searchQuery: '',
  selectedCategory: 'All Items',
  procurementFilter: 'All Requests',
  inventoryViewMode: 'table', // 'table' or 'grid'
  sidebarMobileOpen: false,
  inventory: [],
  requests: [],
  orders: [],
  notifications: [],
  summaryStats: {},
  users: [],
  adjustItem: null,
  historyItem: null
};

// Application Initialization
document.addEventListener('DOMContentLoaded', async () => {
  await loadAllData();
  renderApp();
  setupGlobalShortcuts();
});

async function loadAllData() {
  state.users = await ApiService.getUsers();
  state.inventory = await ApiService.getInventory(state.searchQuery, state.selectedCategory);
  state.requests = await ApiService.getRequests(state.procurementFilter);
  state.orders = await ApiService.getOrders();
  state.notifications = await ApiService.getNotifications();
  state.summaryStats = await ApiService.getReportSummary();
}

function renderApp() {
  const app = document.getElementById('app');
  if (!app) return;

  app.innerHTML = `
    <div class="saas-layout">
      ${renderSidebar()}
      <div class="saas-main">
        ${renderTopbar()}
        <main class="page-container">
          ${renderActiveTab()}
        </main>
      </div>
    </div>
    ${renderModals()}
    <div id="toastContainer" class="toast-container"></div>
  `;

  attachEventListeners();
}

/* ==========================================================================
   Sidebar Component
   ========================================================================== */
function renderSidebar() {
  const activeUser = state.users.find(u => u.role === state.currentRole) || state.users[1];
  const lowStockCount = state.inventory.filter(i => i.stockQty > 0 && i.stockQty <= i.minThreshold).length;
  const pendingRequestsCount = state.requests.filter(r => r.status === 'PENDING_REVIEW').length;

  return `
    <aside class="saas-sidebar ${state.sidebarMobileOpen ? 'mobile-open' : ''}">
      <div class="sidebar-header">
        <a href="#" class="brand-wrapper" onclick="window.switchTab('dashboard'); return false;">
          <div class="brand-icon-box">
            <span class="material-symbols-outlined" style="font-size:20px">inventory_2</span>
          </div>
          <span class="brand-name">SmartInv</span>
          <span class="brand-badge">SaaS</span>
        </a>
      </div>

      <div class="sidebar-nav">
        <span class="nav-section-title">Core Modules</span>

        <div class="sidebar-link ${state.currentTab === 'dashboard' ? 'active' : ''}" onclick="window.switchTab('dashboard')">
          <span class="material-symbols-outlined">dashboard</span>
          <span>Dashboard</span>
        </div>

        <div class="sidebar-link ${state.currentTab === 'inventory' ? 'active' : ''}" onclick="window.switchTab('inventory')">
          <span class="material-symbols-outlined">warehouse</span>
          <span>Inventory Catalog</span>
          <span class="nav-badge">${state.inventory.length}</span>
        </div>

        <div class="sidebar-link ${state.currentTab === 'procurement' ? 'active' : ''}" onclick="window.switchTab('procurement')">
          <span class="material-symbols-outlined">shopping_bag</span>
          <span>Procurement Hub</span>
          ${pendingRequestsCount > 0 ? `<span class="nav-badge danger">${pendingRequestsCount}</span>` : ''}
        </div>

        <div class="sidebar-link ${state.currentTab === 'supplier' ? 'active' : ''}" onclick="window.switchTab('supplier')">
          <span class="material-symbols-outlined">local_shipping</span>
          <span>Supplier Portal</span>
          <span class="nav-badge">${state.orders.length}</span>
        </div>

        <span class="nav-section-title" style="margin-top:12px">Monitoring</span>

        <div class="sidebar-link ${state.currentTab === 'notifications' ? 'active' : ''}" onclick="window.switchTab('notifications')">
          <span class="material-symbols-outlined">notifications</span>
          <span>Alerts Log</span>
          ${lowStockCount > 0 ? `<span class="nav-badge danger">${lowStockCount}</span>` : ''}
        </div>

        <div class="sidebar-link ${state.currentTab === 'reports' ? 'active' : ''}" onclick="window.switchTab('reports')">
          <span class="material-symbols-outlined">insights</span>
          <span>Analytics & Reports</span>
        </div>
      </div>

      <div class="sidebar-footer">
        <div class="user-profile-card" onclick="window.openModal('roleModal')" title="Click to switch test user role">
          <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80" alt="${escapeHtml(activeUser ? activeUser.name : 'User')}" class="avatar" />
          <div class="user-info">
            <p class="user-name">${escapeHtml(activeUser ? activeUser.name : 'Sarah Jenkins')}</p>
            <p class="user-role-label">
              <span class="role-pill">${formatRole(state.currentRole)}</span>
              <span>• Switch</span>
            </p>
          </div>
        </div>
      </div>
    </aside>
  `;
}

/* ==========================================================================
   Topbar Component
   ========================================================================== */
function renderTopbar() {
  const activeUser = state.users.find(u => u.role === state.currentRole) || state.users[1];

  return `
    <header class="saas-topbar">
      <div class="topbar-left">
        <button class="menu-toggle-btn" onclick="window.toggleMobileSidebar()">
          <span class="material-symbols-outlined">menu</span>
        </button>

        <div class="breadcrumb-trail">
          <span>SmartInv</span>
          <span>/</span>
          <span class="breadcrumb-active">${getTabTitle(state.currentTab)}</span>
        </div>

        <span class="engine-status-pill">
          <span class="pulse-dot"></span>
          Java Servlets API Ready
        </span>
      </div>

      <div class="topbar-right">
        <div class="topbar-search">
          <span class="material-symbols-outlined search-icon-inside">search</span>
          <input
            type="text"
            id="topSearchInput"
            class="topbar-search-input"
            placeholder="Quick search SKU / item..."
            value="${escapeHtml(state.searchQuery)}"
          />
          <span class="search-kbd-hint">/</span>
        </div>

        <button class="btn-icon" onclick="window.switchTab('notifications')" title="Notifications Log">
          <span class="material-symbols-outlined" style="font-size:20px">notifications</span>
          <span class="badge-dot"></span>
        </button>

        <button class="btn-saas btn-saas-secondary" onclick="window.openModal('roleModal')">
          <span class="material-symbols-outlined" style="font-size:18px">badge</span>
          <span>${formatRole(state.currentRole)}</span>
        </button>

        ${state.currentRole === 'STORE_MANAGER' || state.currentRole === 'ADMIN' ? `
          <button class="btn-saas btn-saas-primary" onclick="window.openModal('newItemModal')">
            <span class="material-symbols-outlined" style="font-size:18px">add</span>
            <span>New Item</span>
          </button>
        ` : `
          <button class="btn-saas btn-saas-primary" onclick="window.openModal('newRequestModal')">
            <span class="material-symbols-outlined" style="font-size:18px">add_shopping_cart</span>
            <span>New Request</span>
          </button>
        `}
      </div>
    </header>
  `;
}

function getTabTitle(tab) {
  switch (tab) {
    case 'dashboard': return 'Dashboard Overview';
    case 'inventory': return 'Inventory Catalog';
    case 'procurement': return 'Procurement Hub';
    case 'supplier': return 'Supplier Portal';
    case 'notifications': return 'Alerts Log';
    case 'reports': return 'Analytics & Reports';
    default: return 'Dashboard';
  }
}

function formatRole(role) {
  switch (role) {
    case 'STORE_MANAGER': return 'Store Manager';
    case 'PROCUREMENT_OFFICER': return 'Procurement Officer';
    case 'SUPPLIER': return 'Supplier';
    case 'ADMIN': return 'Administrator';
    default: return role;
  }
}

/* ==========================================================================
   Active Tab Renderer
   ========================================================================== */
function renderActiveTab() {
  switch (state.currentTab) {
    case 'dashboard': return renderDashboardView();
    case 'inventory': return renderInventoryView();
    case 'procurement': return renderProcurementView();
    case 'supplier': return renderSupplierView();
    case 'notifications': return renderNotificationsView();
    case 'reports': return renderReportsView();
    default: return renderDashboardView();
  }
}

/* 1. Dashboard View */
function renderDashboardView() {
  const stats = state.summaryStats;
  const criticalItems = state.inventory.filter(i => i.stockQty <= i.minThreshold);

  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Executive Dashboard</h1>
        <p class="page-desc">Real-time inventory levels, procurement status, and operational health</p>
      </div>

      <div style="display:flex; gap:10px">
        <button class="btn-saas btn-saas-secondary" onclick="window.exportCSV()">
          <span class="material-symbols-outlined" style="font-size:18px">download</span>
          <span>Export CSV</span>
        </button>
        <button class="btn-saas btn-saas-primary" onclick="window.openModal('newRequestModal')">
          <span class="material-symbols-outlined" style="font-size:18px">add</span>
          <span>Create Request</span>
        </button>
      </div>
    </div>

    <!-- 4 SaaS KPI Cards -->
    <div class="metrics-row">
      <div class="metric-card">
        <div class="metric-card-top">
          <div class="metric-icon-box metric-icon-blue">
            <span class="material-symbols-outlined" style="font-size:20px">inventory_2</span>
          </div>
          <span class="metric-tag positive">+4.2% MoM</span>
        </div>
        <div class="metric-value-box">
          <span class="metric-label">Total Inventory Units</span>
          <span class="metric-number">${(stats.totalItemsCount || 14208).toLocaleString()}</span>
          <span class="metric-subtext">Across ${state.inventory.length} tracked SKUs</span>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-card-top">
          <div class="metric-icon-box metric-icon-amber">
            <span class="material-symbols-outlined" style="font-size:20px">warning</span>
          </div>
          <span class="metric-tag alert">Action Required</span>
        </div>
        <div class="metric-value-box">
          <span class="metric-label">Critical / Low Stock</span>
          <span class="metric-number" style="color:var(--danger)">${stats.lowStockCount || 3}</span>
          <span class="metric-subtext">Items below safety reorder threshold</span>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-card-top">
          <div class="metric-icon-box metric-icon-green">
            <span class="material-symbols-outlined" style="font-size:20px">local_shipping</span>
          </div>
          <span class="metric-tag positive">${stats.supplierRating || '98.2%'} SLA</span>
        </div>
        <div class="metric-value-box">
          <span class="metric-label">Active Purchase Orders</span>
          <span class="metric-number">${stats.pendingOrdersCount || 2}</span>
          <span class="metric-subtext">2 orders in transit or processing</span>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-card-top">
          <div class="metric-icon-box metric-icon-purple">
            <span class="material-symbols-outlined" style="font-size:20px">payments</span>
          </div>
          <span class="metric-tag neutral">On Target</span>
        </div>
        <div class="metric-value-box">
          <span class="metric-label">Monthly Logistics Spend</span>
          <span class="metric-number">${stats.monthlySpendFormatted || '$2.4M'}</span>
          <span class="metric-subtext">Budget cap: $2.8M (85.7% utilized)</span>
        </div>
      </div>
    </div>

    <!-- Middle Row: Movement Chart + Critical Stock Watchlist -->
    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap:20px;">
      <!-- Stock Movement Chart -->
      <div class="saas-card">
        <div class="saas-card-header">
          <div>
            <h2 class="saas-card-title">Stock Velocity & Daily Outflows</h2>
            <p class="saas-card-desc">Units dispatched over the last 7 days</p>
          </div>
          <button class="btn-saas btn-saas-secondary" style="height:28px; padding:0 10px; font-size:12px" onclick="window.switchTab('reports')">
            Deep Dive
          </button>
        </div>

        <div class="saas-chart-container">
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:45%" title="Mon: 45k units"></div></div>
            <span class="chart-day-label">Mon</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:62%" title="Tue: 62k units"></div></div>
            <span class="chart-day-label">Tue</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:38%" title="Wed: 38k units"></div></div>
            <span class="chart-day-label">Wed</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:78%" title="Thu: 78k units"></div></div>
            <span class="chart-day-label">Thu</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill highlight" style="height:95%" title="Fri (Peak): 95k units"></div></div>
            <span class="chart-day-label" style="color:var(--primary); font-weight:700">Fri</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:50%" title="Sat: 50k units"></div></div>
            <span class="chart-day-label">Sat</span>
          </div>
          <div class="chart-bar-group">
            <div class="chart-bar-track"><div class="chart-bar-fill" style="height:22%" title="Sun: 22k units"></div></div>
            <span class="chart-day-label">Sun</span>
          </div>
        </div>
      </div>

      <!-- Critical Stock Alert List -->
      <div class="saas-card">
        <div class="saas-card-header">
          <div>
            <h2 class="saas-card-title">Critical Stock Watchlist</h2>
            <p class="saas-card-desc">Items needing immediate procurement trigger</p>
          </div>
          <span class="status-pill low-stock">${criticalItems.length} Low / Out</span>
        </div>

        <div class="table-responsive">
          <table class="saas-table">
            <thead>
              <tr>
                <th>Item / SKU</th>
                <th>Qty</th>
                <th>Status</th>
                <th style="text-align:right">Action</th>
              </tr>
            </thead>
            <tbody>
              ${criticalItems.map(item => `
                <tr>
                  <td>
                    <div class="item-meta">
                      <span class="item-name">${escapeHtml(item.name)}</span>
                      <span class="item-sku">${escapeHtml(item.sku)}</span>
                    </div>
                  </td>
                  <td>
                    <span style="font-weight:600; color:${item.stockQty <= 0 ? 'var(--danger)' : 'var(--warning)'}">${item.stockQty}</span>
                    <span style="font-size:11px; color:var(--slate-400)">/ ${item.minThreshold}</span>
                  </td>
                  <td>
                    <span class="status-pill ${item.stockQty <= 0 ? 'out-of-stock' : 'low-stock'}">
                      ${item.stockQty <= 0 ? 'Out of Stock' : 'Low Stock'}
                    </span>
                  </td>
                  <td style="text-align:right">
                    <button class="btn-saas btn-saas-secondary" style="height:28px; padding:0 8px; font-size:12px" onclick="window.quickReorder('${escapeHtml(item.name)}', ${item.minThreshold * 2})">
                      <span class="material-symbols-outlined" style="font-size:15px">bolt</span>
                      <span>Reorder</span>
                    </button>
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Recent Logistics Audit Feed -->
    <div class="saas-card">
      <div class="saas-card-header">
        <div>
          <h2 class="saas-card-title">System Audit & Operations Log</h2>
          <p class="saas-card-desc">Recent inventory updates, order statuses, and automated alerts</p>
        </div>
        <button class="btn-saas btn-saas-secondary" style="height:28px; padding:0 10px; font-size:12px" onclick="window.switchTab('notifications')">
          View All Logs
        </button>
      </div>

      <div style="padding:16px 20px; display:flex; flex-direction:column; gap:12px">
        ${state.notifications.slice(0, 3).map(notif => `
          <div style="display:flex; align-items:flex-start; gap:12px; padding:10px 12px; background:var(--slate-50); border:1px solid var(--border-light); border-radius:8px">
            <span class="material-symbols-outlined" style="font-size:20px; color:${notif.type === 'LOW_STOCK' ? 'var(--danger)' : 'var(--primary)'}">
              ${notif.type === 'LOW_STOCK' ? 'warning' : 'mark_email_read'}
            </span>
            <div style="flex:1">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <span style="font-size:12.5px; font-weight:600; color:var(--slate-900)">${notif.type.replace('_', ' ')} • via ${notif.channel || 'EMAIL'}</span>
                <span style="font-size:11px; color:var(--slate-400)">${notif.sentAt || 'Just now'}</span>
              </div>
              <p style="font-size:13px; color:var(--slate-700); margin-top:2px">${escapeHtml(notif.message)}</p>
            </div>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

/* 2. Inventory Catalog View */
function renderInventoryView() {
  const categories = ['All Items', 'Raw Materials', 'Components', 'Finished Goods', 'Packaging'];

  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Inventory Catalog</h1>
        <p class="page-desc">Track and control real-time stock levels, SKU thresholds, and unit costs</p>
      </div>

      <div style="display:flex; gap:10px">
        <button class="btn-saas btn-saas-secondary" onclick="window.exportCSV()">
          <span class="material-symbols-outlined" style="font-size:18px">download</span>
          <span>Export CSV</span>
        </button>
        ${state.currentRole === 'STORE_MANAGER' || state.currentRole === 'ADMIN' ? `
          <button class="btn-saas btn-saas-primary" onclick="window.openModal('newItemModal')">
            <span class="material-symbols-outlined" style="font-size:18px">add</span>
            <span>Add Item</span>
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Filters & Search Toolbar -->
    <div class="saas-card" style="padding:16px 20px;">
      <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:14px">
        <div class="filter-pills-row">
          ${categories.map(cat => `
            <button class="filter-pill ${state.selectedCategory === cat ? 'active' : ''}" onclick="window.selectCategory('${cat}')">
              ${cat}
            </button>
          `).join('')}
        </div>

        <div style="display:flex; align-items:center; gap:8px">
          <div style="display:flex; border:1px solid var(--border-light); border-radius:6px; overflow:hidden">
            <button
              class="btn-icon"
              style="border:none; border-radius:0; width:30px; height:30px; background:${state.inventoryViewMode==='table'?'var(--slate-200)':'transparent'}"
              onclick="window.setInventoryViewMode('table')"
              title="Table View"
            >
              <span class="material-symbols-outlined" style="font-size:18px">table_rows</span>
            </button>
            <button
              class="btn-icon"
              style="border:none; border-radius:0; width:30px; height:30px; background:${state.inventoryViewMode==='grid'?'var(--slate-200)':'transparent'}"
              onclick="window.setInventoryViewMode('grid')"
              title="Grid View"
            >
              <span class="material-symbols-outlined" style="font-size:18px">grid_view</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Inventory Display -->
    ${state.inventoryViewMode === 'table' ? renderInventoryTable() : renderInventoryGrid()}
  `;
}

function renderInventoryTable() {
  return `
    <div class="saas-card">
      <div class="table-responsive">
        <table class="saas-table">
          <thead>
            <tr>
              <th>Item / SKU</th>
              <th>Category</th>
              <th>Stock & Capacity</th>
              <th>Unit Cost</th>
              <th>Status</th>
              <th style="text-align:right">Actions</th>
            </tr>
          </thead>
          <tbody>
            ${state.inventory.map(item => {
              const maxCap = item.maxCapacity || 1000;
              const pct = Math.min(100, Math.round((item.stockQty / maxCap) * 100));
              const statusClass = item.status === 'IN_STOCK' ? 'in-stock' : (item.status === 'LOW_STOCK' ? 'low-stock' : 'out-of-stock');
              const statusLabel = item.status === 'IN_STOCK' ? 'In Stock' : (item.status === 'LOW_STOCK' ? 'Low Stock' : 'Out of Stock');
              const progressColor = item.stockQty <= 0 ? 'var(--danger)' : (item.stockQty <= item.minThreshold ? 'var(--warning)' : 'var(--primary)');

              return `
                <tr>
                  <td>
                    <div class="item-cell">
                      ${item.imageUrl ? `<img src="${escapeHtml(item.imageUrl)}" class="item-thumbnail" alt="" onerror="this.style.display='none'" />` : `
                        <div class="item-thumbnail" style="display:flex; align-items:center; justify-content:center; color:var(--slate-400)">
                          <span class="material-symbols-outlined" style="font-size:18px">category</span>
                        </div>
                      `}
                      <div class="item-meta">
                        <span class="item-name">${escapeHtml(item.name)}</span>
                        <span class="item-sku">SKU: ${escapeHtml(item.sku)}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="metric-tag neutral">${escapeHtml(item.categoryName || 'General')}</span>
                  </td>
                  <td class="capacity-cell">
                    <div class="capacity-meta">
                      <span style="font-weight:600">${item.stockQty.toLocaleString()} units</span>
                      <span style="color:var(--slate-400)">${pct}%</span>
                    </div>
                    <div class="progress-track">
                      <div class="progress-thumb" style="width:${pct}%; background-color:${progressColor}"></div>
                    </div>
                  </td>
                  <td>
                    <span style="font-weight:600; color:var(--slate-900)">$${item.unitPrice.toFixed(2)}</span>
                    <span style="font-size:11px; color:var(--slate-400)">/ ${escapeHtml(item.unitOfMeasure || 'unit')}</span>
                  </td>
                  <td>
                    <span class="status-pill ${statusClass}">${statusLabel}</span>
                  </td>
                  <td style="text-align:right">
                    <div style="display:inline-flex; gap:6px">
                      <button class="btn-saas btn-saas-secondary" style="height:28px; padding:0 8px; font-size:12px" onclick="window.openHistoryModal(${item.id})">
                        Audit
                      </button>
                      ${state.currentRole === 'STORE_MANAGER' || state.currentRole === 'ADMIN' ? `
                        <button class="btn-saas btn-saas-primary" style="height:28px; padding:0 8px; font-size:12px" onclick="window.openAdjustModal(${item.id})">
                          Adjust
                        </button>
                      ` : ''}
                    </div>
                  </td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

function renderInventoryGrid() {
  return `
    <div style="display:grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap:16px;">
      ${state.inventory.map(item => {
        const maxCap = item.maxCapacity || 1000;
        const pct = Math.min(100, Math.round((item.stockQty / maxCap) * 100));
        const statusClass = item.status === 'IN_STOCK' ? 'in-stock' : (item.status === 'LOW_STOCK' ? 'low-stock' : 'out-of-stock');
        const statusLabel = item.status === 'IN_STOCK' ? 'In Stock' : (item.status === 'LOW_STOCK' ? 'Low Stock' : 'Out of Stock');
        const progressColor = item.stockQty <= 0 ? 'var(--danger)' : (item.stockQty <= item.minThreshold ? 'var(--warning)' : 'var(--primary)');

        return `
          <div class="saas-card" style="padding:16px; display:flex; flex-direction:column; gap:12px">
            <div style="display:flex; justify-content:space-between; align-items:flex-start">
              <div>
                <h3 style="font-size:14.5px; font-weight:600; color:var(--slate-900)">${escapeHtml(item.name)}</h3>
                <span class="item-sku">SKU: ${escapeHtml(item.sku)}</span>
              </div>
              <span class="status-pill ${statusClass}">${statusLabel}</span>
            </div>

            <div>
              <div class="capacity-meta">
                <span style="font-size:12px; color:var(--slate-500)">Stock Level</span>
                <span style="font-size:12px; font-weight:600">${item.stockQty} / ${maxCap}</span>
              </div>
              <div class="progress-track" style="margin-top:4px">
                <div class="progress-thumb" style="width:${pct}%; background-color:${progressColor}"></div>
              </div>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-light); padding-top:10px">
              <span style="font-weight:700; font-size:16px">$${item.unitPrice.toFixed(2)}</span>
              <div style="display:flex; gap:6px">
                <button class="btn-saas btn-saas-secondary" style="height:26px; padding:0 8px; font-size:11.5px" onclick="window.openHistoryModal(${item.id})">History</button>
                ${state.currentRole === 'STORE_MANAGER' || state.currentRole === 'ADMIN' ? `
                  <button class="btn-saas btn-saas-primary" style="height:26px; padding:0 8px; font-size:11.5px" onclick="window.openAdjustModal(${item.id})">Adjust</button>
                ` : ''}
              </div>
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

/* 3. Procurement Hub View */
function renderProcurementView() {
  const reqFilters = ['All Requests', 'Pending Review', 'Processing', 'Delivered'];

  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Procurement Hub</h1>
        <p class="page-desc">Review requisitions, manage approval workflows, and generate purchase orders</p>
      </div>

      <button class="btn-saas btn-saas-primary" onclick="window.openModal('newRequestModal')">
        <span class="material-symbols-outlined" style="font-size:18px">add</span>
        <span>New Purchase Request</span>
      </button>
    </div>

    <!-- Filter Toolbar -->
    <div class="saas-card" style="padding:14px 20px;">
      <div class="filter-pills-row">
        ${reqFilters.map(f => `
          <button class="filter-pill ${state.procurementFilter === f ? 'active' : ''}" onclick="window.selectProcurementFilter('${f}')">
            ${f}
          </button>
        `).join('')}
      </div>
    </div>

    <!-- Requests Table -->
    <div class="saas-card">
      <div class="table-responsive">
        <table class="saas-table">
          <thead>
            <tr>
              <th>Req Code</th>
              <th>Item Requested</th>
              <th>Department</th>
              <th>Quantity</th>
              <th>Priority</th>
              <th>Status</th>
              <th style="text-align:right">Approval Actions</th>
            </tr>
          </thead>
          <tbody>
            ${state.requests.map(req => {
              const isPending = req.status === 'PENDING_REVIEW';
              const canReview = (state.currentRole === 'PROCUREMENT_OFFICER' || state.currentRole === 'ADMIN') && isPending;
              const priorityClass = req.priority === 'High' ? 'alert' : 'neutral';

              return `
                <tr>
                  <td style="font-family:var(--font-mono); font-weight:600; color:var(--slate-800)">
                    ${escapeHtml(req.reqCode)}
                  </td>
                  <td>
                    <span style="font-weight:600; color:var(--slate-900)">${escapeHtml(req.itemName)}</span>
                  </td>
                  <td>
                    <span style="color:var(--slate-600)">${escapeHtml(req.department || 'Operations')}</span>
                  </td>
                  <td>
                    <span style="font-weight:600">${req.quantity.toLocaleString()} units</span>
                  </td>
                  <td>
                    <span class="metric-tag ${priorityClass}">${escapeHtml(req.priority || 'Standard')}</span>
                  </td>
                  <td>
                    <span class="status-pill ${req.status==='APPROVED'||req.status==='DELIVERED'?'delivered':(req.status==='DENIED'?'denied':'pending')}">
                      ${req.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td style="text-align:right">
                    ${canReview ? `
                      <div style="display:inline-flex; gap:6px">
                        <button class="btn-saas btn-saas-primary" style="height:28px; padding:0 10px; font-size:12px; background:var(--success); border-color:var(--success)" onclick="window.updateRequest('${req.id}', 'APPROVED')">
                          Approve
                        </button>
                        <button class="btn-saas btn-saas-secondary" style="height:28px; padding:0 8px; font-size:12px; color:var(--danger); border-color:var(--danger-border)" onclick="window.updateRequest('${req.id}', 'DENIED')">
                          Deny
                        </button>
                      </div>
                    ` : `
                      <span style="font-size:12px; color:var(--slate-400)">${req.createdAt || 'Recent'}</span>
                    `}
                  </td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

/* 4. Supplier Portal View */
function renderSupplierView() {
  const canUpdate = state.currentRole === 'SUPPLIER' || state.currentRole === 'ADMIN';

  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Supplier Portal</h1>
        <p class="page-desc">Track fulfilled Purchase Orders, shipment tracking numbers, and delivery milestones</p>
      </div>

      <div style="display:flex; gap:10px">
        <span class="metric-tag positive" style="padding:6px 12px; font-size:12px">
          SLA Compliance: 98.2%
        </span>
      </div>
    </div>

    <div class="saas-card">
      <div class="table-responsive">
        <table class="saas-table">
          <thead>
            <tr>
              <th>PO Number</th>
              <th>Assigned Supplier</th>
              <th>Order Total</th>
              <th>Est. Delivery</th>
              <th>Shipping Status</th>
              <th style="text-align:right">Status Update</th>
            </tr>
          </thead>
          <tbody>
            ${state.orders.map(order => `
              <tr>
                <td style="font-family:var(--font-mono); font-weight:600; color:var(--slate-900)">
                  ${escapeHtml(order.orderNo)}
                </td>
                <td>
                  <span style="font-weight:500">${escapeHtml(order.supplierName || 'Apex Industrial Parts Ltd')}</span>
                </td>
                <td>
                  <span style="font-weight:600; color:var(--slate-900)">$${order.totalAmount.toFixed(2)}</span>
                </td>
                <td>
                  <span style="color:var(--slate-600)">${escapeHtml(order.estimatedDelivery || 'In Progress')}</span>
                </td>
                <td>
                  <span class="status-pill ${order.status === 'DELIVERED' ? 'delivered' : (order.status === 'SHIPPED' ? 'shipped' : 'pending')}">
                    ${order.status}
                  </span>
                </td>
                <td style="text-align:right">
                  ${canUpdate ? `
                    <select
                      class="form-control"
                      style="height:30px; font-size:12px; display:inline-block; width:auto"
                      onchange="window.updateOrderStatus('${order.id}', this.value)"
                    >
                      <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                      <option value="SHIPPED" ${order.status === 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                      <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                    </select>
                  ` : `
                    <span style="font-size:12px; color:var(--slate-400)">View Only</span>
                  `}
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

/* 5. Alerts & Notifications View */
function renderNotificationsView() {
  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Automated Alert Logs</h1>
        <p class="page-desc">Simulated Email and SMS dispatches for low-stock triggers and procurement events</p>
      </div>

      <button class="btn-saas btn-saas-secondary" onclick="window.clearNotifications()">
        <span class="material-symbols-outlined" style="font-size:18px">done_all</span>
        <span>Mark All as Read</span>
      </button>
    </div>

    <div class="saas-card" style="padding:16px 20px; display:flex; flex-direction:column; gap:10px">
      ${state.notifications.map(n => `
        <div style="display:flex; gap:14px; padding:14px; background:var(--slate-50); border:1px solid var(--border-light); border-radius:8px">
          <div style="width:36px; height:36px; border-radius:8px; display:flex; align-items:center; justify-content:center; background:${n.type==='LOW_STOCK'?'var(--danger-bg)':'var(--primary-light)'}; color:${n.type==='LOW_STOCK'?'var(--danger)':'var(--primary)'}">
            <span class="material-symbols-outlined" style="font-size:20px">${n.type==='LOW_STOCK'?'notification_important':'email'}</span>
          </div>
          <div style="flex:1">
            <div style="display:flex; justify-content:space-between; align-items:center">
              <span style="font-size:12.5px; font-weight:600; color:var(--slate-900)">
                ${n.type} • Dispatched via <span class="metric-tag neutral">${n.channel || 'EMAIL'}</span>
              </span>
              <span style="font-size:11px; color:var(--slate-400)">${n.sentAt || 'Just now'}</span>
            </div>
            <p style="font-size:13.5px; color:var(--slate-700); margin-top:4px">${escapeHtml(n.message)}</p>
            <span style="font-size:11.5px; color:var(--slate-400); margin-top:4px; display:block">
              Recipient: <strong>${escapeHtml(n.recipientEmail || n.recipientRole)}</strong>
            </span>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

/* 6. Analytics & Reports View */
function renderReportsView() {
  const totalValue = state.inventory.reduce((sum, i) => sum + (i.stockQty * i.unitPrice), 0);

  return `
    <div class="page-header-row">
      <div>
        <h1 class="page-title">Analytics & Reports</h1>
        <p class="page-desc">Comprehensive valuation breakdown, fulfillment accuracy, and operational insights</p>
      </div>

      <button class="btn-saas btn-saas-primary" onclick="window.exportCSV()">
        <span class="material-symbols-outlined" style="font-size:18px">download</span>
        <span>Download CSV Report</span>
      </button>
    </div>

    <div class="metrics-row">
      <div class="metric-card">
        <span class="metric-label">Total Inventory Valuation</span>
        <span class="metric-number" style="color:var(--primary)">$${totalValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
        <span class="metric-subtext">Calculated across live items</span>
      </div>

      <div class="metric-card">
        <span class="metric-label">Fulfillment Accuracy</span>
        <span class="metric-number">99.4%</span>
        <span class="metric-subtext">Within 24hr delivery SLA</span>
      </div>

      <div class="metric-card">
        <span class="metric-label">Reorder Frequency</span>
        <span class="metric-number">12.5 days</span>
        <span class="metric-subtext">Average replenishment cycle</span>
      </div>
    </div>

    <div class="saas-card" style="padding:20px;">
      <h3 class="saas-card-title">Stock Health Distribution</h3>
      <p class="saas-card-desc" style="margin-bottom:16px">Ratio of inventory healthy vs low vs depleted</p>

      <div style="height:18px; border-radius:6px; overflow:hidden; display:flex">
        <div style="width:70%; background:var(--success)" title="70% In Stock"></div>
        <div style="width:22%; background:var(--warning)" title="22% Low Stock"></div>
        <div style="width:8%; background:var(--danger)" title="8% Out of Stock"></div>
      </div>

      <div style="display:flex; justify-content:space-between; margin-top:12px; font-size:12.5px; color:var(--slate-600)">
        <span><strong style="color:var(--success)">● 70%</strong> Normal In-Stock</span>
        <span><strong style="color:var(--warning)">● 22%</strong> Reorder Threshold</span>
        <span><strong style="color:var(--danger)">● 8%</strong> Depleted Stock</span>
      </div>
    </div>
  `;
}

/* ==========================================================================
   Modals Implementation
   ========================================================================== */
function renderModals() {
  return `
    <!-- Role Switcher Modal -->
    <div id="roleModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h2 class="modal-title">Switch Active Role</h2>
          <button class="btn-icon" onclick="window.closeModal('roleModal')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <div class="modal-body">
          <p style="font-size:13px; color:var(--slate-500)">
            Select a role to test role-based permissions and actions across the system:
          </p>

          <div style="display:flex; flex-direction:column; gap:8px">
            <div class="metric-card" style="cursor:pointer; padding:12px" onclick="window.setRole('STORE_MANAGER')">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <strong>👔 Store Manager</strong>
                <span class="metric-tag neutral">Inventory & Stock Alerts</span>
              </div>
              <p style="font-size:12px; color:var(--slate-500); margin-top:4px">Can adjust stock counts, create items, and trigger reorders.</p>
            </div>

            <div class="metric-card" style="cursor:pointer; padding:12px" onclick="window.setRole('PROCUREMENT_OFFICER')">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <strong>📋 Procurement Officer</strong>
                <span class="metric-tag positive">Review & Approvals</span>
              </div>
              <p style="font-size:12px; color:var(--slate-500); margin-top:4px">Can approve or deny purchase requisitions and inspect POs.</p>
            </div>

            <div class="metric-card" style="cursor:pointer; padding:12px" onclick="window.setRole('SUPPLIER')">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <strong>🚛 Supplier</strong>
                <span class="metric-tag neutral">Order Dispatch</span>
              </div>
              <p style="font-size:12px; color:var(--slate-500); margin-top:4px">Can update shipment progress (Pending → Shipped → Delivered).</p>
            </div>

            <div class="metric-card" style="cursor:pointer; padding:12px" onclick="window.setRole('ADMIN')">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <strong>⚡ System Administrator</strong>
                <span class="metric-tag alert">Full Access</span>
              </div>
              <p style="font-size:12px; color:var(--slate-500); margin-top:4px">Complete unrestricted privileges across all modules.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- New Item Modal -->
    <div id="newItemModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h2 class="modal-title">Add Inventory Item</h2>
          <button class="btn-icon" onclick="window.closeModal('newItemModal')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <form onsubmit="window.handleCreateItem(event)">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Item Title</label>
              <input type="text" id="itemNameInput" class="form-control" placeholder="e.g. Copper Wire 2mm (Spool)" required />
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px">
              <div class="form-group">
                <label class="form-label">SKU</label>
                <input type="text" id="itemSkuInput" class="form-control" placeholder="e.g. WRE-200-C" required />
              </div>
              <div class="form-group">
                <label class="form-label">Category</label>
                <select id="itemCatInput" class="form-control">
                  <option value="Raw Materials">Raw Materials</option>
                  <option value="Components">Components</option>
                  <option value="Finished Goods">Finished Goods</option>
                  <option value="Packaging">Packaging</option>
                </select>
              </div>
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px">
              <div class="form-group">
                <label class="form-label">Initial Quantity</label>
                <input type="number" id="itemQtyInput" class="form-control" placeholder="100" required />
              </div>
              <div class="form-group">
                <label class="form-label">Unit Cost ($)</label>
                <input type="number" step="0.01" id="itemPriceInput" class="form-control" placeholder="12.50" required />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn-saas btn-saas-secondary" onclick="window.closeModal('newItemModal')">Cancel</button>
            <button type="submit" class="btn-saas btn-saas-primary">Create Item</button>
          </div>
        </form>
      </div>
    </div>

    <!-- New Request Modal -->
    <div id="newRequestModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h2 class="modal-title">New Purchase Requisition</h2>
          <button class="btn-icon" onclick="window.closeModal('newRequestModal')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <form onsubmit="window.handleCreateRequest(event)">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Item / Part Specification</label>
              <input type="text" id="reqItemName" class="form-control" placeholder="e.g. Industrial Bearings XZ-9" required />
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px">
              <div class="form-group">
                <label class="form-label">Required Quantity</label>
                <input type="number" id="reqQuantity" class="form-control" placeholder="250" required />
              </div>
              <div class="form-group">
                <label class="form-label">Priority Level</label>
                <select id="reqPriority" class="form-control">
                  <option value="Standard">Standard</option>
                  <option value="High">High (Urgent)</option>
                  <option value="Low">Low (Stocking)</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Requesting Department</label>
              <input type="text" id="reqDept" class="form-control" placeholder="Manufacturing" value="Manufacturing" required />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn-saas btn-saas-secondary" onclick="window.closeModal('newRequestModal')">Cancel</button>
            <button type="submit" class="btn-saas btn-saas-primary">Submit Requisition</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Adjust Stock Level Modal (No prompt()!) -->
    <div id="adjustStockModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h2 class="modal-title">Adjust Stock Level</h2>
          <button class="btn-icon" onclick="window.closeModal('adjustStockModal')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <div class="modal-body" id="adjustStockModalBody">
          <!-- Filled dynamically -->
        </div>
      </div>
    </div>

    <!-- Stock History Modal (No alert()!) -->
    <div id="itemHistoryModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h2 class="modal-title">Inventory Audit Trail</h2>
          <button class="btn-icon" onclick="window.closeModal('itemHistoryModal')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <div class="modal-body" id="itemHistoryModalBody">
          <!-- Filled dynamically -->
        </div>
      </div>
    </div>
  `;
}

/* ==========================================================================
   Global Shortcuts & Events
   ========================================================================== */
function attachEventListeners() {
  const searchInput = document.getElementById('topSearchInput');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value;
      debounce(async () => {
        state.inventory = await ApiService.getInventory(state.searchQuery, state.selectedCategory);
        renderApp();
      }, 200)();
    });
  }
}

function setupGlobalShortcuts() {
  window.addEventListener('keydown', (e) => {
    if (e.key === '/' && document.activeElement.tagName !== 'INPUT') {
      e.preventDefault();
      document.getElementById('topSearchInput')?.focus();
    }
  });
}

let debounceTimer;
function debounce(func, delay) {
  return function(...args) {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => func.apply(this, args), delay);
  };
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

/* ==========================================================================
   Window Exports for Inline Handlers
   ========================================================================== */
window.switchTab = async (tab) => {
  state.currentTab = tab;
  state.sidebarMobileOpen = false;
  renderApp();
};

window.toggleMobileSidebar = () => {
  state.sidebarMobileOpen = !state.sidebarMobileOpen;
  renderApp();
};

window.setInventoryViewMode = (mode) => {
  state.inventoryViewMode = mode;
  renderApp();
};

window.setRole = (role) => {
  state.currentRole = role;
  window.closeModal('roleModal');
  window.showToast(`Switched active role to ${formatRole(role)}`, 'info');
  renderApp();
};

window.selectCategory = async (cat) => {
  state.selectedCategory = cat;
  state.inventory = await ApiService.getInventory(state.searchQuery, state.selectedCategory);
  renderApp();
};

window.selectProcurementFilter = async (filter) => {
  state.procurementFilter = filter;
  state.requests = await ApiService.getRequests(state.procurementFilter);
  renderApp();
};

window.openModal = (id) => {
  document.getElementById(id)?.classList.add('active');
};

window.closeModal = (id) => {
  document.getElementById(id)?.classList.remove('active');
};

window.showToast = (message, type = 'success') => {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `saas-toast ${type}`;
  toast.innerHTML = `
    <span class="material-symbols-outlined" style="font-size:18px">
      ${type === 'success' ? 'check_circle' : 'info'}
    </span>
    <span>${escapeHtml(message)}</span>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 250);
  }, 3500);
};

window.quickReorder = async (itemName, qty) => {
  await ApiService.createRequest({ itemName, quantity: qty, department: 'Operations', priority: 'High' });
  state.requests = await ApiService.getRequests(state.procurementFilter);
  state.notifications = await ApiService.getNotifications();
  window.showToast(`Requisition submitted for ${qty}x ${itemName}`, 'success');
  renderApp();
};

window.handleCreateItem = async (e) => {
  e.preventDefault();
  const name = document.getElementById('itemNameInput').value;
  const sku = document.getElementById('itemSkuInput').value;
  const categoryName = document.getElementById('itemCatInput').value;
  const stockQty = parseInt(document.getElementById('itemQtyInput').value) || 0;
  const unitPrice = parseFloat(document.getElementById('itemPriceInput').value) || 0;

  await ApiService.addInventoryItem({
    name, sku, categoryName, stockQty, minThreshold: 50, maxCapacity: 1000, unitPrice, unitOfMeasure: 'units'
  });
  window.closeModal('newItemModal');
  window.showToast(`Item "${name}" created successfully`, 'success');
  await loadAllData();
  renderApp();
};

window.handleCreateRequest = async (e) => {
  e.preventDefault();
  const itemName = document.getElementById('reqItemName').value;
  const quantity = parseInt(document.getElementById('reqQuantity').value) || 1;
  const priority = document.getElementById('reqPriority').value;
  const department = document.getElementById('reqDept').value;

  await ApiService.createRequest({ itemName, quantity, priority, department });
  window.closeModal('newRequestModal');
  window.showToast(`Purchase requisition for ${quantity}x ${itemName} submitted`, 'success');
  await loadAllData();
  renderApp();
};

window.updateRequest = async (id, status) => {
  await ApiService.updateRequestStatus(parseInt(id), status);
  window.showToast(`Request ${status === 'APPROVED' ? 'Approved' : 'Denied'}`, status === 'APPROVED' ? 'success' : 'info');
  await loadAllData();
  renderApp();
};

window.updateOrderStatus = async (id, status) => {
  await ApiService.updateOrderStatus(parseInt(id), status);
  window.showToast(`Order status updated to ${status}`, 'success');
  await loadAllData();
  renderApp();
};

window.openAdjustModal = (id) => {
  const item = state.inventory.find(i => i.id === id);
  if (!item) return;

  const body = document.getElementById('adjustStockModalBody');
  if (!body) return;

  body.innerHTML = `
    <div style="display:flex; align-items:center; gap:12px; padding:12px; background:var(--slate-50); border:1px solid var(--border-light); border-radius:8px">
      <div class="item-meta">
        <strong style="color:var(--slate-900)">${escapeHtml(item.name)}</strong>
        <span class="item-sku">SKU: ${escapeHtml(item.sku)}</span>
      </div>
    </div>

    <div class="form-group" style="margin-top:12px">
      <label class="form-label">Adjust Available Stock</label>
      <div style="display:flex; gap:8px; align-items:center">
        <button type="button" class="btn-icon" style="width:38px; height:38px" onclick="document.getElementById('adjustQtyInput').stepDown()">
          <span class="material-symbols-outlined">remove</span>
        </button>
        <input type="number" id="adjustQtyInput" class="form-control" style="text-align:center; font-weight:700; font-size:16px" value="${item.stockQty}" />
        <button type="button" class="btn-icon" style="width:38px; height:38px" onclick="document.getElementById('adjustQtyInput').stepUp()">
          <span class="material-symbols-outlined">add</span>
        </button>
      </div>
    </div>

    <div class="modal-footer" style="padding:14px 0 0 0; background:transparent; border-top:none">
      <button type="button" class="btn-saas btn-saas-secondary" onclick="window.closeModal('adjustStockModal')">Cancel</button>
      <button type="button" class="btn-saas btn-saas-primary" onclick="window.saveAdjustStock(${item.id})">Save Changes</button>
    </div>
  `;

  window.openModal('adjustStockModal');
};

window.saveAdjustStock = async (id) => {
  const item = state.inventory.find(i => i.id === id);
  const newQty = parseInt(document.getElementById('adjustQtyInput').value) || 0;
  if (item) {
    item.stockQty = newQty;
    await ApiService.updateInventoryItem(item);
    window.closeModal('adjustStockModal');
    window.showToast(`Updated stock for ${item.name} to ${newQty}`, 'success');
    await loadAllData();
    renderApp();
  }
};

window.openHistoryModal = (id) => {
  const item = state.inventory.find(i => i.id === id);
  if (!item) return;

  const body = document.getElementById('itemHistoryModalBody');
  if (!body) return;

  body.innerHTML = `
    <div style="margin-bottom:12px">
      <strong style="font-size:15px; color:var(--slate-900)">${escapeHtml(item.name)}</strong>
      <p style="font-size:12px; color:var(--slate-500)">SKU: ${escapeHtml(item.sku)} • Current: ${item.stockQty} ${item.unitOfMeasure || 'units'}</p>
    </div>

    <div class="table-responsive">
      <table class="saas-table">
        <thead>
          <tr>
            <th>Event</th>
            <th>Qty Change</th>
            <th>Logged By</th>
            <th style="text-align:right">Timestamp</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Stock Received (PO-2026-002)</td>
            <td><strong style="color:var(--success)">+200</strong></td>
            <td>Warehouse Dock B</td>
            <td style="text-align:right; font-size:11px; color:var(--slate-400)">Oct 10, 14:22</td>
          </tr>
          <tr>
            <td>Dispatched to Manufacturing</td>
            <td><strong style="color:var(--danger)">-50</strong></td>
            <td>Sarah Jenkins</td>
            <td style="text-align:right; font-size:11px; color:var(--slate-400)">Oct 18, 09:15</td>
          </tr>
          <tr>
            <td>Safety Audit & Cycle Count</td>
            <td><strong style="color:var(--slate-700)">Verified</strong></td>
            <td>Alex Vance</td>
            <td style="text-align:right; font-size:11px; color:var(--slate-400)">Oct 22, 11:30</td>
          </tr>
        </tbody>
      </table>
    </div>
  `;

  window.openModal('itemHistoryModal');
};

window.clearNotifications = () => {
  state.notifications = [];
  window.showToast('All alerts marked as read', 'info');
  renderApp();
};

window.exportCSV = () => {
  const headers = ['ID', 'Name', 'SKU', 'Category', 'StockQty', 'MinThreshold', 'UnitPrice', 'Status'];
  const rows = state.inventory.map(i => [
    i.id,
    `"${(i.name || '').replace(/"/g, '""')}"`,
    `"${(i.sku || '').replace(/"/g, '""')}"`,
    `"${(i.categoryName || '').replace(/"/g, '""')}"`,
    i.stockQty,
    i.minThreshold,
    i.unitPrice,
    i.status
  ]);

  const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement('a');
  link.setAttribute('href', encodedUri);
  link.setAttribute('download', `smartinv_inventory_report_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.showToast('Inventory report downloaded as CSV', 'success');
};
