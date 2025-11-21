## Welcome 👋

Thanks for taking the time to work through this exercise!

We don’t expect perfection or a “finished product” in the short time you have. The goal is simply to see how you think, 
how you structure code, and how you approach a realistic problem. It’s completely okay if you don’t get through everything.

A few things to keep in mind while you work:

- You’re encouraged to make reasonable assumptions if something isn’t fully specified.
- There isn’t one “right” solution — we’re more interested in your reasoning than in a specific pattern or framework.
- Feel free to leave comments or notes in the code if you’d like to explain trade-offs or what you’d do with more time.

Above all, relax and have fun with it. Treat this as a chance to show how you naturally work on a small but real-world backend feature rather than an exam.

---
## 🚀 Setup Instructions

This project contains two separate applications:

- **Backend** — Spring Boot REST API
- **Frontend** — React + TypeScript (Vite)

You must run **both** for the application to work.

---

## 📦 Setup

### **Requirements**
BE
- JDK **25**
- Maven (or use the included Maven wrapper `mvnw`)
- Port **8080** must be free

FE
- NPM (or Yarn)
- Port 5173 must be available (default for Vite)

### **Steps**

BE
1. Navigate to the backend directory:

   ```bash
   cd backend
   ```
2. Start the Spring Boot application:

   ```bash
   ./mvnw spring-boot:run
   ```
FE
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```
   npm run dev
   ```
H2 UI (Optional)
1. Once backend is running, navigate to
   ```text
   http://localhost:8080/h2-console/
   ```
2. For **JDBC URL** enter
   ```text
   jdbc:h2:file:./data/appdb
   ```

---

## Project Overview

This project is a **Task Manager REST API** built with **Spring Boot** and an **H2 in-memory database**.  
It lets you create, read, update, and delete “tasks” via HTTP endpoints.

### Tech Stack

- **Java (Spring Boot 3)**
- **Spring Web** (REST API)
- **Spring Data JPA** (database access)
- **H2 Database** (in-memory DB for local development)
- **Lombok** (optional, for boilerplate reduction)

---

## Current API Overview

The backend exposes a simple REST API for managing tasks under the base path:

```text
GET /api/tasks
Returns a list of all tasks.

GET /api/tasks/{id}
Returns a single task by its ID (or 404 if not found).

POST /api/tasks
Creates a new task from the JSON payload and returns the created task.

PUT /api/tasks/{id}
Updates an existing task with the given ID using the JSON payload.

DELETE /api/tasks/{id}
Deletes the task with the given ID (returns 204 on success).
```
There is also:

- `TaskStatus` – enum for the task status
- `TaskRepository` – Spring Data JPA repository for `Task`
- A basic service & controller layer with CRUD operations
