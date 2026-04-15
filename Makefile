# Zeion NRO Monorepo Master Makefile

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
	@cp -n Hashirama/config/server.example.properties Hashirama/config/server.properties || echo "Hashirama config already exists."
	@cp -n ServerLogin/server.example.ini ServerLogin/server.ini || echo "ServerLogin config already exists."

	@echo "\033[0;34m>>>\033[0m Checking for assets..."
	@if [ ! -d "Hashirama/resources" ] || [ -z "$$(ls -A Hashirama/resources)" ]; then \
		echo "\033[0;33m>>> Assets missing. Downloading from GitHub Release...\033[0m"; \
		curl -L $(DATA_URL) -o $(DATA_FILE); \
		echo "\033[0;34m>>> Extracting data pack...\033[0m"; \
		unzip -q $(DATA_FILE); \
		rm $(DATA_FILE); \
		echo "\033[0;32m>>> Data Pack installed successfully!\033[0m"; \
	else \
		echo "\033[0;32m>>> Assets already exist. Skipping download.\033[0m"; \
	fi

	@echo "\033[0;34m>>>\033[0m Creating required directories..."
	@mkdir -p Hashirama/resources Hashirama/data Hashirama/Eff Hashirama/mob log

	@echo "\033[0;32m>>>\033[0m SETUP COMPLETE!"
	@echo "\033[0;33mPlease update your DB credentials in the config files before running.\033[0m"

build:
	@echo "\033[0;34m>>>\033[0m Building ServerLogin..."
	@cd ServerLogin && $(MAKE) build
	@echo "\033[0;34m>>>\033[0m Building Hashirama..."
	@cd Hashirama && $(MAKE) build
	@echo "\033[0;32m>>>\033[0m All modules built successfully!"

clean:
	@echo "\033[0;31m>>>\033[0m Cleaning ServerLogin..."
	@cd ServerLogin && $(MAKE) clean
	@echo "\033[0;31m>>>\033[0m Cleaning Hashirama..."
	@cd Hashirama && $(MAKE) clean
	@rm -rf log
	@echo "\033[0;32m>>>\033[0m Project cleaned."
