#!/usr/bin/env bash
# Check for exercise 01-projekt-sie-nie-kompiluje (the description is printed below)
# Run: ./cwiczenia/01-projekt-sie-nie-kompiluje/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 01-projekt-sie-nie-kompiluje: Projekt się nie kompiluje"
run_tests "$CW_DIR"
summary
