# 👕 QUAN_AO_F4K

Ứng dụng web quản lý bán quần áo trực tuyến. Người dùng có thể xem sản phẩm, thêm vào giỏ hàng, đặt hàng, đồng thời admin có thể quản lý sản phẩm và đơn hàng.

---

## 🚀 Tính năng chính
- Hiển thị danh mục sản phẩm (ảnh, giá, mô tả).
- Tìm kiếm và lọc sản phẩm theo loại, giá.
- Quản lý giỏ hàng: thêm, cập nhật số lượng, xoá sản phẩm.
- Đặt hàng và quản lý đơn hàng.
- Đăng ký, đăng nhập và quản lý tài khoản người dùng.
- Trang quản trị (admin) để thêm/sửa/xoá sản phẩm.

---

## 🛠️ Công nghệ sử dụng
- **Ngôn ngữ:** Java, HTML, CSS/SCSS, JavaScript  
- **Framework:** Spring Boot, Spring Data JPA, Hibernate (nếu có backend)  
- **Cơ sở dữ liệu:** MySQL  
- **Công cụ:** Git, Maven, IntelliJ IDEA / Eclipse  

---

## 📂 Cấu trúc thư mục (tham khảo)
QUAN_AO_F4K/
├── src/
│ ├── main/
│ │ ├── java/ # code backend
│ │ ├── resources/
│ │ └── static/ # giao diện frontend (HTML/CSS/JS)
├── pom.xml # Maven build file
├── README.md
└── ...


---

## ▶️ Hướng dẫn chạy dự án
1. Clone repo:
   ```bash
   git clone https://github.com/phuclx204/QUAN_AO_F4K.git
   cd QUAN_AO_F4K
2. Cấu hình database trong application.properties (hoặc application.yml):
spring.datasource.url=jdbc:mysql://localhost:3306/quan_ao_f4k
spring.datasource.username=root
spring.datasource.password=your_password

3. Chạy dự án bằng Maven:
mvn spring-boot:run

4.Mở trình duyệt và truy cập http://localhost:8080.
