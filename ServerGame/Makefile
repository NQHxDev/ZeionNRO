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
	@printf " $(BLUE)>>>$(RESET) Đang biên dịch ServerGame ...\n"
	@$(MVN) clean package -DskipTests
	@printf " $(GREEN)>>>$(RESET) Build thành công! File JAR nằm trong thư mục target\n"
	@cp $(JAR_NAME) ./$(FINAL_JAR)
	@printf " $(GREEN)>>>$(RESET) Đã cập nhật ./$(FINAL_JAR) thành công!\n"

clean:
	@printf " $(RED)>>>$(RESET) Đang dọn dẹp dự án...\n"
	@$(MVN) clean
	@rm -f $(FINAL_JAR)

run: build
	@printf " $(GREEN)>>>$(RESET) Đang khởi chạy ServerGame Game Server ...\n"
	@$(JAVA) -jar $(FINAL_JAR)

kill:
	@printf " $(RED)>>>$(RESET) Đang tắt các tiến trình ServerGame đang chạy...\n"
	@pkill -f $(FINAL_JAR) || printf "Không tìm thấy tiến trình đang chạy.\n"

update: build
	@printf " $(GREEN)>>>$(RESET) Dự án đã được cập nhật bản build mới nhất.\n"
