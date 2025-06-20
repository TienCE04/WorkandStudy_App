# Ứng dụng Quản lý Công việc & Học tập

Ứng dụng hỗ trợ sinh viên trong việc:
- Quản lý công việc hằng ngày (todo list, habit)
- Xem và chỉnh sửa thời khóa biểu
- Tải lên, lưu trữ và quản lý tài liệu học tập
- Truy cập các tiện ích như ghi chú, máy tính, v.v.
- Cập nhật hồ sơ cá nhân và cài đặt

---

## Tính năng chính

- ✅ Đăng nhập / Đăng ký tài khoản
- 📅 Quản lý thời khóa biểu
- 📝 Todo list và Habit tracker
- 📂 Tài liệu học tập (upload, xem, xóa)
- 🧰 Tiện ích học tập
- 👤 Hồ sơ cá nhân
- ⚙️ Cài đặt

---

## Kiến trúc & Công nghệ

- **Kiến trúc:** MVVM 
- **Ngôn ngữ:** Kotlin
- **Cơ sở dữ liệu:** SQLite / Room
- **Realtime & Auth:** Firebase Authentication, Firebase Firestore
- **Luồng dữ liệu:** LiveData
- **Thư viện phụ trợ:** Coroutines, WorkManager

---

## Cấu trúc thư mục

| Thư mục               | Mô tả                                      |
|-----------------------|---------------------------------------------|
| `login`               | Màn hình đăng nhập                         |
| `signup`              | Màn hình đăng ký                           |
| `main`                | Giao diện chính (navigation, tổng hợp)     |
| `profile`             | Hồ sơ người dùng                           |
| `school_schedule`     | Thời khóa biểu                             |
| `data_school_schedule`| Dữ liệu và xử lý thời khóa biểu            |
| `todolist`            | Quản lý công việc & thói quen              |
| `document`            | Tải lên, quản lý tài liệu học tập          |
| `tienich`             | Tiện ích (ghi chú, máy tính,...)           |
| `settting`            | Cài đặt ứng dụng *(gợi ý sửa: `settting` → `setting`)* |

---

## ⚙️ Cài đặt & chạy thử

1. Clone repository:
    ```bash
    git clone https://github.com/your-username/your-repo.git
    cd your-repo
    ```

2. Mở bằng Android Studio.

3. Sync Gradle & thêm Firebase:
    - Tạo Project Firebase tại https://console.firebase.google.com/
    - Thêm file `google-services.json` vào thư mục `app/`

---

## 📌 Hướng phát triển tiếp theo

- [ ] Chuẩn hóa Clean Architecture
- [ ] Hỗ trợ đồng bộ hóa đa thiết bị
- [ ] Tối ưu UI/UX & hiệu năng
- [ ] Thêm giao diện Dark/Light Theme

