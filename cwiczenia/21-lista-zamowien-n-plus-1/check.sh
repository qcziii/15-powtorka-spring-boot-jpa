#!/usr/bin/env bash
# Check for exercise 21-lista-zamowien-n-plus-1 (the description is printed below)
# Run: ./cwiczenia/21-lista-zamowien-n-plus-1/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 21-lista-zamowien-n-plus-1: Lista zamówień wysyła do bazy kilkadziesiąt zapytań (N+1)"
run_tests "$CW_DIR"
summary
