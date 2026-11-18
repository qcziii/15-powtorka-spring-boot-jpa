#!/usr/bin/env bash
# Check for exercise 04-konfiguracja-ignorowana (the description is printed below)
# Run: ./cwiczenia/04-konfiguracja-ignorowana/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 04-konfiguracja-ignorowana: Rabat i darmowa dostawa z konfiguracji są ignorowane"
run_tests "$CW_DIR"
summary
