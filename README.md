X (Twitter) Social Network Simulator
<div dir="rtl">

</div>

---
### 📖 Project Introduction

This project is a simulation of the **X platform (formerly Twitter)**, developed as part of the **Advanced Programming** course at the University of Isfahan. The main goal is to design and implement a desktop social network using **Java**, adhering to **Object-Oriented** principles, **MVC** architecture, and leveraging advanced concepts such as **network communication**, **concurrent programming**, and **database management**.

The project is divided into two phases:
- **Phase 1:** Implementation of core entities, main logic, user operations, and basic UI.
- **Phase 2:** Adding advanced features including error handling, multithreading, client-server networking, MySQL database, interfaces, and generic classes.

---

### ✨ Key Features

#### User Management
- Registration and login with validation (email, phone number, password)
- Three user types: **Normal**, **Blue**, and **Golden**
- Premium subscription purchase and token-based credit system
- Follow/unfollow users
- View personal and public profiles
- Display followers/following lists

#### Post Management
- Create, edit, and delete posts
- Attach images or videos to posts
- Like posts
- Reply to posts
- View timeline (suggested posts and latest posts from followed users)
- Hashtag support and search by hashtag, post text, or username
- Filter posts by date (bonus)
- Sort posts by popularity (likes) and views
- Report inappropriate posts

#### Private Messaging System (Phase 2)
- Real-time sending and receiving messages
- Persistent storage in database
- Message history synchronization after reconnection
- Online/offline user status display
- Delivery of messages sent while offline
- Bonus features: Typing indicator, message encryption, delete for everyone, online users list in chat

#### Report Management (Admin Only)
- Submit reports for posts or users
- Review and manage reports (confirm, reject, block)

#### Database (Phase 2)
- **MySQL** and **JDBC**
- **Repository Pattern** with generic `IRepository<T>` interface
- Stores users, posts, messages, hashtags, reports

#### Network Communication (Phase 2)
- **Client-Server** architecture using **Socket** and **Serialization**
- Send/receive `NetworkPacket` objects
- Handle multiple clients concurrently using **Thread Pool**

#### Concurrent Programming (Phase 2)
- Prevent UI freezing with `Thread` and `ExecutorService`
- Background processing for heavy operations (e.g., media upload)
- Safe thread termination

#### Error Handling (Phase 2)
- Custom **Exceptions** (e.g., `AuthenticationException`, `UserNotFoundException`, `NotEnoughBalanceException`, etc.)
- User-friendly error messages instead of crashes

---

### 🧱 Project Structure and Architecture

This project follows the **MVC (Model-View-Controller)** architecture.

#### Suggested Package Structure
├── model // Entities (User, Post, Hashtag, Report, Message, ...)
├── view // UI (JavaFX or Swing)
├── controller // Controllers and business logic
├── repository // Data access layer (IRepository implementations)
├── network // Network communication (ClientHandler, NetworkPacket, ...)
├── exception // Custom exception classes
└── util // Utilities (validation, scheduling, ...)

text

#### Core Entities (Phase 1)
- **User** (Normal, Blue, Golden) – with inheritance and polymorphism
- **Post** – includes text, media attachment, hashtags, likes, replies, views, lock/delete status
- **File, Photo, Video** – multimedia file management
- **Hashtag** – unique ID and list of posts
- **Report** – reporter, reported user/post, status (WAITING, CONFIRMED, REJECTED)
- **Database** (Phase 1) – temporary lists (replaced by MySQL in Phase 2)

---

### 🚀 How to Run

#### Prerequisites
- JDK 17 or higher
- MySQL Server (for Phase 2)
- IDE like IntelliJ IDEA or Eclipse

#### Execution Steps (Phase 2)
1. **Database:** Run SQL scripts to create tables (Users, Messages, Posts, Hashtags, Reports, Follows, etc.) in MySQL.
2. **Server:** Run the `ChatServer` class to start listening for clients.
3. **Client:** Run the client application (UI). Enter username and password to connect to the server and access the user panel.
4. **Operations:** Use menus and buttons in the UI to create posts, follow users, send messages, and perform other actions.

---

### 🛠 Technologies & Tools

| Component | Technology |
|-----------|------------|
| Language | Java (JDK 17) |
| UI Framework | JavaFX or Swing (your choice) |
| Database | MySQL + JDBC |
| Networking | Socket, Object Serialization |
| Concurrency | ExecutorService (Thread Pool) |
| Error Handling | Custom Exceptions |
| Version Control | Git + GitHub |

---

### 📌 Implementation Notes (from project documentation)

- Following **OOP principles** (encapsulation, inheritance, polymorphism) directly affects your grade.
- **MVC architecture** is mandatory.
- In Phase 2, the `Database` class from Phase 1 is **completely removed**; data is managed via Repository and MySQL.
- All network communication must go through the **`NetworkConnection`** interface.
- Use `Platform.runLater` for any UI updates from background threads.
- Use **synchronized** blocks for accessing shared resources.

---

### 👥 Development Team (Project Designers)

- Farnoush Izadbar
- Amirhossein Karimi Zarchi
- Erfan Rahmat
- Ali Talebi
- Mohammad Reza Rostami
- Amirhossein Ahmadi Fard

---

### 📄 License

This project is developed solely for educational purposes as part of the Advanced Programming course at the University of Isfahan.
