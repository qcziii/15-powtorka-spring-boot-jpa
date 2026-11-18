#!/usr/bin/env bash
# Check for exercise 07-rest-notatki (the description is printed below)
# Run: ./cwiczenia/07-rest-notatki/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 07-rest-notatki: API REST notatek (CRUD)"
run_tests "$CW_DIR"
summary
