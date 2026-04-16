# Configurations
JAVA := java
MVN := mvn
JAR_NAME := target/VIP-1.0-RELEASE-jar-with-dependencies.jar
FINAL_JAR := ServerGame.jar

# Colors
BLUE   := \033[0;34m
GREEN  := \033[0;32m
RED    := \033[0;31m
YELLOW := \033[0;33m
RESET  := \033[0m

.PHONY: all build clean run kill update

all: build

build:
	@printf " $(BLUE)>>>$(RESET) Compiling ServerGame ...\n"
	@$(MVN) clean package -DskipTests
	@printf " $(GREEN)>>>$(RESET) Build Successful! JAR file is in the target directory\n"
	@cp $(JAR_NAME) ./$(FINAL_JAR)
	@printf " $(GREEN)>>>$(RESET) Updated ./$(FINAL_JAR) successfully!\n"

clean:
	@printf " $(RED)>>>$(RESET) Cleaning project...\n"
	@$(MVN) clean
	@rm -f $(FINAL_JAR)

run: build
	@printf " $(GREEN)>>>$(RESET) Launching ServerGame Game Server ...\n"
	@$(JAVA) -Djava.awt.headless=true -jar $(FINAL_JAR)

kill:
	@printf " $(RED)>>>$(RESET) Stopping running ServerGame processes...\n"
	@pkill -f $(FINAL_JAR) || printf "No running processes found.\n"

update: build
	@printf " $(GREEN)>>>$(RESET) Project updated to the latest build.\n"
