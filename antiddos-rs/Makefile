# Variables
BINARY_NAME=antiddos-rs
TARGET_DIR=target/debug
RELEASE_DIR=target/release

# Colors
CYAN   := \033[0;36m
GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
RESET  := \033[0m

.PHONY: all build run release clean test help run-release

all: help

help:
	@printf "$(CYAN)==================================================$(RESET)\n"
	@printf "$(CYAN)     ANTI-DDOS PRO MANAGEMENT COMMANDS          $(RESET)\n"
	@printf "$(CYAN)==================================================$(RESET)\n"
	@printf "  $(GREEN)make build$(RESET)         - Compile project in debug mode\n"
	@printf "  $(GREEN)make run$(RESET)           - Build and run with sudo (Debug)\n"
	@printf "  $(GREEN)make release$(RESET)       - Build project in release mode\n"
	@printf "  $(GREEN)make run-release$(RESET)   - Build and run with sudo (Release)\n"
	@printf "  $(GREEN)make baotri$(RESET)        - Gracefully stop for maintenance\n"
	@printf "  $(GREEN)make clean$(RESET)         - Remove build artifacts and logs\n"
	@printf "  $(GREEN)make clean-logs$(RESET)    - Clear only the logs directory\n"
	@printf "  $(GREEN)make test$(RESET)          - Run Rust tests\n"
	@printf "  $(GREEN)make help$(RESET)          - Show this help menu\n"
	@printf "$(CYAN)==================================================$(RESET)\n"

build:
	@printf "$(CYAN)>> Building $(BINARY_NAME) in debug mode...$(RESET)\n"
	@cargo build
	@printf "$(GREEN)>> Build Successful!$(RESET)\n"

run: build
	@printf "$(YELLOW)>> Running $(BINARY_NAME) with sudo...$(RESET)\n"
	@sudo ./$(TARGET_DIR)/$(BINARY_NAME)

release:
	@printf "$(CYAN)>> Building $(BINARY_NAME) in release mode...$(RESET)\n"
	@cargo build --release
	@printf "$(GREEN)>> Release Build Successful!$(RESET)\n"

run-release: release
	@printf "$(YELLOW)>> Running $(BINARY_NAME) (release mode) with sudo...$(RESET)\n"
	@sudo ./$(RELEASE_DIR)/$(BINARY_NAME)

baotri:
	@printf "$(YELLOW)>> Initiating maintenance (Graceful Shutdown)...$(RESET)\n"
	@sudo pkill -TERM $(BINARY_NAME) || echo "Process not running."

clean:
	@printf "$(RED)>> Cleaning project artifacts and logs...$(RESET)\n"
	@cargo clean
	@rm -rf logs/
	@printf "$(GREEN)>> Cleanup complete.$(RESET)\n"

test:
	@printf "$(CYAN)>> Running tests...$(RESET)\n"
	@cargo test
	@printf "$(GREEN)>> Tests complete.$(RESET)\n"

clean-logs:
	@printf "$(RED)>> Clearing anti-DDoS logs ...$(RESET)\n"
	@sudo rm -rf logs/*
	@printf "$(GREEN)>> Logs cleared successfully!$(RESET)\n"
