#!/usr/bin/env bash
# Check for exercise 11-encja-zamowienia (the description is printed below)
# Run: ./cwiczenia/11-encja-zamowienia/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 11-encja-zamowienia: Zamówienia nie zapisują się do bazy, a odczyt się wywraca"
run_tests "$CW_DIR"
summary
