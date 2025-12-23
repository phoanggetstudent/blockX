# SmartGuardian - Ứng dụng Quản lý Thiết bị

Đây là mã nguồn của ứng dụng quản lý thiết bị Android. Vì bạn chọn phương án **Cloud Build**, bạn không cần cài Android Studio.

## Hướng dẫn lấy file APK (Cài đặt)

### Bước 1: Tạo Repository trên GitHub
1. Đăng nhập vào [GitHub](https://github.com).
2. Tạo một Repository mới (ví dụ tên là `SmartGuardian`).
3. **Quan trọng**: Không tích chọn "Add a README file" (để tạo repo rỗng).

### Bước 2: Đẩy code lên GitHub
Mở cửa sổ dòng lệnh (Terminal/cmd) tại thư mục chứa code này và chạy lần lượt các lệnh sau:

```bash
git init
git add .
git commit -m "Khoi tao du an SmartGuardian"
# Thay đường dẫn bên dưới bằng link repo của bạn
git remote add origin https://github.com/USERNAME/SmartGuardian.git
git push -u origin master
```

### Bước 3: Tải file APK
1. Truy cập lại trang Repository của bạn trên GitHub.
2. Bấm vào tab **Actions** ở thanh menu trên cùng.
3. Bạn sẽ thấy một quy trình tên là **Android Build** đang chạy (màu vàng) hoặc đã xong (màu xanh).
4. Bấm vào nó, kéo xuống phần **Artifacts**.
5. Tải file `app-debug` về. Giải nén ra sẽ có file `.apk`.
6. Copy vào điện thoại và cài đặt.

## Tính năng
- **VPN Service**: Chặn web (Demo chặn kết nối).
- **Accessibility Service**: Chặn Shorts/Reels và Web đen.
- **Device Admin**: Ngăn chặn gỡ cài đặt app.
