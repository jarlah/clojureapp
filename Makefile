all: build run

build:
	docker build . -t test

run:
	docker run test