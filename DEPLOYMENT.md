# Deployment Guide

Two parts: running it on your own computer (you've mostly done this already),
then putting it online so anyone can use it. For going online we'll use:

- **Railway** — hosts the Spring Boot backend AND the MySQL database (free trial credit, no card needed to start)
- **Vercel** — hosts the React frontend (free, forever, for projects like this)

Both have generous free tiers for a student/portfolio project like this one.

---

## Part A: Run it locally (recap)

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Open http://localhost:5173. Register a customer and a restaurant admin account to try the flow end to end.

---

## Part B: Deploy online

### Step 1: Push the code to GitHub

1. Create a new repository on GitHub (e.g. `food-ordering-system`), empty, no README.
2. In your project's root folder, run:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: full-stack food ordering system"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/food-ordering-system.git
   git push -u origin main
   ```

### Step 2: Deploy the database + backend on Railway

1. Go to **railway.app** and sign up (GitHub login is easiest).
2. Click **New Project → Provision MySQL**. Railway creates a MySQL database and shows you connection details under the **Variables** tab (`MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`).
3. In the same project, click **New → GitHub Repo** and select your repository.
4. Railway will try to build the whole repo. Tell it to only build the backend:
   - Open the new service's **Settings** tab.
   - Set **Root Directory** to `backend`.
   - Build command: leave default (Railway detects Maven automatically from `pom.xml`).
5. Open the backend service's **Variables** tab and add these (Railway lets you reference the MySQL service's variables directly, or type them in):
   ```
   DB_USERNAME     = ${{MySQL.MYSQLUSER}}
   DB_PASSWORD     = ${{MySQL.MYSQLPASSWORD}}
   JWT_SECRET      = a-long-random-string-at-least-32-characters-here
   ```
6. We also need the datasource URL to point at Railway's MySQL host instead of `localhost`. Add one more variable:
   ```
   SPRING_DATASOURCE_URL = jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   ```
   (Spring Boot automatically prefers an environment variable over the one in `application.properties`, so you don't need to edit any code.)
7. Click **Deploy**. Watch the build logs. When it finishes, Railway gives you a public URL like `https://food-ordering-backend-production.up.railway.app`.
8. Test it: open `https://YOUR-RAILWAY-URL/swagger-ui.html` in a browser. If you see the Swagger page, the backend is live.

### Step 3: Deploy the frontend on Vercel

1. Go to **vercel.com** and sign up with GitHub.
2. Click **Add New → Project**, select your repository.
3. Vercel will ask for the project settings:
   - **Root Directory**: `frontend`
   - **Framework Preset**: Vite (Vercel usually detects this automatically)
4. Before deploying, add an environment variable:
   - Key: `VITE_API_BASE_URL`
   - Value: `https://YOUR-RAILWAY-URL/api` (the same backend URL from Step 2, with `/api` on the end)
5. Click **Deploy**. In about a minute, Vercel gives you a live URL like `https://food-ordering-system.vercel.app`.

### Step 4: Allow the frontend to call the backend (CORS)

Your backend only accepts requests from the origins listed in `app.cors.allowed-origins`. Add your Vercel URL to that list as a Railway environment variable (no code change needed):
```
APP_CORS_ALLOWED-ORIGINS = https://food-ordering-system.vercel.app
```
Spring Boot maps underscores/dots automatically from environment variable names, but to be safe, Railway also lets you edit `application.properties` directly through its dashboard if this doesn't pick up — or simplest, before deploying, change the line in `application.properties` to:
```properties
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000,https://food-ordering-system.vercel.app
```
and push that change to GitHub; Railway auto-redeploys on every push to `main`.

### Step 5: Try it live

1. Open your Vercel URL.
2. Register a Customer account and a Restaurant Admin account.
3. Promote yourself to Super Admin by running this against Railway's MySQL (Railway's dashboard has a **Data** tab with a built-in query console — no separate MySQL client needed):
   ```sql
   UPDATE users SET role = 'SUPER_ADMIN' WHERE email = 'you@example.com';
   ```
4. Log out and back in as that account, approve the restaurant, add categories, and place a test order.

---

## Costs and limits to know about

- **Railway**: free trial gives you a limited amount of usage credit, then it's pay-as-you-go (usually a few dollars a month for a small project like this). Good enough for a portfolio/demo link.
- **Vercel**: free forever for personal projects at this scale.
- If you'd rather pay nothing at all, you can run both backend and frontend locally and just demo it from your own laptop — that's perfectly fine for coursework or an interview demo.

## Common deployment problems

| Symptom | Likely cause |
|---|---|
| Frontend loads but API calls fail with a network error | `VITE_API_BASE_URL` is wrong, or CORS isn't allowing your Vercel domain (Step 4) |
| Backend won't start on Railway | Check the build logs — usually a missing environment variable (`DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `SPRING_DATASOURCE_URL`) |
| 401 Unauthorized on every request | Token not being sent — check browser dev tools → Network tab → request headers for `Authorization: Bearer ...` |
| "Communications link failure" | The backend can't reach MySQL — double check the datasource URL variable in Step 2.6 |
