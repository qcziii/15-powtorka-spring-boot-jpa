#!/usr/bin/env bash
# Check for exercise 18-audyt-i-anulowanie (the description is printed below)
# Run: ./cwiczenia/18-audyt-i-anulowanie/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 18-audyt-i-anulowanie: Audyt znika razem z odrzuconym zamówieniem, a anulowanie nie działa"
run_tests "$CW_DIR"
summary
