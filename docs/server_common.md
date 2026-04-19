# ServerCommon: Core Networking & Framework Layer

`ServerCommon` là module hạt nhân của hạ tầng ZeionNRO, cung cấp các thành phần cơ bản về mạng, xử lý gói tin và tiện ích dùng chung cho toàn bộ hệ thống.

---

## Kiến trúc lớp (Layer Architecture)

Module này được thiết kế để tách biệt hoàn toàn phần kỹ thuật mạng (Networking) khỏi logic nghiệp vụ (Game Logic).

### 1. Networking (Netty-based)
Dựa trên nền tảng **Netty 4.1**, cung cấp hiệu suất I/O cực cao.
- **`NettyServer`**: Lớp khởi tạo server, quản lý vòng đời của Netty Bootstrap.
- **`NettySession`**: Lớp đại diện cho một phiên kết nối, quản lý trạng thái kết nối và mã hóa XOR.
- **`NettyDecoder` / `NettyEncoder`**: Xử lý việc chuyển đổi giữa Byte Stream và các đối tượng `Message`.
- **`CommonHandler`**: Bridge kết nối giữa hạ tầng Netty và Logic Controller của từng module.

### 2. Abstraction (Giao diện chuẩn hóa)
Cung cấp các Interface giúp hệ thống có tính module hóa cao:
- **`IController`**: Định nghĩa cách một module (Login/Game) tiếp nhận và xử lý `Message`.
- **`ISession`**: Giao diện chuẩn cho các phiên kết nối.
- **`ISessionFactory`**: Cho phép từng module tự định nghĩa cách tạo ra các đối tượng Session (ví dụ: Session của Game chứa Player, Session của Login chứa User).

### 3. Common IO
- **`Message`**: Lớp chuẩn hóa gói tin của dự án, hỗ trợ đọc/ghi dữ liệu theo kiểu `DataInputStream`/`DataOutputStream` truyền thống nhưng chạy trên nền Buffer của Netty.

### 4. Utilities
- **`Log`**: Hệ thống Logging hợp nhất, hỗ trợ định dạng màu sắc (Ansi color) giúp việc theo dõi log trên terminal chuyên nghiệp và dễ dàng hơn.

---

## Cách thức hoạt động

Khi một module mới (ví dụ: `ServerGateway` sắp tới) muốn sử dụng hạ tầng mạng:
1. Implement `IController` để xử lý logic.
2. Implement `ISessionFactory` để khởi tạo session đặc thù.
3. Chạy `NettyServer` với các handler mặc định từ `ServerCommon`.

---

## Lợi ích
- **DRY (Don't Repeat Yourself)**: Không còn lặp lại mã nguồn khởi tạo socket ở từng module.
- **Consistency**: Mọi module đều sử dụng chung một cơ chế mã hóa XOR và định dạng log.
- **Performance**: Tối ưu hóa memory footprint thông qua việc sử dụng chung các Netty Pipeline components.
