#!/bin/bash
# ── FEItv — Script de Compilação ──────────────────────────────────────────────
# Pré-requisito: coloque o driver JDBC do PostgreSQL em lib/
# Download: https://jdbc.postgresql.org/download/
# Exemplo:  lib/postgresql-42.7.3.jar

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC="$BASE_DIR/src"
OUT="$BASE_DIR/out"
LIB="$BASE_DIR/lib"

# Verifica se há driver JDBC na pasta lib/
DRIVER=$(ls "$LIB"/postgresql-*.jar 2>/dev/null | head -1)
if [ -z "$DRIVER" ]; then
    echo "ERRO: Driver JDBC do PostgreSQL não encontrado em lib/"
    echo "Baixe em: https://jdbc.postgresql.org/download/"
    echo "Coloque o .jar dentro da pasta lib/"
    exit 1
fi

echo "Driver encontrado: $DRIVER"
echo "Compilando..."

# Limpa saída anterior
rm -rf "$OUT"
mkdir -p "$OUT"

# Coleta todos os arquivos .java
find "$SRC" -name "*.java" > /tmp/feitv_sources.txt

# Compila
javac -encoding UTF-8 -cp "$DRIVER" -d "$OUT" @/tmp/feitv_sources.txt

echo "✅ Compilação concluída! Execute ./run.sh para iniciar."
