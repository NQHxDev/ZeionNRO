# Configurations
DATA_URL := https://github.com/NQHxDev/ZeionNRO/releases/download/Data/HashiramaData.zip
DATA_FILE := HashiramaData.zip

.PHONY: all build clean setup help

all: help

help:
	@echo "\033[0;36m Zeion NRO Master Management \033[0m"
	@echo "-----------------------------------"
	@echo "make setup    - DOWNLOAD Assets, init configs & folder structure"
	@echo "make build    - Compile and package both modules"
	@echo "make clean    - Remove all build artifacts"
	@echo "-----------------------------------"

setup:
	@echo "\033[0;34m>>>\033[0m Initializing Configuration Files..."
	@cp -n ServerGame/config/server.example.properties ServerGame/config/server.properties || echo "ServerGame config already exists."
	@cp -n ServerLogin/server.example.ini ServerLogin/server.ini || echo "ServerLogin config already exists."

	@echo "\033[0;34m>>>\033[0m Checking for assets..."
	@if [ ! -d "ServerGame/resources" ] || [ -z "$$(ls -A ServerGame/resources)" ]; then \
		echo "\033[0;33m>>> Assets missing. Downloading from GitHub Release...\033[0m"; \
		curl -L $(DATA_URL) -o $(DATA_FILE); \
		echo "\033[0;34m>>> Extracting data pack...\033[0m"; \
		unzip -q $(DATA_FILE) -d ServerGame; \
		rm -rf ServerGame/__MACOSX; \
		rm $(DATA_FILE); \
		echo "\033[0;32m>>> Data Pack installed successfully!\033[0m"; \
	else \
		echo "\033[0;32m>>> Assets already exist. Skipping download.\033[0m"; \
	fi

	@echo "\033[0;34m>>>\033[0m Creating required directories..."
	@mkdir -p ServerGame/resources ServerGame/data ServerGame/Eff ServerGame/mob log

	@echo "\033[0;32m>>>\033[0m SETUP COMPLETE!"
	@echo "\033[0;33mPlease update your DB credentials in the config files before running.\033[0m"

build:
	@echo "\033[0;34m>>>\033[0m Building ServerLogin..."
	@cd ServerLogin && $(MAKE) build
	@echo "\033[0;34m>>>\033[0m Building ServerGame..."
	@cd ServerGame && $(MAKE) build
	@echo "\033[0;32m>>>\033[0m All modules built successfully!"

clean:
	@echo "\033[0;31m>>>\033[0m Cleaning ServerLogin..."
	@cd ServerLogin && $(MAKE) clean
	@echo "\033[0;31m>>>\033[0m Cleaning ServerGame..."
	@cd ServerGame && $(MAKE) clean
	@rm -rf log
	@echo "\033[0;32m>>>\033[0m Project cleaned."
