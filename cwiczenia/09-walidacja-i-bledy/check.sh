#!/usr/bin/env bash
# Check for exercise 09-walidacja-i-bledy (the description is printed below)
# Run: ./cwiczenia/09-walidacja-i-bledy/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 09-walidacja-i-bledy: Rejestracja przepuszcza złe dane, a brak użytkownika to błąd 500"
run_tests "$CW_DIR"
summary
