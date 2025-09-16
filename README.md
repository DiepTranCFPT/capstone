# Spring Boot Authentication Service

## Overview
Authentication service với các tính năng:
- Đăng ký và xác thực email
- Đăng nhập JWT
- Đăng nhập Google OAuth2
- Quên mật khẩu với OTP
- Khóa tài khoản sau 5 lần đăng nhập sai
- Admin dashboard quản lý người dùng

## Technologies
- Java 17
- Spring Boot 3.1.4
- Spring Security
- JWT Authentication
- MySQL Database
- JavaMail
- Thymeleaf
- Swagger UI

## Prerequisites
- JDK 17+
- Maven
- MySQL
- Gmail account (for sending emails)

## Configuration
### Environment Variables
```properties
# Database
url=jdbc:mysql://your-db-host:3306/your-db-name
usr=your-db-username
pass=your-db-password

# JWT
jwt=your-jwt-secret-key

# Email (Gmail)
EMAIL_USERNAME=your-gmail@gmail.com
EMAIL_APP_PASSWORD=your-gmail-app-password

# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### Gmail App Password Setup
1. Truy cập Google Account Settings
2. Security > 2-Step Verification
3. App Passwords > Generate new
4. Select app: Other (Custom name)
5. Copy mật khẩu được tạo vào EMAIL_APP_PASSWORD

### Google OAuth2 Setup
1. Truy cập https://console.cloud.google.com
2. Tạo project mới hoặc chọn project có sẵn
3. Enable Google+ API
4. Credentials > Create Credentials > OAuth Client ID
5. Application Type: Web application
6. Authorized redirect URIs: 
   - http://localhost:8080/login/oauth2/code/google
7. Copy Client ID và Client Secret

## API Documentation
Swagger UI available at: `http://localhost:8080/swagger-ui.html`

### Main Endpoints

#### Authentication
```
POST /api/v1/auth/register - Đăng ký tài khoản mới
POST /api/v1/auth/authenticate - Đăng nhập
POST /api/v1/auth/forgot-password - Yêu cầu OTP reset password
POST /api/v1/auth/verify-otp - Xác thực OTP
POST /api/v1/auth/verify-email - Xác thực email
POST /api/v1/auth/change-password - Đổi mật khẩu
```

#### Admin (Yêu cầu ADMIN role)
```
GET /api/v1/admin/users - Danh sách user
PUT /api/v1/admin/role-permissions - Cập nhật quyền
PUT /api/v1/admin/password-policy - Cập nhật policy mật khẩu
```

## Security Features
1. JWT Authentication
2. Google OAuth2 Integration 
3. Email Verification
4. OTP for Password Reset
5. Account Locking
   - Khóa sau 5 lần đăng nhập sai
   - Tự động mở khóa sau 24h
   - Email thông báo khi bị khóa
6. Password Policy
   - Độ dài tối thiểu
   - Yêu cầu chữ hoa/thường
   - Yêu cầu số và ký tự đặc biệt
7. Role-based Access Control
8. CORS Configuration

## Frontend Integration
CORS được cấu hình cho:
- http://localhost:3000 (React)
- http://localhost:4200 (Angular)
- http://localhost:8081 (Vue)

## Build & Run
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

## Contributing
1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request
