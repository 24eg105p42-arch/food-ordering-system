# Online Food Ordering System

A full-stack, multi-role food ordering platform.

- **Backend**: Spring Boot 3.5 (Java 21), Spring Security + JWT, Spring Data JPA, MySQL
- **Frontend**: React 18 (Vite), React Router, Axios
- **Roles**: Customer, Restaurant Admin, Super Admin

See `DEPLOYMENT.md` for how to run this locally and deploy it for free.

## Folder structure

```
food-ordering-system/
├── backend/        Spring Boot REST API
├── frontend/        React app (Vite)
└── database/        schema.sql (reference — Hibernate creates these tables automatically)
```

## Run it locally (quick version)

### 1. Backend
```bash
cd backend
# Edit src/main/resources/application.properties and set your MySQL password
mvn spring-boot:run
```
Runs on **http://localhost:8080**. Swagger UI: http://localhost:8080/swagger-ui.html

### 2. Frontend
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```
Runs on **http://localhost:5173**

## How the three roles work

1. **Register** as a Customer or a Restaurant Admin at `/register`.
2. A **Restaurant Admin** logs in, creates their restaurant profile, and adds menu items — but the restaurant is **not visible to customers** until a Super Admin approves it.
3. A **Super Admin** account is NOT created through the signup form (on purpose — anyone could become one otherwise). Create it directly in MySQL:
   ```sql
   -- First register a normal account through the UI, then run:
   UPDATE users SET role = 'SUPER_ADMIN' WHERE email = 'you@example.com';
   ```
4. Log back in as that user — you now see the Super Admin menu (Restaurants, Categories, Users, Stats).
5. Approve the restaurant, add a few categories (e.g. "Pizza", "Desserts") so restaurant admins can categorize their menu items.
6. Now customers can browse, order, and track delivery; the restaurant admin accepts orders and updates status; the super admin can see all orders and revenue.

## API overview

| Area | Base path | Who can call it |
|---|---|---|
| Auth | `/api/auth/**` | Anyone |
| Browsing | `/api/public/**` | Anyone (GET only) |
| Customer | `/api/customer/**` | Logged-in customers |
| Restaurant Admin | `/api/restaurant-admin/**` | Logged-in restaurant admins |
| Super Admin | `/api/admin/**` | Logged-in super admins |

Every protected request needs the header: `Authorization: Bearer <token>` (the token comes back from `/api/auth/login`).

## What's included vs. what you can add next

Included: registration/login, browse/search, cart, checkout with address + payment method, the full order lifecycle (placed → accepted → preparing → out for delivery → delivered), order cancellation, reviews, restaurant menu management, restaurant approval workflow, Super Admin user/restaurant/category management and revenue stats.

Natural next steps: image upload (currently just URL fields), real payment gateway integration (Razorpay/Stripe — currently a placeholder), email notifications, pagination on long lists, automated tests.
