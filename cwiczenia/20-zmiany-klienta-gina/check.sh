#!/usr/bin/env bash
# Check for exercise 20-zmiany-klienta-gina (the description is printed below)
# Run: ./cwiczenia/20-zmiany-klienta-gina/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 20-zmiany-klienta-gina: Nowi klienci i zmiany danych klientów znikają"
run_tests "$CW_DIR"
summary
