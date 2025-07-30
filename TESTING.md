# Testing Guide

## Available Test Commands

### Enhanced Test Runner (Recommended)
```bash
# Beautiful, colored output with progress indicators
./test.sh
```

### NPM-style Commands
```bash
# Enhanced test runner (same as ./test.sh)
npm run test

# Standard Maven test output
npm run test:maven

# Quiet Maven test output
npm run test:quick
```

### Direct Maven Commands
```bash
# Full test output with build info
mvn test

# Quiet test output (minimal)
mvn test -q

# Run specific test class
mvn test -Dtest=VoteTest

# Run specific test method
mvn test -Dtest=VoteTest#shouldCreateVoteWithValidData
```

## Test Structure

- **VoteTest** (17 tests): Vote model validation, self-voting support, equality
- **ParentTest** (22 tests): Parent model validation, builder pattern, business logic  
- **VotingSessionTest** (30 tests): Session lifecycle, status transitions, workflow
- **VotingSystemIntegrationTest** (7 tests): End-to-end workflows, edge cases

## Test Features

✅ **Self-voting support** - Candidates can vote for themselves  
✅ **Comprehensive validation** - All business rules tested  
✅ **Builder pattern testing** - Immutable object construction  
✅ **Integration scenarios** - Complete voting workflows  
✅ **Edge case handling** - Error conditions and boundary cases  

## Output Examples

The enhanced test runner (`./test.sh`) provides:
- 🎯 Clear test class indicators
- ⚙️ Nested test group progress
- ✅ Pass/fail status with timing
- 📊 Final results summary
- 🏆 Success/failure indicators

Total: **76 tests** covering all business logic