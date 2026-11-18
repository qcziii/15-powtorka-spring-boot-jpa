#!/usr/bin/env bash
# Check for exercise 12-zapytania-w-repozytorium (the description is printed below)
# Run: ./cwiczenia/12-zapytania-w-repozytorium/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 12-zapytania-w-repozytorium: Aplikacja kina nie startuje przez zapytania w repozytorium"
run_tests "$CW_DIR"
summary
