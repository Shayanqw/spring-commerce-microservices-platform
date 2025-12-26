#!/usr/bin/env bash
set -euo pipefail

echo "Smoke testing health endpoints..."

function check() {
  local name="$1"
  local url="$2"
  echo -n "- $name: "
  if curl -fsS "$url" >/dev/null; then
    echo "OK"
  else
    echo "FAIL"
    exit 1
  fi
}

check "Keycloak" "http://localhost:8080/realms/spring-microservices-security-realm"
check "API Gateway" "http://localhost:9001/actuator/health"
check "Product Service" "http://localhost:8084/actuator/health"
check "Inventory Service" "http://localhost:8083/actuator/health"
check "Order Service" "http://localhost:8082/actuator/health"

echo
echo "All good ✅"
