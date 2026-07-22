# Spring Boot + PostgreSQL + Redis on Kubernetes Sample

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-postgres-redis-on-k8s-sample/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-postgres-redis-on-k8s-sample/actions/workflows/maven.yml)

A sample Spring Boot REST API that stores employees in **PostgreSQL** and caches read results in **Redis**, packaged with a Dockerfile and Kubernetes manifests for deployment.

## Tech Stack

| Component   | Version / Detail                          |
|-------------|-------------------------------------------|
| Java        | 25 (Temurin)                               |
| Spring Boot | 4.1.0                                      |
| Database    | PostgreSQL (Spring Data JPA / Hibernate)   |
| Cache       | Redis (Jedis client, Spring Cache)         |
| Build       | Maven                                      |
| Deploy      | Docker + Kubernetes (NodePort services)    |

## How It Works

- `GET /api/employees` — returns all employees. The result is cached in Redis under the `employee_list` cache; subsequent calls skip the database until the cache is evicted.
- `POST /api/employees` — creates an employee and evicts the `employee_list` cache.

```bash
# create an employee
curl -X POST http://localhost:8082/api/employees \
  -H 'Content-Type: application/json' \
  -d '{"name": "Hendi Santika"}'

# list employees (first call hits Postgres, next calls hit Redis)
curl http://localhost:8082/api/employees
```

## Configuration

Connection settings live in `src/main/resources/application.properties` and can be overridden via environment variables:

| Property                     | Env var                      | Default                    |
|------------------------------|------------------------------|----------------------------|
| `spring.datasource.url`      | `SPRING_DATASOURCE_URL`      | remote k8s NodePort URL    |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `rootuser`                 |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `rootpassword`             |
| `redis.host`                 | `REDIS_HOST`                 | remote k8s node IP         |
| `redis.port`                 | `REDIS_PORT`                 | `32666`                    |

The app listens on port **8082**.

## Run Locally

Start PostgreSQL and Redis (e.g. with Docker):

```bash
docker run -d --name pg -e POSTGRES_USER=rootuser -e POSTGRES_PASSWORD=rootpassword \
  -e POSTGRES_DB=employee -p 5432:5432 postgres:17-alpine
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

Then run the app pointing at them:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/employee \
SPRING_DATASOURCE_USERNAME=rootuser \
SPRING_DATASOURCE_PASSWORD=rootpassword \
REDIS_HOST=localhost \
REDIS_PORT=6379 \
./mvnw spring-boot:run
```

## Build & Docker Image

```bash
./mvnw -B package
docker build -t hendisantika/spring-boot-postgres-redis-on-k8s-sample .
```

## Deploy to Kubernetes

Manifests are in the `k8s/` directory:

| File                       | Purpose                                        |
|----------------------------|------------------------------------------------|
| `postgres-configmap.yaml`  | PostgreSQL credentials & database name         |
| `postgres-pvc.yaml`        | Persistent volume claim for PostgreSQL data    |
| `postgres-deployment.yaml` | PostgreSQL deployment + NodePort service       |
| `redis.ns.yaml`            | Redis namespace                                |
| `redis-deployment.yaml`    | Redis deployment                               |
| `redis-service.yaml`       | Redis NodePort service                         |
| `deployment.yaml`          | Spring Boot app deployment + NodePort service (port 30163) |

```bash
kubectl apply -f k8s/postgres-configmap.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/redis.ns.yaml
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/redis-service.yaml
kubectl apply -f k8s/deployment.yaml
```

The app is then reachable at `http://<node-ip>:30163/api/employees`.

## CI

GitHub Actions (`.github/workflows/maven.yml`) builds and tests every push and PR against `main`, spinning up PostgreSQL and Redis as service containers, and submits the dependency graph to GitHub on pushes.

## Author

**Hendi Santika**
- Link: [s.id/hendisantika](https://s.id/hendisantika)
- Telegram: [@hendisantika34](https://t.me/hendisantika34)
