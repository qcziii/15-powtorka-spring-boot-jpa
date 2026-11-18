#!/usr/bin/env bash
# Check for exercise 16-zmiany-same-sie-zapisuja (the description is printed below)
# Run: ./cwiczenia/16-zmiany-same-sie-zapisuja/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 16-zmiany-same-sie-zapisuja: Podgląd rabatu zmienia cenę w bazie, a edycja kasuje dane"
run_tests "$CW_DIR"
summary
