X (Twitter) Social Network Simulator

<div dir="rtl">

## 📖 معرفی پروژه

این پروژه، شبیه‌سازی از پلتفرم **X (توییتر سابق)** است که در قالب درس **برنامه‌سازی پیشرفته** دانشگاه اصفهان پیاده‌سازی شده است. هدف اصلی، طراحی و توسعه یک شبکه اجتماعی دسکتاپ با استفاده از زبان **جاوا**، رعایت اصول **شی‌گرایی**، معماری **MVC** و بهره‌گیری از مفاهیم پیشرفته‌ای همچون **ارتباطات شبکهای**، **برنامه‌نویسی همزمان** و **پایگاه داده** است.

این پروژه در دو فاز طراحی و پیاده‌سازی شده است:
- **فاز اول:** پیاده‌سازی موجودیت‌ها، منطق اصلی، عملیات کاربران و رابط کاربری اولیه.
- **فاز دوم:** اضافه کردن قابلیت‌های پیشرفته شامل مدیریت خطاها، برنامه‌نویسی همزمان، ارتباطات شبکه (کلاینت-سرور)، پایگاه داده MySQL، استفاده از واسط‌ها و کلاس‌های جنریک.

---

## ✨ امکانات اصلی پروژه

### مدیریت کاربران
- ثبت‌نام و ورود با اعتبارسنجی (ایمیل، شماره تلفن، رمز عبور)
- سه نوع حساب کاربری: **عادی (Normal)**، **آبی (Blue)** و **طلایی (Golden)**
- خرید اشتراک پریمیوم و افزایش اعتبار (توکن)
- دنبال کردن و لغو دنبال کردن کاربران
- مشاهده پروفایل شخصی و عمومی
- نمایش لیست دنبال‌کنندگان و دنبال‌شوندگان

### مدیریت پست‌ها
- ایجاد، ویرایش و حذف پست
- افزودن تصویر یا ویدئو به پست
- لایک کردن پست‌ها
- پاسخ به پست‌ها (ریپلی)
- مشاهده تایم‌لاین (پست‌های پیشنهادی و جدیدترین پست‌های دنبال‌شوندگان)
- پشتیبانی از هشتگ‌ها و جستجو بر اساس هشتگ، متن پست و نام کاربری
- فیلتر پست‌ها بر اساس تاریخ انتشار (امتیازی)
- مرتب‌سازی پست‌ها بر اساس محبوبیت (تعداد لایک) و بازدید
- گزارش پست‌های نامناسب

### سیستم پیام‌رسانی خصوصی (فاز دوم)
- ارسال و دریافت پیام به‌صورت لحظه‌ای (Real-time)
- ذخیره دائمی پیام‌ها در پایگاه داده
- همگام‌سازی تاریخچه پیام‌ها پس از اتصال مجدد
- نمایش وضعیت آنلاین/آفلاین کاربران
- تحویل پیام‌های ارسال‌شده در زمان آفلاین بودن گیرنده
- قابلیت‌های امتیازی: نمایش «در حال تایپ» (Typing Indicator)، رمزنگاری پیام‌ها، پاک کردن پیام برای همه، و نمایش کاربران آنلاین در چت

### مدیریت گزارش‌ها (ویژه ادمین)
- ثبت گزارش برای پست‌ها یا کاربران
- بررسی و مدیریت گزارش‌ها توسط ادمین (تأیید، رد، مسدودیت)

### پایگاه داده (فاز دوم)
- استفاده از **MySQL** و **JDBC**
- پیاده‌سازی **Repository Pattern** با واسط جنریک `IRepository<T>`
- ذخیره‌سازی اطلاعات کاربران، پست‌ها، پیام‌ها، هشتگ‌ها و گزارش‌ها

### ارتباطات شبکه (فاز دوم)
- معماری **کلاینت-سرور** با استفاده از **Socket** و **Serialization**
- ارسال و دریافت بسته‌های اطلاعاتی (`NetworkPacket`)
- مدیریت همزمان چندین کلاینت با **Thread Pool**

### برنامه‌نویسی همزمان (فاز دوم)
- جلوگیری از قفل شدن رابط کاربری با استفاده از `Thread` و `ExecutorService`
- پردازش پس‌زمینه برای عملیات سنگین (مانند آپلود مدیا)
- مدیریت ایمن توقف رشته‌ها

### مدیریت خطاها (فاز دوم)
- تعریف و استفاده از **Exceptionهای اختصاصی** (مانند `AuthenticationException`، `UserNotFoundException`، `NotEnoughBalanceException` و ...)
- نمایش پیام‌های خطای کاربرپسند به جای کرش کردن برنامه

