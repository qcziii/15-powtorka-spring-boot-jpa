#!/usr/bin/env bash
# Check for exercise 10-encja-i-repozytorium (the description is printed below)
# Run: ./cwiczenia/10-encja-i-repozytorium/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 10-encja-i-repozytorium: Encja książki i repozytorium Spring Data"
run_tests "$CW_DIR"
summary
