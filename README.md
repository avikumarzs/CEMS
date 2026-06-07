# 🎓 CEMS - Campus Event Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![TiDB](https://img.shields.io/badge/TiDB_Cloud-3139E0?style=for-the-badge&logo=pingcap&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)

## 📖 About the Project

**CEMS** is a robust, cloud-connected application designed to streamline the planning, approval, and registration of university events. Built with a focus on enterprise-grade architecture, CEMS solves the chaos of manual campus scheduling by providing distinct, secure workflows for Students, Event Organizers, and System Administrators.

This project operates on a **complete 3-tier distributed architecture**, utilizing a Java desktop client that communicates via a REST API to a cloud-hosted Spring Boot server, fully integrated with a normalized TiDB Cloud database.

## ✨ Key Features

### 🔐 Smart Authentication & UI
* **Chrome-Style Profile Manager:** Features an MRU (Most Recently Used) login screen that securely caches recent users as clickable avatars. Includes a dynamic, layered UI to seamlessly remove cached profiles.
* **Smart Form Toggles:** Replaces outdated dropdowns with modern segmented controls (e.g., smoothly toggling between "Student" and "Organizer" sign-ups to dynamically hide/show department fields).

### 🎭 Role-Based Access Control (RBAC)
* **System Administrator (DBA):** Has root access. Can securely approve/reject proposed events, oversee all registrations, and dynamically add or remove academic departments and venues.
* **Organizers:** Campus-wide users who can propose new events, specify venue requirements, and track live registration metrics.
* **Students:** Can browse a live feed of approved campus events, register for open seats, and manage their personal schedules.

### 🛡️ Enterprise-Grade Security & Validation
* **Cloud-Synced Integrity:** All transactions are handled securely by the Spring Boot REST API, ensuring that seat-capacity math and student registrations never result in data corruption or overbooking.
* **Strict Validation:** Utilizes Regular Expressions (Regex) to validate email formats, enforce strict ID structures, and block illogical inputs.

## 🛠️ Tech Stack

* **Frontend Client:** Java Swing (AWT, CardLayout, GridBagLayout)
* **Backend API:** Spring Boot 3 (Java 21, REST APIs, Dockerized)
* **Database:** TiDB Serverless (MySQL 8.0 Compatible)
* **Cloud Infrastructure:** Render (API Hosting) & GitHub Releases (Client Distribution)

## 🚀 Quick Start (No Setup Required)

You do not need to build this project from source or configure a local database to test it. The backend API is fully deployed in the cloud.

1. **Download the Client:** [Click here to download the latest `.jar` file](LINK_TO_YOUR_GITHUB_RELEASE_HERE)
2. **Prerequisites:** Ensure **Java 17+** is installed on your system.
3. **Run the Application:**

Open your terminal and execute the following command:
```bash
java -jar CEMS-Client-v1.jar
```
*(Alternatively, double-click the `.jar` file if your OS has Java associated with executable JARs).*

## 🗄️ Database Architecture

The system utilizes a strictly normalized **3NF Relational Database Schema**:
* `User` (Handles Students, Organizers, and Admins)
* `Department` (Dynamically linked to Students)
* `Venue` (Tracks physical locations and maximum capacity)
* `Event` (Tracks status: Pending, Approved, Completed)
* `Registration` (The associative entity bridging Students and Events)

## 🛣️ Future Scope (Roadmap)

- [ ] **Phase 2:** Introduce JWT (JSON Web Tokens) for stateless authentication between the Swing client and Spring Boot API.
- [ ] **Phase 3:** Develop a companion Android application for students using Kotlin, integrating with the existing backend.
- [ ] **Phase 4:** Implement an AI-driven smart recommendation engine to suggest events to students based on their department and registration history.

---
*Conceptualized and developed as an enterprise architecture showcase.*
