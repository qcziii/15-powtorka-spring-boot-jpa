#!/usr/bin/env bash
# Check for exercise 06-ustawienia-i-profile (the description is printed below)
# Run: ./cwiczenia/06-ustawienia-i-profile/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 06-ustawienia-i-profile: Ustawienia sklepu z application.yml i profil dev nie działają"
run_tests "$CW_DIR"
summary
