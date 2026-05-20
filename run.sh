#!/bin/bash
# ── FEItv — Script de Execução ────────────────────────────────────────────────

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
OUT="$BASE_DIR/out"
LIB="$BASE_DIR/lib"

DRIVER=$(ls "$LIB"/postgresql-*.jar 2>/dev/null | head -1)
if [ -z "$DRIVER" ]; then
    echo "ERRO: Driver JDBC não encontrado em lib/"
    exit 1
fi

if [ ! -f "$OUT/Main.class" ]; then
    echo "Projeto não compilado. Execute ./compile.sh primeiro."
    exit 1
fi

java -cp "$OUT:$DRIVER" Main
