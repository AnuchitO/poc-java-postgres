.PHONY: run 
run: 
	DB_HOST=localhost DB_PORT=5432 DB_USER=postgres DB_PASS=postgres ./gradlew build && DB_HOST=localhost DB_PORT=5432 DB_USER=postgres DB_PASS=postgres java -jar build/libs/*.jar

.PHONY: start
start:
	docker-compose up -d

.PHONY: stop
stop:
	docker-compose down -v
