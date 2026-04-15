import subprocess
import time
import os
import mysql.connector
from mysql.connector import Error

# Hàm tạo kết nối đến MySQL
def create_connection():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            database='nro',
            user='root',
            password=''
        )
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"Error: {e}")
        return None

# Thêm IP vào cơ sở dữ liệu
def add_ip_to_db(ip, reason):
    connection = create_connection()
    if connection:
        try:
            with connection.cursor() as cursor:
                cursor.execute("INSERT INTO blockip_list (blocker_ip, block_reason, blocked_at) VALUES (%s, %s, NOW())", (ip, reason))
            connection.commit()
        finally:
            connection.close()

# Xóa IP khỏi cơ sở dữ liệu
def remove_ip_from_db(ip):
    connection = create_connection()
    if connection:
        try:
            with connection.cursor() as cursor:
                cursor.execute("DELETE FROM blockip_list WHERE blocker_ip = %s", (ip,))
            connection.commit()
        finally:
            connection.close()

# Lấy danh sách các IP đã bị chặn từ cơ sở dữ liệu
def get_all_blocked_ips():
    connection = create_connection()
    if connection:
        try:
            with connection.cursor() as cursor:
                cursor.execute("SELECT blocker_ip FROM blockip_list")
                result = cursor.fetchall()
                return [row[0] for row in result]
        finally:
            connection.close()
    return []

# Lấy quy tắc tường lửa hiện có dựa trên tên quy tắc
def get_existing_rule(rule_name):
    command = f"netsh advfirewall firewall show rule name=\"{rule_name}\""
    try:
        output = subprocess.run(command, shell=True, capture_output=True, text=True, check=True)
        if "No rules match" not in output.stdout:
            return output.stdout
    except subprocess.CalledProcessError:
        return None
    return None

# Lấy danh sách các IP từ đầu ra của quy tắc tường lửa
def get_ips_from_rule(rule_output):
    lines = rule_output.splitlines()
    for line in lines:
        if line.strip().startswith("RemoteIP:"):
            ips = line.split(":")[1].strip()
            return ips.split(",")
    return []

# Thêm quy tắc tường lửa mới để chặn IP
def add_firewall_rule(rule_name, ip):
    command = f"netsh advfirewall firewall add rule name=\"{rule_name}\" dir=in action=block remoteip={ip}"
    subprocess.run(command, shell=True, check=True)

# Cập nhật quy tắc tường lửa với danh sách IP mới
def update_firewall_rule(rule_name, ips):
    delete_firewall_rule(rule_name)
    add_firewall_rule(rule_name, ips)

# Xóa quy tắc tường lửa dựa trên tên quy tắc
def delete_firewall_rule(rule_name):
    command = f"netsh advfirewall firewall delete rule name=\"{rule_name}\""
    subprocess.run(command, shell=True, check=True)

# Chặn IP bằng cách thêm vào quy tắc tường lửa và cơ sở dữ liệu
def block_ip(ip, reason=""):
    rule_name = "Block Multiple IPs"
    existing_rule = get_existing_rule(rule_name)
    
    if existing_rule:
        current_ips = get_ips_from_rule(existing_rule)
        if ip not in current_ips:
            current_ips.append(ip)
            updated_ips = ",".join(current_ips)
            update_firewall_rule(rule_name, updated_ips)
            add_ip_to_db(ip, reason)
            log_action("Block", ip, reason)
    else:
        add_firewall_rule(rule_name, ip)
        add_ip_to_db(ip, reason)
        log_action("Block", ip, reason)

# Bỏ chặn IP bằng cách xóa khỏi quy tắc tường lửa và cơ sở dữ liệu
def unblock_ip(ip):
    rule_name = "Block Multiple IPs"
    existing_rule = get_existing_rule(rule_name)
    
    if existing_rule:
        current_ips = get_ips_from_rule(existing_rule)
        if ip in current_ips:
            current_ips.remove(ip)
            if current_ips:
                updated_ips = ",".join(current_ips)
                update_firewall_rule(rule_name, updated_ips)
            else:
                delete_firewall_rule(rule_name)
            remove_ip_from_db(ip)
            log_action("Unblock", ip)

# Lấy danh sách IP đang kết nối đến cổng
def get_active_sockets(port):
    command = f"netstat -nao | findstr :{port}"
    try:
        output = subprocess.run(command, shell=True, capture_output=True, text=True, check=True)
        ip_addresses = []
        lines = output.stdout.splitlines()
        for line in lines:
            parts = line.split()
            if len(parts) >= 5 and parts[1].endswith(f":{port}"):
                ip_address = parts[2].split(":")[0]
                if ip_address not in ip_addresses:
                    ip_addresses.append(ip_address)
        return ip_addresses
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {command}")
        print(f"Return code: {e.returncode}")
        return []

# Ghi dữ liệu vào file
def write_to_file(file_path, data):
    with open(file_path, "a") as file:
        file.write(data)

# Hàm xử lý chặn DDoS
def block_ddos(ip):
    reason = "Detected DDoS attack"
    block_ip(ip, reason)
    log_action("Block DDoS", ip, reason)
    send_email("DDoS Attack Detected", f"Blocked IP {ip} due to DDoS attack")
    print(f"Blocked IP {ip} due to DDoS attack")

# Hàm phát hiện DDoS và thực thi chặn IP nếu có nhiều kết nối hơn giới hạn
def detect_ddos(port, ip_address_limit):
    ip_addresses = get_active_sockets(port)
    for ip in ip_addresses:
        if len(get_active_sockets(port)) > ip_address_limit:
            block_ddos(ip)
            write_to_file("chongddos/blockedvv_ips.txt", f"Blocked IP: {ip}\n")

# Hàm ghi log hành động
def log_action(action, ip, reason=""):
    timestamp = time.strftime('%Y-%m-%d %H:%M:%S')
    log_message = f"[{timestamp}] Action: {action} IP: {ip} Reason: {reason}\n"
    with open("chongddos/action.log", "a") as log_file:
        log_file.write(log_message)

# Hàm chính của chương trình
def main():
    file_path = "chongddos/ip.txt"
    blocked_file_path = "chongddos/blockedvv_ips.txt"
    port = 14445
    ip_address_limit = 10

    # Tạo thư mục chongddos nếu chưa tồn tại
    os.makedirs(os.path.dirname(file_path), exist_ok=True)

    while True:
        detect_ddos(port, ip_address_limit)

        # Mở file để ghi dữ liệu vào
        with open(file_path, "w") as file:
            for ip in get_active_sockets(port):
                file.write(f"IP: {ip}\n")
                print(f"Đã ghi địa chỉ IP vào tệp {file_path}")
                if len(get_active_sockets(port)) > ip_address_limit:
                    write_to_file(blocked_file_path, f"Blocked IP: {ip}\n")
                    block_ddos(ip)

        # Kiểm tra và bỏ chặn các IP không còn trong danh sách cơ sở dữ liệu
        blocked_ips_in_db = get_all_blocked_ips()
        rule_name = "Block Multiple IPs"
        existing_rule = get_existing_rule(rule_name)
        if existing_rule:
            current_ips = get_ips_from_rule(existing_rule)
            for ip in current_ips:
                if ip not in blocked_ips_in_db:
                    unblock_ip(ip)

        time.sleep(60)  # Chờ 1 phút

if __name__ == "__main__":
    main()
