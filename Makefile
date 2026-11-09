.PHONY: run pack docker-build docker-run

run:
	java Main.java

pack:
	java Main.java --pack

docker-build:
	docker build -t shop-java .

docker-run:
	docker run --rm -p 127.0.0.1:8000:8000 shop-java
