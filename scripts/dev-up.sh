#!/usr/bin/env bash
set -euo pipefail

echo "Starting platform (core services)..."
docker compose up --build -d

echo
echo "Core URLs:"
echo "- API Gateway:     http://localhost:9001"
echo "- Keycloak:        http://localhost:8080 (admin/password)"
echo "- Product Service: http://localhost:8084"
echo "- Order Service:   http://localhost:8082"
echo "- Inventory:       http://localhost:8083"
echo
echo "Tip: start optional tooling (Mongo Express, RedisInsight):"
echo "  docker compose --profile tools up -d"
