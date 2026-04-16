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
	@printf "  $(GREEN)make build$(RESET)    - Compile and package JAR (with dependencies)\n"
	@printf "  $(GREEN)make run$(RESET)      - Build and launch Login Server immediately\n"
	@printf "  $(GREEN)make clean$(RESET)    - Delete temporary files and target/ directory\n"
	@printf "  $(GREEN)make update$(RESET)   - Build and copy new JAR to root directory\n"
	@printf "  $(GREEN)make rebuild$(RESET)  - Clean and then rebuild from scratch\n"
	@printf "  $(GREEN)make help$(RESET)     - Show this help menu\n"
	@printf "$(CYAN)==================================================$(RESET)\n"

build:
	@printf "$(CYAN) Compiling $(APP_NAME) ...$(RESET)\n"
	@$(MVN) clean package -DskipTests
	@printf "$(GREEN) Build Successful! JAR file is in the $(JAR_DIR) directory$(RESET)\n"

update: build
	@printf "$(YELLOW) Updating JAR file to root directory...$(RESET)\n"
	@cp $(FAT_JAR) $(ROOT_JAR)
	@printf "$(GREEN) Updated $(ROOT_JAR) successfully!$(RESET)\n"

clean:
	@printf "$(YELLOW) Cleaning temporary files...$(RESET)\n"
	@$(MVN) clean
	@printf "$(GREEN) Cleanup complete.$(RESET)\n"

run: update
	@printf "$(CYAN) Launching Login Server ...$(RESET)\n"
	@$(JAVA) -jar $(ROOT_JAR)

rebuild: clean update

