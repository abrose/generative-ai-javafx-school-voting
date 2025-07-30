# Test Plan for Voting Session

## Steps to test voting functionality:

1. **Start the application**
   ```
   mvn javafx:run
   ```

2. **Create a new session**
   - Enter class name (e.g., "Class 5A")
   - Click "Create New Session"

3. **Add parents**
   - Add at least 3-4 parents:
     - John Smith
     - Jane Doe  
     - Bob Johnson
     - Alice Williams

4. **Select candidates**
   - Check at least 2 parents as candidates (e.g., Jane Doe and Bob Johnson)
   - The "Start Voting" button should become enabled

5. **Start voting**
   - Click "Start Voting"
   - This should transition to the voting view (currently shows "Switching to voting view" in logs)

## Expected behavior:
- Session starts in SETUP status
- After clicking "Start Voting", status changes to VOTING
- View switches to voting interface (to be implemented)