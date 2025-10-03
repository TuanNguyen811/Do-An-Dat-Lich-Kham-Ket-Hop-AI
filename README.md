# 🩺 Ứng Dụng Đặt Lịch Khám Tích Hợp AI

> Đồ án môn Nhập môn Công nghệ Phần mềm – Học viện Công nghệ Bưu chính Viễn thông

Ứng dụng hỗ trợ bệnh nhân đặt lịch khám bệnh, theo dõi sức khỏe cá nhân và nhận tư vấn thông minh từ AI. Dự án gồm hệ thống **Android App cho bệnh nhân**, **Desktop App cho bác sĩ & Admin**, và **Backend** xử lý nghiệp vụ.
video demo: https://youtu.be/qG-ZNh1VEDI?si=BJ_4-__BBDuzU-lu
---

## 🚀 Tính Năng Chính

### 📱 Ứng dụng Android (cho bệnh nhân)
- Đăng ký, đăng nhập, quên mật khẩu (OTP qua email)
- Đặt lịch khám: chọn khoa, bác sĩ, thời gian
- Theo dõi & nhập 15 chỉ số sức khỏe
- Chatbot AI tư vấn sức khỏe
- Nhận thông báo nhắc tái khám, uống thuốc
- Quản lý thông tin cá nhân

### 💻 Ứng dụng Desktop (cho bác sĩ & admin)
- **Admin**
  - Quản lý khoa, bác sĩ, lịch làm việc
  - Tạo tài khoản bác sĩ & phân quyền
- **Bác sĩ**
  - Xem lịch khám, cập nhật chỉ số sức khỏe
  - Ghi chẩn đoán & lịch nhắc tái khám
  - Quản lý hồ sơ bệnh nhân & lịch làm việc

### 🤖 Tích Hợp AI (Gemini)
- Phân tích 15 chỉ số sức khỏe từ bệnh nhân
- Sinh prompt tự động → gửi Gemini AI
- Nhận tư vấn cá nhân hóa qua chatbot

---

## 🛠️ Kiến Trúc Hệ Thống

```text
Android App  ⇄  FastAPI Backend  ⇄  MySQL
     ⇅                            ⇅
Gemini AI                   Desktop App
```

- Kiến trúc RESTful API, mô hình Client‑Server
- Android: MVVM + Retrofit
- Desktop: Java Swing + MVC
- Backend: Python (FastAPI) + JWT Auth
- CSDL: MySQL, thiết kế ERD chuẩn hóa

---

## 🧑‍💻 Công Nghệ Sử Dụng

| Thành phần   | Công nghệ                    |
|--------------|------------------------------|
| Backend      | Python, FastAPI, JWT         |
| Database     | MySQL                        |
| Android App  | Java, MVVM, Retrofit, Glide  |
| Desktop App  | Java Swing                   |
| AI Chatbot   | Gemini API (Google AI)       |

---

## 📂 Cấu Trúc Dự Án

```bash
Do-An-Dat-Lich-Kham-Ket-Hop-AI/
│
├── backend/               # FastAPI backend
├── android-app/           # Ứng dụng Android
├── desktop-app/           # Ứng dụng Java Swing
├── database/              # Script SQL, sơ đồ ERD
├── README.md
└── ...
```

---

## ⚙️ Cài Đặt & Chạy Thử

### Backend (FastAPI)
```bash
cd backend
python -m venv venv
source venv/bin/activate   # Windows: venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --reload
```

### Android App
1. Mở bằng **Android Studio**.
2. Cập nhật địa chỉ IP server trong file cấu hình (`BaseUrl`).
3. Build APK & chạy trên thiết bị thật.

### Desktop App
1. Mở bằng **NetBeans** hoặc IDE Java bất kỳ.
2. Cập nhật hằng số `IP_BACKEND`.
3. Build & Run.

---

## 🧪 Kiểm Thử
- **Unit Test**: Auth, lịch khám, chỉ số sức khỏe.
- **Integration Test**: Token, gửi email OTP, chatbot.
- **UI Testing**: Thiết bị Android thật.
- **User Acceptance Test**: Sinh viên đóng vai trò người dùng.

---

## 🌟 Lộ Trình Phát Triển
- Nâng cao AI bằng Deep Learning & dữ liệu thực tế.
- Tích hợp video call tư vấn từ xa.
- Kết nối thiết bị wearable (smartwatch…).
- Triển khai microservices, bảo mật FHIR/HIPAA.
- Hỗ trợ đa ngôn ngữ, nền tảng Web/iOS.

---
---

## ✅ Tác Giả

- **Nguyễn Văn Tuân** – [@TuanNguyen811](https://github.com/TuanNguyen811)

---

## 📌 License

Dự án phục vụ mục đích **học tập**. Vui lòng không sử dụng cho mục đích thương mại nếu chưa được cho phép.
