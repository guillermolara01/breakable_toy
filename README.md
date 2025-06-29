# Product Inventory System

A full-stack inventory management system built with **Spring Boot** on the backend and **React + TypeScript** on the frontend.

## Features 💻
 
### Backend (Spring Boot) ☕️
- RESTful API for product and category management
- In-memory storage
- Pagination, filtering, and multi-column sorting
- Custom metrics (Stock totals, Value by category, Average Price)
- Layered architecture (Controller → Service → Repository)
- Unit tested with JUnit + Mockito
- API Docs with Swagger
  
### Frontend (React + TypeScript) ⚛️
- MUI components and theming (light/dark mode)
- Product creation, editing, deletion
- Multi-category selection with chip UI
- Filtering by name, category, and stock status
- Color-coded rows by expiration and stock level
- Theme toggling (blue-purple palette)
- Snackbar alerts, spinners, modals, tooltips
- Jest tests.

---

## How To Run run 💪 
### Backend

#### Prerequisites
- Java 17+
- Maven 3+

#### Installing Dependencies
```bash
 mvn install
```

#### Run the backend
##### You might need to go to the backend folder
```bash
cd backend
```
##### Then run the app
```bash
mvn spring-boot:run
```
##### Backend Runs on port **9090**.

##### Swagger API Docs
#### Can be accessed on:
#### `http://localhost:9090/api/swagger-ui/index.html#/`

#### (The backend app needs to be running)

#### Test the backend

```bash
mvn test
```
---
### Frontend

#### Prerequisites
- npm
- Node JS 20+

#### Running the frontend
##### Moving to the frontend folder
```bash
cd frontend
```
- Install npm dependencies
```bash
npm install
```
- Run the App
```bash
npm run dev
```
##### The frontend runs on port **8080**

#### Testing the Frontend
```bash
npm run test
```
---
