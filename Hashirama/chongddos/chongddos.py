import subprocess
import time

def block_ip(ip):
    add_firewall_command = f"netsh advfirewall firewall add rule name=\"Block IP\" dir=in action=block remoteip={ip}"
    subprocess.run(add_firewall_command, shell=True, check=True)

def unblock_ip(ip):
    delete_firewall_command = f"netsh advfirewall firewall delete rule name=\"Block IP\" dir=in remoteip={ip}"
    subprocess.run(delete_firewall_command, shell=True, check=True)

def get_active_sockets(port):
    command = f"netstat -nao | findstr :{port}"
    try:
        output = subprocess.check_output(command, shell=True).decode("utf-8")
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {command}")
        print(f"Return code: {e.returncode}")
        return {}

    ip_sockets = {}
    lines = output.splitlines()
    for line in lines:
        parts = line.split()
        if len(parts) >= 5 and parts[1].endswith(f":{port}"):
            ip_address = parts[2].split(":")[0]
            socket_id = parts[4]
            if ip_address not in ip_sockets:
                ip_sockets[ip_address] = []
            ip_sockets[ip_address].append(socket_id)
    return ip_sockets

def write_to_file(file_path, data):
    with open(file_path, "a") as file:
        file.write(data)

def main():
    file_path = "chongddos/ip.txt"
    blocked_file_path = "chongddos/blocked_ips.txt"
    port = 14445

    while True:
        ip_sockets = get_active_sockets(port)

        with open(file_path, "w") as file:
            for ip, sockets in ip_sockets.items():
                file.write(f"IP: {ip}\n")
                print(f"Đã ghi địa chỉ IP và số socket vào tệp {file_path}")
                file.write(f"Sockets: {len(sockets)}\n\n")
                if len(sockets) > 10:
                    write_to_file(blocked_file_path, f"Blocked IP: {ip}\n")
                    block_ip(ip)

        time.sleep(300)  # Wait for 5 minutes

if __name__ == "__main__":
    main()
