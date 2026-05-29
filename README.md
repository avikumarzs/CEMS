# 🎓 CEMS - Campus Event Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![TiDB](https://img.shields.io/badge/TiDB_Cloud-3139E0?style=for-the-badge&logo=pingcap&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)

## 📖 About the Project

**CEMS** is a robust, cloud-connected desktop application designed to streamline the planning, approval, and registration of university events. Built with a focus on enterprise-grade architecture, CEMS solves the chaos of manual campus scheduling by providing distinct, secure workflows for Students, Event Organizers, and System Administrators.

Rather than relying on a local database, CEMS is fully integrated with **TiDB Cloud**, allowing seamless, real-time data synchronization across any machine running the application.

## ✨ Key Features

### 🔐 Smart Authentication & UI
* **Chrome-Style Profile Manager:** Features an MRU (Most Recently Used) login screen that securely caches recent users as clickable avatars. Includes a dynamic, layered UI to seamlessly remove cached profiles.
* **Smart Form Toggles:** Replaces outdated dropdowns with modern segmented controls (e.g., smoothly toggling between "Student" and "Organizer" sign-ups to dynamically hide/show department fields).

### 🎭 Role-Based Access Control (RBAC)
* **System Administrator (DBA):** Has root access. Can securely approve/reject proposed events, oversee all registrations, and dynamically add or remove academic departments and venues.
* **Organizers:** Campus-wide users who can propose new events, specify venue requirements, and track live registration metrics.
* **Students:** Can browse a live feed of approved campus events, register for open seats, and manage their personal schedules.

### 🛡️ Enterprise-Grade Security & Validation
* **Transactional Integrity:** Utilizes raw JDBC Transactions (`conn.setAutoCommit(false)`) to ensure that seat-capacity math and student registrations either succeed together or roll back completely, preventing data corruption.
* **Overbooking Prevention:** Database-driven capacity checks mathematically block users from registering if a venue is full.
* **Strict Frontend Validation:** Utilizes Regular Expressions (Regex) to validate email formats, enforce strict ID structures (e.g., `E001`), and block illogical inputs (like scheduling events in the past).

## 🛠️ Tech Stack

* **Frontend:** Java Swing (AWT, CardLayout, GridBagLayout)
* **Backend:** Core Java (JDK 17+)
* **Database:** TiDB Serverless (MySQL 8.0 Compatible)
* **Architecture:** DAO (Data Access Object) Design Pattern
* **Connectivity:** JDBC (Java Database Connectivity)

## 🗄️ Database Architecture

The system utilizes a strictly normalized **3NF Relational Database Schema**:
* `User` (Handles Students, Organizers, and Admins)
* `Department` (Dynamically linked to Students)
* `Venue` (Tracks physical locations and maximum capacity)
* `Event` (Tracks status: Pending, Approved, Completed)
* `Registration` (The associative entity bridging Students and Events)

## 🚀 Installation & Setup

### Prerequisites
* **Java Development Kit (JDK):** Version 11 or higher.
* **MySQL Connector/J:** Required for database connectivity.
* **IDE:** IntelliJ IDEA, Eclipse, or VS Code.

### Running Locally
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/yourusername/CEMS-Campus-Events.git](https://github.com/yourusername/CEMS-Campus-Events.git)
   ```
2. **Add Dependencies:**
   * Download the `mysql-connector-j.jar` file.
   * Add it to your project's build path/dependencies in your IDE.
3. **Configure Database:**
   * Create a free cluster on [TiDB Cloud](https://tidbcloud.com/).
   * Execute the provided `database_schema.sql` script via MySQL Workbench to build the tables.
4. **Update Connection String:**
   * Navigate to `src/utils/DatabaseConnection.java`.
   * Inject your TiDB Host, Username, and Password.
5. **Compile and Run:**
   * Execute `Main.java` to launch the application.

## 📸 Screenshots

*(Note: Add your actual screenshots here once you upload them to your repo!)*

| Login Profiles | Student Dashboard | Admin Approval Panel |
| :---: | :---: | :---: |
| <img src="link_to_image" width="250"/> | <img src="link_to_image" width="250"/> | <img src="link_to_image" width="250"/> |

## 🛣️ Future Scope (Roadmap)

- [ ] **Phase 2:** Migrate from 2-Tier architecture to a 3-Tier architecture by wrapping the database in a **Node.js (Express)** or **Spring Boot** Web API.
- [ ] **Phase 3:** Introduce JWT (JSON Web Tokens) for stateless authentication.
- [ ] **Phase 4:** Develop a companion Android application for students using Kotlin.

---
*Conceptualized and developed as an enterprise architecture showcase.*
