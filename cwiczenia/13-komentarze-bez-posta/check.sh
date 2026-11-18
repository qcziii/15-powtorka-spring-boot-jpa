#!/usr/bin/env bash
# Check for exercise 13-komentarze-bez-posta (the description is printed below)
# Run: ./cwiczenia/13-komentarze-bez-posta/check.sh (from any directory). Compiles the exercise and runs its tests.
CW_DIR=$(cd "$(dirname "$0")" && pwd)
source "$CW_DIR/../../scripts/lib.sh"
echo "Ćwiczenie 13-komentarze-bez-posta: Komentarze nie są powiązane z postem w bazie"
run_tests "$CW_DIR"
summary
