const API_BASE = 'http://localhost:8080/api';

// Rich Mock Seed Data
const defaultUsers = [
  { id: 1, username: 'admin', name: 'Alex Vance', role: 'ADMIN', email: 'admin@smartinv.com', department: 'IT Administration' },
  { id: 2, username: 'manager', name: 'Sarah Jenkins', role: 'STORE_MANAGER', email: 'sarah.j@smartinv.com', department: 'Operations' },
  { id: 3, username: 'procurement', name: 'Michael Ross', role: 'PROCUREMENT_OFFICER', email: 'michael.r@smartinv.com', department: 'Procurement' },
  { id: 4, username: 'supplier_apex', name: 'Apex Industrial', role: 'SUPPLIER', email: 'contact@apexind.com', department: 'Supply Chain' }
];

const defaultInventory = [
  { id: 1, name: 'Industrial Ball Bearing', sku: 'BRG-782-X', categoryName: 'Components', stockQty: 1240, minThreshold: 200, maxCapacity: 1500, unitPrice: 12.45, unitOfMeasure: 'units', status: 'IN_STOCK', imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY' },
  { id: 2, name: 'Grade 8 Hex Bolt 1/2"', sku: 'FCS-110-B', categoryName: 'Raw Materials', stockQty: 150, minThreshold: 500, maxCapacity: 2000, unitPrice: 0.85, unitOfMeasure: 'units', status: 'LOW_STOCK', imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8' },
  { id: 3, name: 'Lithium-Ion Cell 18650', sku: 'PWR-881-L', categoryName: 'Components', stockQty: 0, minThreshold: 100, maxCapacity: 500, unitPrice: 4.50, unitOfMeasure: 'units', status: 'OUT_OF_STOCK', imageUrl: '' },
  { id: 4, name: 'Copper Wire 2mm (Spool)', sku: 'WRE-200-C', categoryName: 'Raw Materials', stockQty: 12, minThreshold: 50, maxCapacity: 250, unitPrice: 89.99, unitOfMeasure: 'spools', status: 'LOW_STOCK', imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY' },
  { id: 5, name: 'Industrial Lub. X-200', sku: 'LUB-X20-D', categoryName: 'Raw Materials', stockQty: 45, minThreshold: 100, maxCapacity: 400, unitPrice: 65.00, unitOfMeasure: 'drums', status: 'LOW_STOCK', imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8' }
];

const defaultRequests = [
  { id: 1, reqCode: 'REQ-4092', itemName: 'Industrial Bearings XZ-9', quantity: 250, department: 'Manufacturing', priority: 'Standard', status: 'PENDING_REVIEW', createdAt: 'Oct 24' },
  { id: 2, reqCode: 'REQ-4091', itemName: 'Safety Harness Pro', quantity: 50, department: 'Operations', priority: 'High', status: 'PROCESSING', createdAt: 'Oct 23' },
  { id: 3, reqCode: 'REQ-4088', itemName: 'Lithium Battery Packs', quantity: 500, department: 'Maintenance', priority: 'High', status: 'DENIED', createdAt: 'Oct 21' },
  { id: 4, reqCode: 'REQ-4085', itemName: 'Hydraulic Fluid 50G', quantity: 10, department: 'Manufacturing', priority: 'Standard', status: 'DELIVERED', createdAt: 'Oct 20' }
];

const defaultOrders = [
  { id: 1, orderNo: 'PO-2026-001', requestId: 2, supplierName: 'Apex Industrial Parts Ltd', totalAmount: 3112.50, status: 'SHIPPED', estimatedDelivery: '2026-08-15' },
  { id: 2, orderNo: 'PO-2026-002', requestId: 4, supplierName: 'Global Raw Materials Co.', totalAmount: 650.00, status: 'DELIVERED', estimatedDelivery: '2026-08-10' }
];

const defaultNotifications = [
  { id: 1, type: 'LOW_STOCK', recipientRole: 'STORE_MANAGER', recipientEmail: 'sarah.j@smartinv.com', message: 'ALERT: Copper Wire 2mm (Spool) reached critical low level (12 units remaining).', channel: 'EMAIL', sentAt: '10 mins ago' },
  { id: 2, type: 'LOW_STOCK', recipientRole: 'PROCUREMENT_OFFICER', recipientEmail: 'michael.r@smartinv.com', message: 'ALERT: Grade 8 Hex Bolt 1/2" reached low stock threshold (150/2000).', channel: 'SMS', sentAt: '2 hours ago' },
  { id: 3, type: 'ORDER_STATUS', recipientRole: 'STORE_MANAGER', recipientEmail: 'sarah.j@smartinv.com', message: 'UPDATE: Purchase Order PO-2026-001 has been marked SHIPPED by Apex Industrial Parts Ltd.', channel: 'EMAIL', sentAt: '5 hours ago' }
];

let localUsers = [...defaultUsers];
let localInventory = [...defaultInventory];
let localRequests = [...defaultRequests];
let localOrders = [...defaultOrders];
let localNotifications = [...defaultNotifications];

async function fetchAPI(endpoint, options = {}) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      headers: { 'Content-Type': 'application/json', ...options.headers },
      ...options
    });
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) return data;
      if (data && typeof data === 'object' && !Array.isArray(data)) return data;
    }
  } catch (err) {
    // API backend unreachable or offline
  }
  return null;
}

