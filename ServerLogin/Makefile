# Configurations
JAVA := java
MVN := mvn
APP_NAME := ServerLogin
VERSION := 1.0-RELEASE
JAR_DIR := target
FAT_JAR := $(JAR_DIR)/$(APP_NAME)-$(VERSION)-jar-with-dependencies.jar
ROOT_JAR := ./ServerLogin.jar

# Colors
CYAN   := \033[0;36m
GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
RESET  := \033[0m

.PHONY: all build clean run rebuild help update

all: help

help:
	@printf "$(CYAN)==================================================$(RESET)\n"
	@printf "$(CYAN)     SERVER LOGIN MANAGEMENT COMMANDS           $(RESET)\n"
	@printf "$(CYAN)==================================================$(RESET)\n"
	@printf "  $(GREEN)make build$(RESET)    - Biên dịch và đóng gói JAR (kèm dependencies)\n"
	@printf "  $(GREEN)make run$(RESET)      - Build và khởi chạy Login Server ngay lập tức\n"
	@printf "  $(GREEN)make clean$(RESET)    - Xóa các file rác và thư mục target/\n"
	@printf "  $(GREEN)make update$(RESET)   - Build và copy JAR mới ra thư mục gốc\n"
	@printf "  $(GREEN)make rebuild$(RESET)  - Clean và sau đó Build lại từ đầu\n"
	@printf "  $(GREEN)make help$(RESET)     - Hiển thị hướng dẫn này\n"
	@printf "$(CYAN)==================================================$(RESET)\n"

build:
	@printf "$(CYAN) Đang biên dịch $(APP_NAME) ...$(RESET)\n"
	@$(MVN) clean package -DskipTests
	@printf "$(GREEN) Build thành công! File JAR nằm trong thư mục $(JAR_DIR)$(RESET)\n"

update: build
	@printf "$(YELLOW) Đang cập nhật file JAR ra thư mục gốc...$(RESET)\n"
	@cp $(FAT_JAR) $(ROOT_JAR)
	@printf "$(GREEN) Đã cập nhật $(ROOT_JAR) thành công!$(RESET)\n"

clean:
	@printf "$(YELLOW) Đang dọn dẹp các tệp tin tạm...$(RESET)\n"
	@$(MVN) clean
	@printf "$(GREEN) Đã dọn dẹp xong.$(RESET)\n"

run: update
	@printf "$(CYAN) Đang khởi chạy Login Server ...$(RESET)\n"
	@$(JAVA) -jar $(ROOT_JAR)

rebuild: clean update

