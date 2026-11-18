#!/usr/bin/env bash
# Check for exercise 05-raport-z-cudzymi-danymi (the description is printed below)
# Run: ./cwiczenia/05-raport-z-cudzymi-danymi/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 05-raport-z-cudzymi-danymi: Raport zamówienia zawiera dane poprzednich klientów"
run_tests "$CW_DIR"
summary
