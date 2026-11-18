#!/usr/bin/env bash
# Check for exercise 15-encja-w-odpowiedzi-json (the description is printed below)
# Run: ./cwiczenia/15-encja-w-odpowiedzi-json/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 15-encja-w-odpowiedzi-json: Autor z książkami: błąd przy zamianie na JSON i wyciek danych wewnętrznych"
run_tests "$CW_DIR"
summary
