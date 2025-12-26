#!/usr/bin/env bash
set -euo pipefail

echo "Stopping platform..."
docker compose down

echo "Done."
