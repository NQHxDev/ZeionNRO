# Variables
JAVA_HOME := $(shell /usr/libexec/java_home -v 21)
JAVA := $(JAVA_HOME)/bin/java
JAVAC := $(JAVA_HOME)/bin/javac
MVN := /opt/homebrew/bin/mvn
APP_NAME := ServerLogin
VERSION := 1.0-RELEASE
JAR_DIR := target
FAT_JAR := $(JAR_DIR)/$(APP_NAME)-$(VERSION)-jar-with-dependencies.jar
ROOT_JAR := ./ServerLogin.jar

# Colors for terminal output
CYAN   = \033[0;36m
GREEN  = \033[0;32m
YELLOW = \033[0;33m
RED    = \033[0;31m
NC     = \033[0m # No Color

.PHONY: all build clean run rebuild help update

# Default target
all: help

# Help command
help:
	@echo "$(CYAN)==================================================$(NC)"
	@echo "$(CYAN)     SERVER LOGIN MANAGEMENT COMMANDS           $(NC)"
	@echo "$(CYAN)==================================================$(NC)"
	@echo "  $(GREEN)make build$(NC)    - Biên dịch và đóng gói JAR (kèm dependencies)"
	@echo "  $(GREEN)make run$(NC)      - Build và khởi chạy Login Server ngay lập tức"
	@echo "  $(GREEN)make clean$(NC)    - Xóa các file rác và thư mục target/"
	@echo "  $(GREEN)make update$(NC)   - Build và copy JAR mới ra thư mục gốc"
	@echo "  $(GREEN)make rebuild$(NC)  - Clean và sau đó Build lại từ đầu"
	@echo "  $(GREEN)make help$(NC)     - Hiển thị hướng dẫn này"
	@echo "$(CYAN)==================================================$(NC)"

# Build the project
build:
	@echo "$(CYAN) Đang biên dịch $(APP_NAME) ...$(NC)"
	@JAVA_HOME=$(JAVA_HOME) $(MVN) clean package -DskipTests
	@echo "$(GREEN) Build thành công! File JAR nằm trong thư mục $(JAR_DIR)$(NC)"

# Update root JAR
update: build
	@echo "$(YELLOW) Đang cập nhật file JAR ra thư mục gốc...$(NC)"
	@cp $(FAT_JAR) $(ROOT_JAR)
	@echo "$(GREEN) Đã cập nhật $(ROOT_JAR) thành công!$(NC)"

# Clean build artifacts
clean:
	@echo "$(YELLOW) Đang dọn dẹp các tệp tin tạm...$(NC)"
	@$(MVN) clean
	@echo "$(GREEN) Đã dọn dẹp xong.$(NC)"

# Build and Run
run: update
	@echo "$(CYAN) Đang khởi chạy Login Server ...$(NC)"
	@$(JAVA) -jar $(ROOT_JAR)

# Rebuild
rebuild: clean update

