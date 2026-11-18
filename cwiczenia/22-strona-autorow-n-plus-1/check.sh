#!/usr/bin/env bash
# Check for exercise 22-strona-autorow-n-plus-1 (the description is printed below)
# Run: ./cwiczenia/22-strona-autorow-n-plus-1/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 22-strona-autorow-n-plus-1: Strona autorów z książkami: zapytanie na każdego autora (N+1 i stronicowanie)"
run_tests "$CW_DIR"
summary
