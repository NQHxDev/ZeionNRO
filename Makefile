# Zeion NRO Monorepo Master Makefile

# Configurations
# Sếp hãy dán link GitHub Release vào đây sau khi đã upload file ZIP nhé
DATA_URL = "https://github.com/NQHxDev/ZeionNRO/releases/download/v1.0/DataPack.zip"

.PHONY: all build clean setup help

all: help

help:
	@echo "\033[0;36m Zeion NRO Master Management \033[0m"
	@echo "-----------------------------------"
	@echo "make setup    - Initialize configs and folder structure"
	@echo "make build    - Compile and package both modules"
	@echo "make clean    - Remove all build artifacts"
	@echo "-----------------------------------"

setup:
	@echo "\033[0;34m>>>\033[0m Initializing Configuration Files..."
	@cp -n Hashirama/config/server.example.properties Hashirama/config/server.properties || echo "Hashirama config already exists!"
	@cp -n ServerLogin/server.example.ini ServerLogin/server.ini || echo "ServerLogin config already exists!"
	@echo "\033[0;34m>>>\033[0m Creating Asset Directories..."
	@mkdir -p Hashirama/resources Hashirama/data Hashirama/Eff Hashirama/mob log
	@echo "\033[0;32m>>>\033[0m Setup Complete! Please update your DB credentials in the config files!"
	@echo "Note: To download data, you can run: curl -L $(DATA_URL) -o data.zip"

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