---

## 🧱 ساختار پروژه و معماری

این پروژه بر اساس معماری **MVC (Model-View-Controller)** طراحی شده است. تشخیص قرارگیری هر بخش در این معماری به عهده توسعه‌دهنده بوده است.

### پکیج‌بندی پیشنهادی
├── model // موجودیت‌ها (User, Post, Hashtag, Report, Message, ...)
├── view // رابط کاربری (JavaFX یا Swing)
├── controller // کنترلرها و منطق اصلی برنامه
├── repository // الیه دسترسی به داده (پیاده‌سازی IRepository)
├── network // ارتباطات شبکه (ClientHandler, NetworkPacket, ...)
├── exception // کلاس‌های Exception اختصاصی
└── util // ابزارهای کمکی (اعتبارسنجی، زمان‌بندی، ...)

text

### موجودیت‌های اصلی (فاز اول)
- **User** (Normal, Blue, Golden) – با قابلیت ارث‌بری و چندریختی
- **Post** – شامل متن، فایل پیوست (تصویر/ویدئو)، هشتگ‌ها، لایک‌ها، پاسخ‌ها، بازدید و وضعیت قفل/حذف
- **File, Photo, Video** – مدیریت فایل‌های چندرسانه‌ای
- **Hashtag** – با شناسه یکتا و لیست پست‌ها
- **Report** – شامل کاربر گزارش‌دهنده، کاربر/پست گزارش‌شده و وضعیت (WAITING, CONFIRMED, REJECTED)
- **Database** (فاز اول) – لیست‌های موقت (در فاز دوم با MySQL جایگزین شده است)

---

## 🚀 نحوه اجرا

### پیش‌نیازها
- JDK 17 یا بالاتر
- MySQL Server (برای فاز دوم)
- IDE مانند IntelliJ IDEA یا Eclipse

### مراحل اجرا (فاز دوم)
1. **پایگاه داده:** اسکریپت‌های ایجاد جداول (Users, Messages, Posts, Hashtags, Reports, Follows, ...) را در MySQL اجرا کنید.
2. **سرور:** کلاس `ChatServer` را اجرا کنید تا منتظر اتصال کلاینت‌ها باشد.
3. **کلاینت:** برنامه کلاینت (رابط کاربری) را اجرا کنید. با وارد کردن نام کاربری و رمز عبور، به سرور متصل شده و وارد پنل کاربری می‌شوید.
4. **عملیات:** از طریق منوها و دکمه‌های رابط کاربری، می‌توانید پست ایجاد کنید، کاربران را دنبال کنید، پیام ارسال کنید و سایر عملیات را انجام دهید.

---

## 🛠 تکنولوژی‌ها و ابزارها

| بخش | فناوری |
|------|--------|
| زبان برنامه‌نویسی | Java (JDK 17) |
| رابط کاربری | JavaFX یا Swing (انتخاب شما) |
| پایگاه داده | MySQL + JDBC |
| ارتباطات شبکه | Socket, Object Serialization |
| مدیریت همزمانی | ExecutorService (Thread Pool) |
| مدیریت خطا | Exceptionهای اختصاصی |
| کنترل نسخه | Git + GitHub |

---

## 📌 نکات پیاده‌سازی (از مستند پروژه)

- رعایت اصول **شی‌گرایی** (محصورسازی، ارث‌بری، چندریختی) در نمره تأثیر مستقیم دارد.
- استفاده از **معماری MVC** الزامی است.
- در فاز دوم، کلاس `Database` فاز اول **کاملاً کنار گذاشته** شده و اطلاعات از طریق Repository و MySQL مدیریت می‌شوند.
- تمام ارتباطات شبکه از طریق **واسط `NetworkConnection`** انجام می‌شود.
- برای تغییرات گرافیکی در تردهای فرعی، حتماً از `Platform.runLater` استفاده کنید.
- مدیریت **همگام‌سازی (synchronized)** برای دسترسی به منابع مشترک ضروری است.

---

## 👥 تیم توسعه‌دهندگان (طراحان پروژه)

- فرنوش ایزدبار
- امیرحسین کریمی زارچی
- عرفان رحمت
- علی طالبی
- محمد رضا رستمی
- امیرحسین احمدی فرد

---

## 📄 مجوز

این پروژه صرفاً با اهداف آموزشی و در چارچوب درس برنامه‌سازی پیشرفته دانشگاه اصفهان توسعه داده شده است.

</div>

---

## 🇬🇧 English Version

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
