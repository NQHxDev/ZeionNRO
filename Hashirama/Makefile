# Configurations
JAVA_HOME := $(shell /usr/libexec/java_home -v 21)
JAVA := $(JAVA_HOME)/bin/java
JAVAC := $(JAVA_HOME)/bin/javac
MVN := /opt/homebrew/bin/mvn
JAR_NAME := target/VIP-1.0-RELEASE-jar-with-dependencies.jar
FINAL_JAR := Hashirama.jar

.PHONY: all build clean run kill update

all: build

build:
	@echo " \033[0;34m>>>\033[0m Đang biên dịch Hashirama ..."
	@JAVA_HOME=$(JAVA_HOME) $(MVN) clean package -DskipTests
	@echo " \033[0;32m>>>\033[0m Build thành công! File JAR nằm trong thư mục target"
	@cp $(JAR_NAME) ./$(FINAL_JAR)
	@echo " \033[0;32m>>>\033[0m Đã cập nhật ./$(FINAL_JAR) thành công!"

clean:
	@echo " \033[0;31m>>>\033[0m Đang dọn dẹp dự án..."
	@$(MVN) clean
	@rm -f $(FINAL_JAR)

run: build
	@echo " \033[0;32m>>>\033[0m Đang khởi chạy Hashirama Game Server ..."
	@$(JAVA) -jar $(FINAL_JAR)

kill:
	@echo " \033[0;31m>>>\033[0m Đang tắt các tiến trình Hashirama đang chạy..."
	@pkill -f $(FINAL_JAR) || echo "Không tìm thấy tiến trình đang chạy."

update: build
	@echo " \033[0;32m>>>\033[0m Dự án đã được cập nhật bản build mới nhất."
