# Colors for portability
CYAN   := \033[0;36m
BLUE   := \033[0;34m
YELLOW := \033[0;33m
GREEN  := \033[0;32m
RED    := \033[0;31m
RESET  := \033[0m

# Configurations
DATA_URL := https://github.com/NQHxDev/ZeionNRO/releases/download/Data/HashiramaData.zip
DATA_FILE := HashiramaData.zip

ifeq ($(OS),Windows_NT)
MVN_DETECT := mvn
else
MVN_DETECT := $(shell command -v mvn 2>/dev/null || ( [ -f /opt/homebrew/bin/mvn ] && echo "/opt/homebrew/bin/mvn" ) || echo "mvn")
endif
MVN ?= $(MVN_DETECT)

.PHONY: all build clean setup help

all: help

help:
	@printf "$(CYAN) Zeion NRO Master Management $(RESET)\n"
	@echo "-----------------------------------"
	@echo "make setup    - DOWNLOAD Assets, init configs & folder structure"
	@echo "make build    - Compile and package both modules"
	@echo "make clean    - Remove all build artifacts"
	@echo "-----------------------------------"

setup:
	@printf "$(BLUE)>>>$(RESET) Initializing Configuration Files...\n"
	@cp -n ServerGame/config/server.example.properties ServerGame/config/server.properties || echo "ServerGame config already exists."
	@cp -n ServerLogin/server.example.ini ServerLogin/server.ini || echo "ServerLogin config already exists."

	@printf "$(BLUE)>>>$(RESET) Checking for assets...\n"
	@if [ ! -d "ServerGame/resources" ] || [ -z "$$(ls -A ServerGame/resources)" ]; then \
		printf "$(YELLOW)>>> Assets missing. Downloading from GitHub Release...$(RESET)\n"; \
		curl -L $(DATA_URL) -o $(DATA_FILE); \
		printf "$(BLUE)>>> Extracting data pack...$(RESET)\n"; \
		unzip -q $(DATA_FILE) -d ServerGame; \
		rm -rf ServerGame/__MACOSX; \
		rm $(DATA_FILE); \
		printf "$(GREEN)>>> Data Pack installed successfully!$(RESET)\n"; \
	else \
		printf "$(GREEN)>>> Assets already exist. Skipping download.$(RESET)\n"; \
	fi

	@printf "$(BLUE)>>>$(RESET) Creating required directories...\n"
	@mkdir -p ServerGame/resources ServerGame/data ServerGame/Eff ServerGame/mob log

	@printf "$(GREEN)>>>$(RESET) SETUP COMPLETE!\n"
	@printf "$(YELLOW)Please update your DB credentials in the config files before running.$(RESET)\n"

build:
	@printf "$(BLUE)>>>$(RESET) Building ServerCommon...\n"
	@cd ServerCommon && "$(MVN)" clean install -DskipTests
	@printf "$(BLUE)>>>$(RESET) Building ServerLogin...\n"
	@cd ServerLogin && $(MAKE) build MVN="$(MVN)"
	@printf "$(BLUE)>>>$(RESET) Building ServerGame...\n"
	@cd ServerGame && $(MAKE) build MVN="$(MVN)"
	@printf "$(GREEN)>>>$(RESET) All modules built successfully!\n"

clean:
	@printf "$(RED)>>>$(RESET) Cleaning ServerLogin...\n"
	@cd ServerLogin && $(MAKE) clean MVN="$(MVN)"
	@printf "$(RED)>>>$(RESET) Cleaning ServerGame...\n"
	@cd ServerGame && $(MAKE) clean MVN="$(MVN)"
	@rm -rf log
	@printf "$(GREEN)>>>$(RESET) Project cleaned.\n"
