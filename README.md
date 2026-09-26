# 📦 StockSense — Inventory Management System

> A hackathon-grade full-stack inventory management system built with **Java + Spring Boot (Spring Web + Spring Data JPA) + H2 File-Based Database + Vanilla Frontend**.  
> **Core Architecture:** **Append-Only Transaction Ledger** — stock is never stored as an editable field on the product; it is always dynamically calculated by summing transactions in the ledger.

---

## 🏗️ Project Architecture & Layout

```
Stock_sense_odoo/
├── pom.xml                                    ← Maven Project Configuration (Java 17+, Spring Boot 3.3.4)
├── src/
│   ├── main/
│   │   ├── java/com/stocksense/
│   │   │   ├── StockSenseApplication.java     ← Main Spring Boot Application Entry Point
│   │   │   ├── config/
│   │   │   │   ├── DataInitializer.java       ← Auto-seeds "Main Warehouse" location on boot
│   │   │   │   └── WebConfig.java             ← CORS & static resource mapping for frontend
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java     ← GET /products, POST /products
│   │   │   │   ├── StockMoveController.java   ← POST /moves/receipt, POST /moves/delivery
│   │   │   │   ├── DashboardController.java   ← GET /dashboard, GET /health
│   │   │   │   └── GlobalExceptionHandler.java← Standardized error JSON with clear detail messages
│   │   │   ├── dto/                           ← Request & response DTOs with validation
│   │   │   ├── exception/                     ← Custom domain exceptions (404, 409, 422)
│   │   │   ├── model/                         ← JPA Entities: Product, Location, StockMove, MoveType
│   │   │   ├── repository/                    ← Spring Data JPA Repositories with ledger sum queries
│   │   │   └── service/                       ← Business logic & stock validation services
│   │   └── resources/
│   │       └── application.properties         ← H2 file-based DB, H2 console, Snake_case JSON
│   └── test/java/com/stocksense/
│       ├── StockSenseApplicationTests.java    ← Context load sanity test
│       └── StockSenseIntegrationTests.java    ← Full end-to-end integration test suite
├── frontend/                                  ← Plain HTML, CSS, Vanilla JS (no frameworks)
│   ├── index.html                             ← Real-time Dashboard with Low-Stock Alerts
│   ├── products.html                          ← Product Catalogue & Creation Form
│   ├── receipt.html                           ← Stock Inward Receipt Form
│   ├── delivery.html                          ← Stock Outward Delivery Form with live stock preview
│   ├── style.css                              ← Modern Indigo Design System
│   ├── api.js                                 ← Centralized fetch API client (auto-detects port 8080)
│   └── utils.js                               ← Shared UI toast, alerts, formatters, and badges
├── data/                                      ← H2 persistent database files (stocksense.mv.db)
├── .gitignore
└── README.md
```

---

## ⚡ Quick Start Instructions

### 1. Prerequisites
- **Java 17 or Java 21** installed (`java -version`)
- **Apache Maven 3.8+** installed (`mvn -v`)

### 2. Run the Backend Server

You can start the backend directly using Maven:

```bash
mvn spring-boot:run
```

*Or*, build the standalone executable JAR and run it:

```bash
mvn clean package -DskipTests
java -jar target/stocksense-1.0.0.jar
```

The Spring Boot application will start and listen on port **8080**.

### 3. Open the Frontend

Once the server is running, open your web browser to:

👉 **[http://localhost:8080](http://localhost:8080)**

The Spring Boot server automatically serves the frontend pages directly! You can also navigate between:
- **Dashboard:** `http://localhost:8080/index.html`
- **Products:** `http://localhost:8080/products.html`
- **Stock Receipt:** `http://localhost:8080/receipt.html`
- **Stock Delivery:** `http://localhost:8080/delivery.html`

### 4. Interactive H2 Web Console (Optional)

You can inspect the live file-based H2 database directly in your browser:
- **URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL:** `jdbc:h2:file:./data/stocksense`
- **User Name:** `sa`
- **Password:** *(leave blank)*

---

## 🧪 Running Automated Tests

A full integration test suite is included in `StockSenseIntegrationTests.java`. To run all tests:

```bash
mvn test
```

This verifies:
1. Product creation with unique SKU enforcement.
2. Initial stock starts at 0 without any manual edit.
3. Positive ledger entries via receipts correctly increase calculated stock.
4. Negative ledger entries via deliveries correctly reduce calculated stock.
5. Deliveries that exceed available stock are strictly rejected with HTTP 422 and a clear error message.
6. The dashboard detects items with stock `< 10` and raises real-time low-stock alerts.

---

## 🧠 Core Design Principle — The Append-Only Ledger

In traditional inventory software, a `quantity` field is often stored directly on the `Product` table and modified with `UPDATE products SET quantity = quantity - 5`. This creates race conditions, audit gaps, and historical data loss.

**StockSense follows an append-only ledger pattern:**
1. The `Product` table has **no editable stock quantity column**.
2. Every physical event is recorded as an immutable row in the `stock_moves` table:
   - **Receipt (+qty)**: goods arriving into the warehouse.
   - **Delivery (-qty)**: goods shipped to customers (pre-validated against current balance).
3. Current stock for any product is **always calculated** on-demand via the ledger query:
   ```sql
   SELECT COALESCE(SUM(quantity_change), 0) 
   FROM stock_moves 
   WHERE product_id = ?
   ```
4. This guarantees a 100% auditable history, zero ledger drift, and complete consistency.

---

## 🔌 REST API Endpoints

| Method | Endpoint | Request Body | Description |
|---|---|---|---|
| `GET` | `/health` | — | Health check |
| `GET` | `/products` | — | Returns all products with computed current stock |
| `POST` | `/products` | `{"name","sku","category","unit_of_measure"}` | Creates a new product (validates SKU uniqueness) |
| `POST` | `/moves/receipt` | `{"product_id", "quantity", "note"}` | Appends a positive `StockMove` (`move_type: "receipt"`) |
| `POST` | `/moves/delivery` | `{"product_id", "quantity", "note"}` | Pre-validates available stock; appends negative `StockMove` (`move_type: "delivery"`) or rejects with 422 |
| `GET` | `/dashboard` | — | Returns total product count, low-stock threshold (10), and low-stock items |

---

## 🛡️ Validation & Error Handling

- **Frontend Validation:**
  - Prevents form submission if required fields are blank.
  - Rejects zero, decimal, or negative inputs; only positive whole numbers allowed.
  - Interactive visual gauges warn users before submitting an order if quantity exceeds stock.
- **Backend Validation:**
  - `@Valid` annotations enforce positive quantities (`@Min(1)`).
  - Duplicate SKUs return `409 Conflict`.
  - Missing products return `404 Not Found`.
  - Delivery quantity exceeding available stock returns `422 Unprocessable Entity` with a clear message:  
    `"Insufficient stock for '<Product>'. Available: <X>, Requested: <Y>."`
