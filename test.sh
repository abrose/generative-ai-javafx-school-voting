#!/bin/bash

# ABOUTME: Enhanced test runner script with colored output and better formatting
# ABOUTME: Provides a cleaner test experience with progress indicators and summaries

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Unicode symbols
CHECK_MARK="✅"
CROSS_MARK="❌"
HOURGLASS="⏳"
ROCKET="🚀"
TROPHY="🏆"
TARGET="🎯"
GEAR="⚙️"
CHART="📊"

echo -e "${BLUE}${BOLD}"
echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║                       ${ROCKET} School Voting System - Test Suite ${ROCKET}                      ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "${CYAN}${HOURGLASS} Starting test execution...${NC}\n"

START_TIME=$(date +%s)

# Create a temporary file to capture Maven output
TEMP_FILE=$(mktemp)

# Run Maven tests and capture output
if mvn test > "$TEMP_FILE" 2>&1; then
    BUILD_SUCCESS=true
else
    BUILD_SUCCESS=false
fi

# Process the output
while IFS= read -r line; do
    if [[ $line == *"Running com.school.voting"* ]]; then
        # Extract just the test class name
        TEST_CLASS=$(echo "$line" | sed 's/.*Running com\.school\.voting\.//' | sed 's/com\.school\.voting\.//')
        echo -e "${PURPLE}${TARGET} Testing: ${BOLD}${TEST_CLASS}${NC}"
    elif [[ $line == *"Running "*" "*" and Error Conditions"* ]] || [[ $line == *"Running "*" "*" Integration"* ]] || [[ $line == *"Running "*" "*" Workflow"* ]]; then
        NESTED_CLASS=$(echo "$line" | sed 's/.*Running //' | sed 's/.*Test //')
        echo -e "  ${CYAN}${GEAR} ${NESTED_CLASS}${NC}"
    elif [[ $line == *"Tests run:"* ]] && [[ $line == *"Time elapsed:"* ]]; then
        if [[ $line == *"Failures: 0"* ]] && [[ $line == *"Errors: 0"* ]]; then
            COUNT=$(echo "$line" | grep -o "Tests run: [0-9]*" | grep -o "[0-9]*")
            echo -e "    ${GREEN}${CHECK_MARK} ${COUNT} tests passed ${GREEN}$(echo "$line" | grep -o "Time elapsed: [0-9.]*[^,]*")${NC}"
        else
            echo -e "    ${RED}${CROSS_MARK} ${line}${NC}"
        fi
    elif [[ $line == *"Results:"* ]]; then
        echo -e "\n${YELLOW}${BOLD}${CHART} RESULTS SUMMARY:${NC}"
    elif [[ $line =~ ^Tests\ run:.*,\ Failures:.*,\ Errors:.*,\ Skipped: ]] && [[ $line != *"Time elapsed:"* ]]; then
        # Final summary line  
        if [[ $line == *"Failures: 0"* ]] && [[ $line == *"Errors: 0"* ]]; then
            echo -e "${GREEN}${BOLD}${TROPHY} ${line}${NC}"
        else
            echo -e "${RED}${BOLD}${CROSS_MARK} ${line}${NC}"
        fi
    elif [[ $line == *"Total time:"* ]]; then
        echo -e "${CYAN}${line}${NC}"
    fi
done < "$TEMP_FILE"

# Clean up
rm "$TEMP_FILE"

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo -e "\n${BLUE}${BOLD}"
if [ "$BUILD_SUCCESS" = true ]; then
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                           ${CHECK_MARK} Test Execution Complete ${CHECK_MARK}                          ║"
    echo "║                              Duration: ${DURATION} seconds                               ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
else
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                           ${CROSS_MARK} Test Execution Failed ${CROSS_MARK}                           ║"
    echo "║                              Duration: ${DURATION} seconds                               ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
fi
echo -e "${NC}"

# Exit with same code as Maven
if [ "$BUILD_SUCCESS" = true ]; then
    exit 0
else
    exit 1
fi