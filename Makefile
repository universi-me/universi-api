.DEFAULT_GOAL := help
COMPOSE=docker-compose

.PHONY:help
help:
	@cat $(MAKEFILE_LIST) | grep -e "^[a-zA-Z_\-]*: *.*## *" | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: run
run: ## Create and start containers for local development
	./deploy.sh run

.PHONY: down
down: ## Stop and remove local Docker containers
	$(COMPOSE) down

.PHONY: build
build: ## Build local Docker containers
	./deploy.sh build