#!/usr/bin/env bash
# Check for exercise 17-przelew-gubi-pieniadze (the description is printed below)
# Run: ./cwiczenia/17-przelew-gubi-pieniadze/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 17-przelew-gubi-pieniadze: Nieudany przelew i tak zabiera pieniądze z konta"
run_tests "$CW_DIR"
summary
