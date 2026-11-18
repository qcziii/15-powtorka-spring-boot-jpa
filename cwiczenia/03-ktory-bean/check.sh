#!/usr/bin/env bash
# Check for exercise 03-ktory-bean (the description is printed below)
# Run: ./cwiczenia/03-ktory-bean/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 03-ktory-bean: Aplikacja nie startuje: Spring nie wie, który bean wstrzyknąć"
run_tests "$CW_DIR"
summary
