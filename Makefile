DOCKER_IMAGE=jsl-test

.PHONY: test
test:
	docker build --target=build -t $(DOCKER_IMAGE) .
	docker run -i --rm \
		-v maven-repo:/root/.m2 \
		-v $(PWD):/app \
		-w /app \
		$(DOCKER_IMAGE) mvn clean test
