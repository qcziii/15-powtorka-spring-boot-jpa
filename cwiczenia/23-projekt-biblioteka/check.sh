#!/usr/bin/env bash
# Check for exercise 23-projekt-biblioteka (the description is printed below)
# Run: ./cwiczenia/23-projekt-biblioteka/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 23-projekt-biblioteka: Projekt: API biblioteki"
run_tests "$CW_DIR"
summary
