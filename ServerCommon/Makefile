# Configurations
MVN := mvn
APP_NAME := ServerCommon
VERSION := 1.0-RELEASE

.PHONY: all build clean install help

all: help

help:
	@echo "ServerCommon Management Commands"
	@echo "-------------------------------"
	@echo "make build    - Compile and package JAR"
	@echo "make install  - Build and install to local Maven repo"
	@echo "make clean    - Delete target/ directory"

build:
	@$(MVN) clean package -DskipTests

install:
	@$(MVN) clean install -DskipTests

clean:
	@$(MVN) clean
