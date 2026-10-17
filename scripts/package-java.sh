#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROJECT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(CDPATH= cd -- "$PROJECT_DIR/.." && pwd)
OUTPUT="$REPO_DIR/public/petstation-java.tar.gz"

tar \
  --exclude='petstation-java/target' \
  --exclude='petstation-java/out' \
  --exclude='petstation-java/.idea' \
  --exclude='petstation-java/.env' \
  --exclude='petstation-java/src/main/resources/db.properties' \
  -czf "$OUTPUT" \
  -C "$REPO_DIR" petstation-java

echo "Đã tạo $OUTPUT"
if command -v sha256sum >/dev/null 2>&1; then
  sha256sum "$OUTPUT"
fi