export const ApiService = {
  async getUsers() {
    const data = await fetchAPI('/auth');
    return (data && data.length > 0) ? data : localUsers;
  },

  async getInventory(search = '', category = '') {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category) params.append('category', category);

    const data = await fetchAPI(`/inventory?${params.toString()}`);
    const items = (data && data.length > 0) ? data : localInventory;

    return items.filter(item => {
      const matchSearch = !search || item.name.toLowerCase().includes(search.toLowerCase()) || item.sku.toLowerCase().includes(search.toLowerCase());
      const matchCat = !category || category === 'All Items' || item.categoryName === category;
      return matchSearch && matchCat;
    });
  },

  async addInventoryItem(item) {
    const res = await fetchAPI('/inventory', {
      method: 'POST',
      body: JSON.stringify(item)
    });
    
    item.id = Date.now();
    item.status = item.stockQty <= 0 ? 'OUT_OF_STOCK' : (item.stockQty <= item.minThreshold ? 'LOW_STOCK' : 'IN_STOCK');
    localInventory.unshift(item);
    if (item.stockQty <= item.minThreshold) {
      localNotifications.unshift({
        id: Date.now(),
        type: 'LOW_STOCK',
        recipientRole: 'STORE_MANAGER',
        message: `AUTOMATED ALERT: Item '${item.name}' stock is low (${item.stockQty} units remaining).`,
        channel: 'EMAIL',
        sentAt: 'Just now'
      });
    }
    return true;
  },

  async updateInventoryItem(item) {
    const res = await fetchAPI('/inventory', {
      method: 'PUT',
      body: JSON.stringify(item)
    });
    
    const idx = localInventory.findIndex(i => i.id === item.id);
    if (idx !== -1) {
      item.status = item.stockQty <= 0 ? 'OUT_OF_STOCK' : (item.stockQty <= item.minThreshold ? 'LOW_STOCK' : 'IN_STOCK');
      localInventory[idx] = { ...localInventory[idx], ...item };
    }
    return true;
  },

  async getRequests(filter = '') {
    const data = await fetchAPI(`/procurement?filter=${encodeURIComponent(filter)}`);
    const reqs = (data && data.length > 0) ? data : localRequests;

    return reqs.filter(r => {
      if (!filter || filter === 'All Requests') return true;
      if (filter === 'Pending Review') return r.status === 'PENDING_REVIEW';
      if (filter === 'Processing') return r.status === 'PROCESSING';
      if (filter === 'Delivered') return r.status === 'DELIVERED';
      return true;
    });
  },

  async createRequest(req) {
    await fetchAPI('/procurement', {
      method: 'POST',
      body: JSON.stringify(req)
    });

    const newReq = {
      id: Date.now(),
      reqCode: 'REQ-' + Math.floor(1000 + Math.random() * 9000),
      itemName: req.itemName,
      quantity: req.quantity,
      department: req.department || 'Operations',
      priority: req.priority || 'Standard',
      status: 'PENDING_REVIEW',
      createdAt: 'Just now'
    };
    localRequests.unshift(newReq);
    localNotifications.unshift({
      id: Date.now(),
      type: 'APPROVAL_NEEDED',
      recipientRole: 'PROCUREMENT_OFFICER',
      message: `NEW REQUEST: Purchase request for ${req.quantity}x ${req.itemName} created.`,
      channel: 'EMAIL',
      sentAt: 'Just now'
    });
    return true;
  },

  async updateRequestStatus(id, status) {
    await fetchAPI('/procurement', {
      method: 'PUT',
      body: JSON.stringify({ id, status })
    });

    const req = localRequests.find(r => r.id === id);
    if (req) req.status = status;
    return true;
  },

  async getOrders() {
    const data = await fetchAPI('/orders');
    return (data && data.length > 0) ? data : localOrders;
  },

  async updateOrderStatus(id, status) {
    await fetchAPI('/orders', {
      method: 'PUT',
      body: JSON.stringify({ id, status })
    });

    const ord = localOrders.find(o => o.id === id);
    if (ord) ord.status = status;
    localNotifications.unshift({
      id: Date.now(),
      type: 'ORDER_STATUS',
      recipientRole: 'STORE_MANAGER',
      message: `ORDER UPDATE: Order PO-2026-00${id} status updated to ${status}.`,
      channel: 'EMAIL',
      sentAt: 'Just now'
    });
    return true;
  },

  async getNotifications() {
    const data = await fetchAPI('/notifications');
    return (data && data.length > 0) ? data : localNotifications;
  },

  async getReportSummary() {
    const data = await fetchAPI('/reports');
    if (data && data.totalItemsCount) return data;

    return {
      totalItemsCount: localInventory.reduce((acc, i) => acc + i.stockQty, 0) || 14208,
      lowStockCount: localInventory.filter(i => i.stockQty > 0 && i.stockQty <= i.minThreshold).length || 32,
      outOfStockCount: localInventory.filter(i => i.stockQty <= 0).length,
      pendingOrdersCount: localOrders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED').length || 156,
      monthlySpendFormatted: '$2.4M',
      supplierRating: '98.2%',
      openRequestsCount: localRequests.filter(r => r.status === 'PENDING_REVIEW' || r.status === 'PROCESSING').length || 24
    };
  }
};
