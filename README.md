# On-Map Real Estate Transactions

Spring Boot backend + React (Vite) frontend with a transactions generator that publishes a deal every 5 seconds. Data is stored in Postgres and exposed via REST for the frontend map.

## Architecture
- **Backend (Spring Boot)**: `POST /api/transactions` to ingest, `GET /api/transactions/latest?limit=50` to read.
- **Generator (separate service)**: Spring Boot app that posts a new transaction every 5s with WebClient + retry/backoff. Runs as its own container (`generator`).
- **SSE stream**: `GET /api/transactions/stream` (text/event-stream) pushes new transactions to clients.
- **Frontend (React + MapLibre + OSM tiles)**: Auto-refreshes the latest transactions and plots them on a keyless MapLibre map (OpenStreetMap tiles).
- **Database**: Postgres.
- **MCP**: Already enabled for filesystem demo (can be disabled if unneeded).
 - **Migrations**: Flyway (`db/migration`) manages schema + indexes.

## Local development
Copy `.env.example` to `.env` to configure shared values (Postgres credentials, CORS origins, API base) before running anything locally or with Docker. The map is keyless (MapLibre + OSM).

### Backend
```bash
./mvnw spring-boot:run
```
Profiles:
- Default: Postgres (`spring.datasource.*` in `application.properties`) — set env vars for your DB.
- `dev`: in-memory H2 with seeded data (no external DB): `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`
  - CORS origins are controlled via `APP_CORS_ALLOWED_ORIGINS`/`app.cors.allowed-origins` (defaults to `http://localhost:5173,http://localhost:4173`).

Generator:
```bash
transactions.generator.enabled=true \
GENERATOR_TARGET_URL=http://localhost:8080/api/transactions \
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev   # http://localhost:5173, proxies /api -> http://localhost:8080
```
Create `frontend/.env` from `frontend/.env.example` (or reuse root `.env`). Adjust `VITE_API_BASE` if the backend is not on the same origin.

## Docker
```bash
docker compose up --build
# backend: http://localhost:8080
# frontend: http://localhost:5173
# generator: separate container posting every 5s
```

The compose file reads from `.env` for database credentials and API base URL. A Postgres health check gates backend startup to avoid race conditions. MapLibre uses OpenStreetMap tiles (no API key needed). For heavy traffic, point MapLibre to your own/hosted tile source.

## Configuration & secrets
- Copy `src/main/resources/application-example.properties` and set values via environment variables (`SPRING_DATASOURCE_*`, `OPENAI_API_KEY`, etc.). Avoid committing real secrets.
- Seeding and generator are opt-in; enable via `transactions.seed.enabled=true` or `transactions.generator.enabled=true` when needed.
- Disable MCP if unused: `spring.ai.mcp.client.enabled=false`.

## Testing
- Backend unit/integration: `./mvnw test` (uses H2 + Flyway, generator/seed disabled, MCP disabled).
- Frontend: add Vite/React testing as needed (Playwright/Cypress for E2E recommended).

## API
- `POST /api/transactions` — body:
```json
{
  "id": "uuid-optional",
  "city": "Riyadh",
  "cityCode": "011",
  "latitude": 24.71,
  "longitude": 46.67,
  "time": "2025-01-01T12:00:00Z",
  "price": 900000,
  "type": "VILLA"
}
```
- `GET /api/transactions/latest?limit=50`
- `GET /api/transactions/stream` — SSE stream of new transactions

## Branching
Follow standard flow: feature branches off `dev`, PR into `dev` (never into `main`), squash/merge with reviews. Commit every adjusted file with clear messages. Frontend and backend live in the same repo.
