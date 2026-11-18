#!/usr/bin/env bash
# Check for exercise 14-pozycje-zamowienia (the description is printed below)
# Run: ./cwiczenia/14-pozycje-zamowienia/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 14-pozycje-zamowienia: Pozycje zamówienia nie zapisują się i nie usuwają"
run_tests "$CW_DIR"
summary
