# Configurations
MVN := mvn
JAVA := java
OS := $(shell uname -s)
FINAL_JAR := ServerGame.jar
JAR_NAME := target/VIP-1.0-RELEASE-jar-with-dependencies.jar

# Colors
BLUE   := \033[0;34m
GREEN  := \033[0;32m
RED    := \033[0;31m
YELLOW := \033[0;33m
RESET  := \033[0m

ifeq ($(OS),Windows_NT)
	KILL_PORT := for /f "tokens=5" %a in ('netstat -aon ^| findstr :14445') do taskkill /f /pid %a 2>nul || exit 0
else
	UNAME_S := $(shell uname -s)
	ifeq ($(UNAME_S),Darwin)
		KILL_PORT := lsof -t -i:14445 | xargs kill -9 2>/dev/null || true
	else
		KILL_PORT := fuser -k 14445/tcp 2>/dev/null || true
	endif
endif

.PHONY: all build release clean run kill update

all: build

build:
	@printf " $(BLUE)>>>$(RESET) Compiling ServerGame ...\n"
	@$(MVN) clean package -DskipTests
	@printf " $(GREEN)>>>$(RESET) Build Successful! JAR file is in the target directory\n"
	@cp $(JAR_NAME) ./$(FINAL_JAR)
	@printf " $(GREEN)>>>$(RESET) Updated ./$(FINAL_JAR) successfully!\n"

release:
	@printf " $(GREEN)>>>$(RESET) Launching ServerGame...\n"
	@$(JAVA) -Djava.awt.headless=true -jar $(FINAL_JAR)

clean:
	@printf " $(RED)>>>$(RESET) Cleaning project...\n"
	@$(MVN) clean
	@rm -f $(FINAL_JAR)

run: kill build release

kill:
	@printf " $(GREEN)>>>$(RESET) Killing Process ...\n"
	-@$(KILL_PORT)
	@printf " $(GREEN)>>>$(RESET) Killing Process successfully ...\n"

update: build
	@printf " $(GREEN)>>>$(RESET) Project updated to the latest build.\n"
