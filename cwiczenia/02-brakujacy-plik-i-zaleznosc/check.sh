#!/usr/bin/env bash
# Check for exercise 02-brakujacy-plik-i-zaleznosc (the description is printed below)
# Run: ./cwiczenia/02-brakujacy-plik-i-zaleznosc/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 02-brakujacy-plik-i-zaleznosc: Kursy walut się nie wczytują, a walidacja się nie kompiluje"
run_tests "$CW_DIR"
summary
