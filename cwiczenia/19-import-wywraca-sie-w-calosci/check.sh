#!/usr/bin/env bash
# Check for exercise 19-import-wywraca-sie-w-calosci (the description is printed below)
# Run: ./cwiczenia/19-import-wywraca-sie-w-calosci/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 19-import-wywraca-sie-w-calosci: Jeden zły wiersz wywraca cały import cennika"
run_tests "$CW_DIR"
summary
