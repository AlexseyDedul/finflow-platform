# finflow-platform
AWS services, k8s, camunda

## Run locally with Docker Compose

```bash
cd infrastructure/docker-compose
docker compose up --build

curl http://localhost:8080/actuator/health

http://localhost:8080/swagger-ui.html


