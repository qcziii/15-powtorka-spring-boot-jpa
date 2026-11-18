#!/usr/bin/env bash
# Check for exercise 08-kontroler-nie-odpowiada (the description is printed below)
# Run: ./cwiczenia/08-kontroler-nie-odpowiada/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 08-kontroler-nie-odpowiada: Kontroler produktów: puste odpowiedzi, 400 i 500 zamiast danych"
run_tests "$CW_DIR"
summary
