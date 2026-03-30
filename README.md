# Decentralized FIR System

A modern, role-based First Information Report (FIR) system simulating an immutable blockchain ledger using Spring Boot and AngularJS.

## Features
- **Frontend:** Premium UI using Bootstrap 5, glassmorphism CSS, and AngularJS.
- **Backend:** Spring Boot REST APIs with JWT authentication and Spring Security.
- **Role-based Access:** Differentiates between regular citizens and police administrators.
- **Interactive Map:** Leaflet integration to show nearby police stations.
- **Immutable Ledger simulation:** Backend architecture pre-integrated with Web3J for future Ethereum smart-contact connectivity, currently using a mocked configuration bean for immediate testing.

## Prerequisites
- Java 17+
- Maven
- A simple local server to serve the frontend (e.g., `npx serve`, Python `http.server`, or VSCode Live Server).

## How to Run

### 1. Run the Spring Boot Backend
Navigate into the backend directory and run the Maven wrapper:
```bash
cd backend
./mvnw spring-boot:run
```
*(The backend defaults to port 8080 and uses an in-memory H2 database. Default roles are auto-initialized).*

### 2. Run the AngularJS Frontend
You can serve the `frontend` folder using any static file server. 

**Using Node (npx):**
```bash
cd frontend
npx serve -p 3000
```
Open your browser at `http://localhost:3000` to start exploring the app.

## Testing Setup
1. Register a new user on the frontend and leave the "Admin" box unchecked.
2. Register a second user and check the "Register as Police Admin" box.
3. Log in as the user -> File an FIR -> View the interactive map.
4. Log out -> Log in as the admin -> View all FIRs -> Change case statuses.
