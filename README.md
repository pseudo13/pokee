# Pokee

A small Pokémon collection app built with **Angular** and **Spring Boot**.

![Pokee Screenshot](docs/screenshot.png)

## Features

- 🔎 Pokémon search and details
- 🔐 JWT authentication
- 👤 Personal Pokémon collection
- ⭐ Rating and favorites
- 📝 Notes with editing
- 🗑️ Remove collection items
- ⚡ Caffeine caching
- 💾 SQLite persistence

## Tech Stack

**Frontend:** Angular · TypeScript · Signals · Reactive Forms · Tailwind CSS

**Backend:** Java 21 · Spring Boot · Spring Security · JWT · JPA · SQLite · Caffeine · Maven

**External API:** [PokéAPI](https://pokeapi.co/)

## Architecture

```text
┌─────────────────────────────┐
│          Angular            │
│                             │
│  Search │ Details │ Login   │
│  Collection │ Edit          │
└──────────────┬──────────────┘
               │
          REST + JWT
               │
               ▼
┌─────────────────────────────┐
│        Spring Boot          │
│                             │
│  Controllers               │
│       │                     │
│    Services                │
│       │                     │
│  ┌────┴──────────────┐     │
│  │                   │     │
│  ▼                   ▼     │
│ SQLite            Caffeine │
│                       │     │
│                       ▼     │
│                    PokéAPI  │
└─────────────────────────────┘
```

### Backend Responsibilities

- **Controllers** expose the REST API.
- **Services** contain business logic.
- **Spring Security + JWT** handle authentication and authorization.
- **SQLite + JPA** persist users, Pokémon and collection data.
- **Caffeine** caches the Pokémon catalog used for search.
- **PokéAPI** provides external Pokémon data when it is not available locally.

### Frontend Responsibilities

- Angular handles the UI and routing.
- Signals manage local application state.
- Reactive Forms handle login, registration and collection editing.
- An HTTP interceptor adds the JWT to authenticated requests.
- The frontend communicates only with the Spring Boot API.

### Collection Flow

```text
User
 │
 ▼
Angular
 │  POST /api/me/collection
 ▼
Spring Security
 │  identifies user from JWT
 ▼
CollectionService
 │
 ├── validates Pokémon
 ├── checks duplicate
 └── stores user-specific data
       │
       ▼
    SQLite
```

Each user has their own collection. The client does not provide a user ID; the backend determines the current user from the authenticated JWT.

## Requirements

- Java 21
- Node.js 20+
- PowerShell
- Git (for cloning repos — required on first run)

> [!IMPORTANT]  
> **Clone the backend and frontend repos before running.** This script does not contain those directories; it expects `backend/` and `frontend/` as sibling folders. See [First Time Setup](#first-time-setup).

## First Time Setup

Before running the app, clone the backend and frontend repositories as sibling folders alongside `run.ps1`:

```powershell
git clone https://github.com/pseudo13/backend.git
git clone https://github.com/pseudo13/frontend.git
```

This is a **one-time step** — after that, `run.ps1` will fetch the latest changes automatically.

## Clone

Each component lives in its own directory. Clone the repo here, or just run `run.ps1` and it will do everything for you.

```text
Pokee/
├── backend/    ← https://github.com/pseudo13/backend  (clone into this directory)
├── frontend/   ← https://github.com/pseudo13/frontend (clone into this directory)
└── run.ps1
```

## Run

If you cloned manually, from the project root:

```powershell
.\run.ps1
```

The script:

1. Checks that **Java 21, Node.js 20+ and npm** are installed.
2. Verifies the **backend and frontend** repos are present (clones them if missing, fetches otherwise).
3. Installs frontend dependencies if `node_modules` does not exist.
4. Starts the **Spring Boot backend**.
5. Starts the **Angular frontend**.
6. Waits for the backend to be ready.
7. Opens the application in your default browser.

After startup:

- **Frontend:** http://localhost:4200
- **Backend:** http://localhost:8088

## Manual Start

**Backend**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**Frontend**

```powershell
cd frontend
npm install
npm start
```

## Project Structure

```text
Pokee/
├── backend/
│   └── src/main/java/com/example/pokemon/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── security/
│       └── external/
│
├── frontend/
│   └── src/app/
│       ├── core/
│       ├── models/
│       └── pages/
│           ├── login/
│           ├── register/
│           ├── pokemon-list/
│           ├── pokemon-detail/
│           └── collection/
│
├── docs/
│   └── screenshot.png
├── run.ps1
└── README.md
```