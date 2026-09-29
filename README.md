# Smart Inventory & Procurement Management System (SmartInv)

> **Modern Lightweight Light SaaS Web Application & 3-Tier Architecture**  
> Built for **CCS375 Web Technologies** featuring a modern SaaS Frontend (Inter font, reactive dashboard, role-based workflows), Java Servlets Backend API, MySQL / SQLite resilient storage, and Docker containerization.

---

## 🌟 Modern Light SaaS Architecture & Features

1. **Modern Light SaaS Dashboard:**
   - **Real-Time KPI Cards:** Total inventory items with trend indicators, critical low-stock count, active purchase orders, and monthly spend tracking.
   - **Stock Movement Analytics:** Interactive 7-day velocity bar chart with hover tooltips and daily breakdown.
   - **Critical Stock Watchlist:** Low-stock items with 1-click **Quick Reorder** action triggers.
   - **Live Audit Trail:** Chronological log of shipments, stock changes, and automated alerts.

2. **Inventory Management & Catalog:**
   - **Fast Search & Category Filtering:** Instant search by item title or SKU with categories (*Raw Materials*, *Components*, *Finished Goods*, *Packaging*).
   - **Table & Grid View Toggle:** Switch seamlessly between enterprise SaaS data table and responsive card grid.
   - **In-App Modal Steppers:** Smooth modal for adjusting available stock (+ / - stepper) and viewing historical audit records without browser popups.
   - **Live CSV Export:** 1-click export generating formatted CSV reports of current inventory data.

3. **Procurement Hub:**
   - **Requisition Tracking:** Requisitions with auto-generated requisition codes (`REQ-4092`), priority tagging (`High`, `Standard`, `Low`), and department attribution.
   - **Multi-Stage Approval Workflow:** Procurement Officers and Administrators can 1-click **Approve** (generating POs) or **Deny** with in-app toast feedback.

4. **Supplier Portal:**
   - Real-time Purchase Order tracking with estimated delivery dates and amounts.
   - Inline status updates (`PENDING` -> `SHIPPED` -> `DELIVERED`).

5. **Automated Notification System:**
   - Low-stock trigger dispatches simulated Email & SMS alerts to Store Managers and Procurement Officers.
   - Real-time notification log tracking with "Mark all as read" capability.

6. **Role-Based Access Control (RBAC):**
   - **Store Manager:** Stock level adjustments, reordering, stock creation.
   - **Procurement Officer:** Multi-stage purchase request review, approval/denial workflow.
   - **Supplier:** View assigned POs and update order shipping milestones.
   - **Admin:** Complete unrestricted access across all modules.

---

## 🛠️ Technical Stack

- **Presentation Tier (Frontend):** Modern Vanilla JS & CSS3 Light SaaS Design System (Inter font, Material Symbols, responsive sidebar, toast notifications, high-density data tables).
- **Application Tier (Backend):** Java Servlets (`AuthServlet`, `InventoryServlet`, `ProcurementServlet`, `OrderServlet`, `NotificationServlet`, `ReportServlet`) running on embedded HTTP engine with CORS support.
- **Data Tier (Database):** MySQL Database with automatic resilient fallback for seamless standalone execution.
- **Containerization:** Docker & Docker Compose (`docker-compose.yml`).

---

## 🚀 Quick Start Guide

### Option 1: Run Locally (Standalone Development)

#### 1. Compile & Start the Java Backend:
```bash
# Compile backend sources
javac -d backend/bin backend/src/main/java/com/smartinv/config/*.java backend/src/main/java/com/smartinv/model/*.java backend/src/main/java/com/smartinv/dao/*.java backend/src/main/java/com/smartinv/servlet/*.java backend/src/main/java/com/smartinv/AppServer.java

# Run backend server
java -cp backend/bin com.smartinv.AppServer
```
*Backend API will run at `http://localhost:8080/api`.*

#### 2. Start the Modern SaaS Frontend:
```bash
cd frontend
npm install
npm run dev
```
*Access the Web Application in your browser at `http://localhost:5173`.*

---

### Option 2: Run via Docker
```bash
docker-compose up --build
```
- **Frontend URL:** [http://localhost](http://localhost)
- **Java Servlets Backend API:** [http://localhost:8080/api](http://localhost:8080/api)
- **MySQL Database:** `localhost:3306` (User: `root`, Password: `root123`, Database: `smartinv_db`)

---

## 📦 How to Push to Git / GitHub

This repository is pre-configured with a clean `.gitignore` to keep out `node_modules`, build artifacts, and database files.

To push to your remote GitHub repository:

```bash
# 1. Initialize git (if not already initialized)
git init

# 2. Add files and make initial commit
git add .
git commit -m "feat: complete modern light SaaS redesign with verified Java servlets backend"

# 3. Link your GitHub repository
git remote add origin https://github.com/<your-username>/<your-repo-name>.git

# 4. Push to main branch
git branch -M main
git push -u origin main
```

---

## 📁 Repository Structure
```
Mini-Proj/
├── database/
│   ├── schema.sql            # Table definitions (Users, Inventory, Orders, Requests)
│   └── seed.sql              # Initial seed data
├── backend/
│   ├── pom.xml               # Maven configuration for Java Servlets backend
│   └── src/main/java/com/smartinv/
│       ├── config/           # DBConnection.java (JDBC connection & resilient storage)
│       ├── model/            # User, InventoryItem, PurchaseRequest, Order, Supplier
│       ├── dao/              # Data Access Objects (UserDAO, InventoryDAO, etc.)
│       ├── servlet/          # Java Servlets (AuthServlet, InventoryServlet, etc.)
│       └── AppServer.java    # Embedded HTTP Servlet Container
├── frontend/
│   ├── index.html            # Web app entry point with Inter font & Material Symbols
│   ├── package.json          # Node dependencies & Vite build setup
│   └── src/
│       ├── styles.css        # Modern Light SaaS Design System & Tokens
│       ├── services/api.js   # Dual-mode REST API client with local fallback
│       └── main.js           # Modern SaaS UI components, table views & modals
├── Dockerfile.backend        # Multi-stage container build for Java Servlets backend
├── Dockerfile.frontend       # Multi-stage container build for Frontend
├── docker-compose.yml        # Orchestration for MySQL, Java Backend & Frontend
├── .gitignore                # Comprehensive Git exclusions
└── README.md                 # Project documentation
```
