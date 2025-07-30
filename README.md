# School Voting System 🗳️

A professional JavaFX desktop application for conducting parent spokesperson elections in schools. This system provides a complete voting workflow from setup to results with PDF export capabilities.

![Java](https://img.shields.io/badge/Java-20-orange?style=flat-square&logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-3.45-green?style=flat-square&logo=sqlite)
![Maven](https://img.shields.io/badge/Maven-3.9+-red?style=flat-square&logo=apachemaven)
![Tests](https://img.shields.io/badge/Tests-76%20passing-brightgreen?style=flat-square)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Technology Stack](#technology-stack)
- [Development Setup](#development-setup)
- [Testing](#testing)
- [Contributing](#contributing)
- [API Reference](#api-reference)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## 🎯 Overview

The School Voting System is a desktop application designed to facilitate democratic parent spokesperson elections in schools. It provides a secure, user-friendly interface for conducting elections with features like candidate management, sequential voting, real-time progress tracking, and professional PDF report generation.

### 🎪 What This System Does

1. **Session Management**: Create and manage voting sessions for different classes
2. **Parent Management**: Add parents and designate candidates
3. **Sequential Voting**: Parents vote one-by-one in a controlled environment
4. **Results Calculation**: Automatic winner/deputy determination with tie handling
5. **PDF Export**: Generate professional election reports
6. **Self-Voting Support**: Candidates can vote for themselves
7. **Skip Functionality**: Handle absent parents gracefully

## ✨ Features

### 🔧 Core Functionality
- ✅ **Complete Voting Workflow**: Setup → Voting → Results → Export
- ✅ **Multi-Class Support**: Separate sessions for different classes
- ✅ **Sequential Voting**: One parent votes at a time
- ✅ **Progress Tracking**: Real-time voting progress with visual indicators
- ✅ **Skip Voters**: Handle absent parents without breaking workflow
- ✅ **Self-Voting**: Candidates can vote for themselves
- ✅ **Session Recovery**: Resume interrupted sessions after app restart

### 📊 Results & Reporting
- ✅ **Winner/Deputy Selection**: Automatic determination based on vote counts
- ✅ **Tie Detection**: Clear indication of tied results
- ✅ **Professional PDF Reports**: Formatted results with statistics
- ✅ **Voting Statistics**: Turnout rates and participation metrics
- ✅ **Complete Results Table**: All candidates with vote counts and percentages

### 🛡️ Data Management
- ✅ **SQLite Database**: Local data storage with ACID compliance
- ✅ **Data Validation**: Input validation and constraint enforcement
- ✅ **Session State Persistence**: Maintains state across app restarts
- ✅ **Double-Vote Prevention**: Database constraints prevent vote duplication

### 🎨 User Experience
- ✅ **Professional UI**: Clean, intuitive JavaFX interface
- ✅ **Responsive Design**: Adapts to different screen sizes
- ✅ **Touch-Friendly**: Large buttons suitable for touch screens
- ✅ **Error Handling**: User-friendly error messages and recovery
- ✅ **Confirmation Dialogs**: Prevents accidental actions

## 📸 Screenshots

*Note: Add screenshots here showing the main interfaces*

### Admin Panel
- Parent management interface
- Candidate selection
- Session creation

### Voting Interface
- Current voter display
- Candidate selection buttons
- Progress indicators

### Results View
- Winner announcement
- Complete results table
- Export options

## 🚀 Quick Start

### For End Users

1. **Download & Install Java 20+**
   ```bash
   # Check if Java is installed
   java --version
   ```

2. **Clone & Run**
   ```bash
   git clone <repository-url>
   cd school-voting
   ./mvnw javafx:run    # On Windows: mvnw.cmd javafx:run
   ```

3. **Start Voting**
   - Create a new session with class name
   - Add parents (or use pre-populated class "6c")
   - Select candidates (minimum 2 required)
   - Start voting and follow the workflow

### For Developers

```bash
# Clone repository
git clone <repository-url>
cd school-voting

# Run tests
./mvnw test

# Run with enhanced test output
npm run test

# Start application
./mvnw javafx:run
```

## 📋 Prerequisites

### System Requirements
- **Operating System**: Windows 10+, macOS 10.14+, or Linux
- **Java**: OpenJDK/Oracle JDK 20 or higher
- **Memory**: 512MB RAM minimum, 1GB recommended
- **Storage**: 100MB free space

### Development Requirements
- **Java Development Kit**: JDK 20+
- **Maven**: 3.9+ (or use included wrapper)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git**: For version control

### Optional Tools
- **Node.js**: For npm-style commands (testing)
- **PDF Viewer**: For viewing exported results

## 🔧 Installation

### Method 1: Using Maven Wrapper (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd school-voting

# Make wrapper executable (Unix/macOS)
chmod +x mvnw

# Run the application
./mvnw javafx:run

# On Windows
mvnw.cmd javafx:run
```

### Method 2: Using System Maven

```bash
# Ensure Maven 3.9+ is installed
mvn --version

# Clone and run
git clone <repository-url>
cd school-voting
mvn javafx:run
```

### Method 3: IDE Setup

1. **IntelliJ IDEA**:
   - File → Open → Select project directory
   - Wait for Maven import to complete
   - Run configuration: Main class `com.school.voting.MainApp`
   - VM options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

2. **Eclipse**:
   - File → Import → Existing Maven Projects
   - Browse to project directory
   - Import project
   - Right-click project → Run As → Java Application

3. **VS Code**:
   - Install "Extension Pack for Java"
   - Open project folder
   - F5 to run or use integrated terminal

## 📖 Usage Guide

### 1. Session Setup

**Creating a New Session:**
1. Launch the application
2. Enter class name (e.g., "6c", "7a")
3. Click "Create New Session"

**Adding Parents:**
1. Enter parent name in the text field
2. Click "Add Parent" or press Enter
3. Repeat for all parents in the class

**Selecting Candidates:**
1. Check the checkbox next to candidate names
2. Minimum 2 candidates required
3. Parents can be both voters and candidates

### 2. Voting Process

**Starting Voting:**
1. Ensure at least 2 candidates are selected
2. Click "Start Voting"
3. System transitions to voting interface

**Conducting Votes:**
1. Current voter name is displayed prominently
2. Voter selects their preferred candidate
3. Confirmation dialog appears
4. System automatically advances to next voter

**Handling Special Cases:**
- **Skip Voter**: Click "Skip" if parent is absent
- **End Early**: Use "End Voting" to conclude before all parents vote
- **Self-Voting**: Candidates can vote for themselves

### 3. Results and Export

**Viewing Results:**
1. Voting automatically transitions to results
2. Winner and deputy (runner-up) are highlighted
3. Complete results table shows all candidates
4. Voting statistics display turnout information

**Exporting Results:**
1. Click "Export Results" button
2. Choose to open PDF immediately or save
3. PDF includes:
   - Session information
   - Winner and deputy announcement
   - Complete results table
   - Voting statistics

### 4. Session Management

**Continuing Interrupted Sessions:**
- App automatically detects existing sessions
- Shows appropriate action buttons:
  - "Continue Voting" for in-progress sessions
  - "View Results" for completed sessions
  - "Reset Session" to start fresh

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │      Data       │
│     Layer       │◄──►│     Logic       │◄──►│     Layer       │
│   (JavaFX UI)   │    │   (Services)    │    │   (SQLite)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Design Patterns Used

**1. Model-View-Controller (MVC)**
- **Models**: `Parent`, `Vote`, `VotingSession` (immutable data objects)
- **Views**: FXML files defining UI layout
- **Controllers**: Handle user interactions and business logic

**2. Data Access Object (DAO)**
- **ParentDAO**: Database operations for parents
- **VoteDAO**: Database operations for votes  
- **VotingSessionDAO**: Database operations for sessions

**3. Singleton Pattern**
- **SessionManager**: Global session state management
- **DatabaseManager**: Single database connection point

**4. Builder Pattern**
- **All Models**: Immutable objects with builder construction
- **Example**: `Parent.builder().name("John").sessionId(1).build()`

**5. Factory Pattern**
- **ViewFactory**: Creates and manages JavaFX views

### Component Interaction Flow

```
User Action → Controller → SessionManager → DAO → Database
     ↓              ↓           ↓         ↓        ↓
UI Update ← View ← Model ← Business Logic ← Data
```

## 📁 Project Structure

```
school-voting/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/school/voting/
│   │   │       ├── controller/          # UI Controllers
│   │   │       │   ├── AdminController.java
│   │   │       │   ├── VotingController.java
│   │   │       │   └── ResultsController.java
│   │   │       ├── dao/                 # Data Access Objects
│   │   │       │   ├── DatabaseManager.java
│   │   │       │   ├── ParentDAO.java
│   │   │       │   ├── VoteDAO.java
│   │   │       │   └── VotingSessionDAO.java
│   │   │       ├── model/               # Data Models
│   │   │       │   ├── Parent.java
│   │   │       │   ├── Vote.java
│   │   │       │   └── VotingSession.java
│   │   │       ├── util/                # Utility Classes
│   │   │       │   ├── SessionManager.java
│   │   │       │   ├── PdfExportService.java
│   │   │       │   └── DatabaseInitializer.java
│   │   │       ├── view/                # View Management
│   │   │       │   └── ViewFactory.java
│   │   │       └── MainApp.java         # Application Entry Point
│   │   └── resources/
│   │       ├── fxml/                    # UI Layouts
│   │       │   ├── admin.fxml
│   │       │   ├── voting.fxml
│   │       │   └── results.fxml
│   │       ├── css/                     # Stylesheets
│   │       │   └── styles.css
│   │       └── db/                      # Database Scripts
│   │           └── schema.sql
│   └── test/
│       ├── java/                        # Test Classes
│       │   └── com/school/voting/
│       │       ├── model/               # Model Tests
│       │       │   ├── VoteTest.java
│       │       │   ├── ParentTest.java
│       │       │   └── VotingSessionTest.java
│       │       └── VotingSystemIntegrationTest.java
│       └── resources/
│           └── junit-platform.properties
├── target/                              # Build Output
├── docs/                                # Documentation
├── .gitignore
├── pom.xml                              # Maven Configuration
├── package.json                         # NPM-style Commands
├── test.sh                              # Enhanced Test Runner
├── TESTING.md                           # Testing Guide
└── README.md                            # This File
```

### Key Directories Explained

**`src/main/java/com/school/voting/`**
- **`controller/`**: JavaFX controllers handling UI interactions
- **`dao/`**: Database access layer with CRUD operations
- **`model/`**: Immutable data objects representing business entities
- **`util/`**: Utility classes for session management and services
- **`view/`**: View factory for managing JavaFX scenes

**`src/main/resources/`**
- **`fxml/`**: JavaFX FXML layout files
- **`css/`**: Application stylesheets
- **`db/`**: Database schema and initialization scripts

**`src/test/`**
- **Unit tests** for models and business logic
- **Integration tests** for complete workflows
- **Test configuration** files

## 🗃️ Database Schema

### Tables Overview

```sql
-- Voting Sessions Table
CREATE TABLE voting_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_name TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'SETUP',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT chk_status CHECK (status IN ('SETUP', 'VOTING', 'COMPLETED'))
);

-- Parents Table  
CREATE TABLE parents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    session_id INTEGER NOT NULL,
    is_candidate BOOLEAN DEFAULT 0,
    has_voted BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES voting_sessions(id) ON DELETE CASCADE
);

-- Votes Table
CREATE TABLE votes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    voter_id INTEGER NOT NULL,
    candidate_id INTEGER NOT NULL,
    session_id INTEGER NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voter_id) REFERENCES parents(id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES parents(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES voting_sessions(id) ON DELETE CASCADE,
    UNIQUE(voter_id, session_id) -- Prevents double voting
);
```

### Entity Relationships

```
VotingSession (1) ─────┐
                       │
                       ▼
                   Parent (N)
                       │
                   ┌───┴───┐
                   ▼       ▼
                Voter   Candidate
                   │       ▲
                   └───────┘
                    Vote (N:1)
```

### Key Constraints

1. **Referential Integrity**: All foreign keys with CASCADE DELETE
2. **Unique Voting**: One vote per voter per session
3. **Status Validation**: Session status must be SETUP/VOTING/COMPLETED
4. **Required Fields**: Names and session IDs cannot be null

## 🛠️ Technology Stack

### Core Technologies

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Java** | 20+ | Core language | [Oracle Docs](https://docs.oracle.com/en/java/) |
| **JavaFX** | 21.0.2 | Desktop UI framework | [OpenJFX Docs](https://openjfx.io/javadoc/21/) |
| **SQLite** | 3.45.0 | Embedded database | [SQLite Docs](https://www.sqlite.org/docs.html) |
| **Maven** | 3.9+ | Build system | [Maven Docs](https://maven.apache.org/guides/) |

### Key Libraries

| Library | Version | Purpose | License |
|---------|---------|---------|---------|
| **iText** | 8.0.2 | PDF generation | AGPL/Commercial |
| **SLF4J** | 2.0.12 | Logging framework | MIT |
| **JUnit 5** | 5.10.1 | Testing framework | EPL 2.0 |
| **TestFX** | 4.0.18 | JavaFX testing | BSD |

### Development Tools

- **Build**: Maven 3.9+ with wrapper
- **Testing**: JUnit 5 with custom test runner
- **Code Quality**: Built-in validation and error handling
- **Database**: SQLite JDBC driver
- **PDF**: iText 8 Core modules

### Why These Technologies?

**Java 20 + JavaFX 21**
- ✅ **Cross-platform**: Runs on Windows, macOS, Linux
- ✅ **Rich UI**: Native desktop experience
- ✅ **Performance**: Compiled bytecode execution
- ✅ **Ecosystem**: Mature libraries and tools

**SQLite**
- ✅ **Zero-config**: No server setup required
- ✅ **ACID compliance**: Data integrity guaranteed
- ✅ **Portable**: Single file database
- ✅ **Fast**: Optimized for local access

**Maven**
- ✅ **Dependency management**: Automatic library handling
- ✅ **Build lifecycle**: Standardized build process
- ✅ **IDE integration**: Works with all major IDEs
- ✅ **Plugin ecosystem**: Extensive plugin support

## 🔧 Development Setup

### 1. Environment Setup

**Install Java Development Kit**

```bash
# Check current Java version
java --version
javac --version

# If Java 20+ not installed:
# - Download from https://jdk.java.net/20/
# - Or use package manager:

# macOS (Homebrew)
brew install openjdk@20

# Ubuntu/Debian
sudo apt install openjdk-20-jdk

# Windows (Chocolatey)
choco install openjdk20
```

**Configure JAVA_HOME**

```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_HOME=/path/to/java20
export PATH=$JAVA_HOME/bin:$PATH

# Windows (System Properties → Environment Variables)
JAVA_HOME = C:\Program Files\Java\jdk-20
PATH = %JAVA_HOME%\bin;%PATH%
```

### 2. Project Setup

**Clone and Initialize**

```bash
# Clone repository
git clone <repository-url>
cd school-voting

# Verify Maven wrapper
./mvnw --version

# Install dependencies and compile
./mvnw clean compile

# Run tests
./mvnw test

# Start application
./mvnw javafx:run
```

### 3. IDE Configuration

**IntelliJ IDEA Setup**

1. **Import Project**:
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Configure JavaFX**:
   - File → Project Structure → Libraries
   - Add JavaFX 21 if not auto-detected

3. **Run Configuration**:
   - Main class: `com.school.voting.MainApp`
   - VM options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

4. **Enable Annotation Processing**:
   - Settings → Build → Compiler → Annotation Processors
   - Enable annotation processing

**VS Code Setup**

1. **Install Extensions**:
   - Extension Pack for Java
   - JavaFX Support (optional)

2. **Configure Settings**:
   ```json
   {
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-20",
         "path": "/path/to/jdk20"
       }
     ],
     "java.compile.nullAnalysis.mode": "automatic"
   }
   ```

### 4. Database Setup

**Automatic Initialization**
- Database is created automatically on first run
- Located at: `./voting_system.db`
- Pre-populated with sample class "6c" (15 parents)

**Manual Database Operations**

```bash
# Connect to database
sqlite3 voting_system.db

# View tables
.tables

# View schema
.schema

# Query data
SELECT * FROM voting_sessions;
SELECT * FROM parents WHERE session_id = 1;
```

### 5. Build Verification

**Complete Build Test**

```bash
# Clean build
./mvnw clean

# Compile and test
./mvnw compile test

# Package (creates JAR)
./mvnw package

# Run packaged application
java -jar target/school-voting-1.0.0.jar
```

## 🧪 Testing

### Test Overview

The project includes **76 comprehensive tests** covering:

- ✅ **Unit Tests**: Individual component testing
- ✅ **Integration Tests**: End-to-end workflow testing  
- ✅ **Business Logic Tests**: Core functionality validation
- ✅ **Model Tests**: Data object validation

### Running Tests

**Enhanced Test Runner (Recommended)**

```bash
# Colorful, formatted output
./test.sh

# Or using npm-style command
npm run test
```

**Standard Maven Testing**

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=VoteTest

# Run specific test method
./mvnw test -Dtest=VoteTest#shouldCreateVoteWithValidData

# Quiet output
./mvnw test -q
```

### Test Structure

**Model Tests (69 tests)**
```
VoteTest (17 tests)
├── Vote creation and validation
├── Self-voting support
├── Equality and hashing
└── Immutability verification

ParentTest (22 tests)  
├── Parent creation with builder
├── Validation rules
├── Business logic
└── Data integrity

VotingSessionTest (30 tests)
├── Session lifecycle
├── Status transitions
├── Builder pattern
└── Workflow validation
```

**Integration Tests (7 tests)**
```
VotingSystemIntegrationTest
├── Complete voting workflows
├── Self-voting scenarios
├── Edge cases and error conditions
└── State transition validation
```

### Test Configuration

**JUnit Configuration** (`src/test/resources/junit-platform.properties`)
```properties
junit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
junit.jupiter.execution.parallel.enabled=false
junit.platform.output.capture.stdout=true
junit.platform.output.capture.stderr=true
```

**Maven Surefire Configuration**
- Enhanced reporting with colored output
- Detailed test execution information
- Failed test debugging support

### Writing New Tests

**Test Class Template**

```java
package com.school.voting.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

class NewFeatureTest {
    
    @Nested
    @DisplayName("Feature Group Description")
    class FeatureGroup {
        
        @Test
        @DisplayName("Should do something specific")
        void shouldDoSomethingSpecific() {
            // Given
            // When  
            // Then
        }
    }
}
```

**Testing Best Practices**

1. **Arrange-Act-Assert**: Clear test structure
2. **Descriptive Names**: Use `@DisplayName` for clarity
3. **Test Groups**: Use `@Nested` for organization
4. **Edge Cases**: Test boundary conditions
5. **Error Scenarios**: Verify exception handling

## 🤝 Contributing

### Getting Started

1. **Fork the Repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/yourusername/school-voting.git
   cd school-voting
   git remote add upstream https://github.com/original/school-voting.git
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Changes and Test**
   ```bash
   # Make your changes
   ./mvnw test  # Ensure all tests pass
   ./test.sh    # Run enhanced test suite
   ```

4. **Commit and Push**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request**
   - Go to GitHub and create a pull request
   - Describe your changes clearly
   - Reference any related issues

### Code Style Guidelines

**Java Code Standards**

```java
// Class naming: PascalCase
public class VotingController {
    
    // Constants: UPPER_SNAKE_CASE
    private static final String DEFAULT_STATUS = "SETUP";
    
    // Fields: camelCase with access modifier
    private final SessionManager sessionManager;
    
    // Methods: camelCase with descriptive names
    public void handleStartVoting() {
        // Method implementation
    }
    
    // Builder pattern for immutable objects
    Parent parent = Parent.builder()
            .name("John Doe")
            .sessionId(1)
            .build();
}
```

**FXML Guidelines**

```xml
<!-- Use fx:id for all interactive elements -->
<Button fx:id="submitBtn" text="Submit" onAction="#handleSubmit"/>

<!-- Apply consistent styling -->
<Button styleClass="primary-button" prefWidth="200"/>

<!-- Group related elements -->
<VBox spacing="10" styleClass="input-group">
    <Label text="Parent Name:"/>
    <TextField fx:id="parentNameField"/>
</VBox>
```

**CSS Conventions**

```css
/* Use semantic class names */
.primary-button {
    -fx-background-color: #27ae60;
    -fx-text-fill: white;
}

/* Follow JavaFX property naming */
.panel {
    -fx-background-color: white;
    -fx-border-color: #ddd;
    -fx-border-radius: 5;
}
```

### Development Workflow

**Before Starting Work**

1. **Check Issues**: Look for existing issues or create one  
2. **Discuss Changes**: For major features, discuss in issues first
3. **Update Dependencies**: Ensure you have latest versions
4. **Run Full Test Suite**: Verify starting point is clean

**During Development**

1. **Follow TDD**: Write tests before implementation when possible
2. **Commit Often**: Small, focused commits with clear messages
3. **Test Continuously**: Run tests frequently during development
4. **Update Documentation**: Keep README and comments current

**Before Submitting**

1. **Full Test Run**: `./mvnw clean test`
2. **Code Review**: Self-review your changes
3. **Manual Testing**: Test the actual UI workflows
4. **Documentation**: Update relevant documentation

### Contribution Areas

**🐛 Bug Fixes**
- Check GitHub issues labeled "bug"
- Provide clear reproduction steps
- Include fix with test case

**✨ New Features**
- Propose features in GitHub discussions
- Follow existing architecture patterns
- Include comprehensive tests
- Update documentation

**📚 Documentation**
- Improve code comments
- Add JavaDoc documentation
- Update README sections
- Create tutorials or guides

**🧪 Testing**
- Add test coverage for uncovered code
- Create integration tests
- Improve test reliability
- Add performance tests

**🎨 UI/UX Improvements**
- Enhance visual design
- Improve accessibility
- Add responsive behaviors
- Create better error messages

### Code Review Process

**Pull Request Requirements**

- ✅ **All tests pass**: CI must be green
- ✅ **Code coverage**: New code should include tests  
- ✅ **Documentation**: Update relevant docs
- ✅ **Clean commits**: Squash/rebase if needed
- ✅ **Description**: Clear PR description with context

**Review Checklist**

- [ ] Code follows project conventions
- [ ] Tests cover new functionality
- [ ] No breaking changes (or properly documented)
- [ ] Performance impact considered
- [ ] Security implications reviewed
- [ ] Documentation updated

## 📚 API Reference

### Core Models

#### Parent

```java
public class Parent {
    // Immutable model representing a parent/voter
    
    // Factory method
    public static Builder builder()
    
    // Properties
    public Integer getId()
    public String getName()
    public boolean isCandidate()
    public boolean hasVoted()
    public Integer getSessionId()
    public LocalDateTime getCreatedAt()
    
    // Business logic
    public boolean canVote()
    
    // Builder pattern
    public Builder toBuilder()
}
```

**Usage Example:**
```java
Parent parent = Parent.builder()
    .name("John Doe")
    .sessionId(1)
    .isCandidate(true)
    .build();
```

#### Vote

```java
public class Vote {
    // Immutable model representing a vote
    
    // Constructor
    public Vote(Integer voterId, Integer candidateId, Integer sessionId)
    
    // Properties
    public Integer getId()
    public Integer getVoterId()
    public Integer getCandidateId()
    public Integer getSessionId()
    public LocalDateTime getVotedAt()
}
```

#### VotingSession

```java
public class VotingSession {
    // Immutable model representing a voting session
    
    public enum Status {
        SETUP("Setup Phase"),
        VOTING("Voting in Progress"), 
        COMPLETED("Voting Completed")
    }
    
    // Factory method
    public static Builder builder()
    
    // Properties
    public Integer getId()
    public String getClassName()
    public Status getStatus()
    public LocalDateTime getCreatedAt()
    public LocalDateTime getCompletedAt()
    
    // Business logic
    public boolean isActive()
    public boolean canStartVoting()
    public boolean isVoting()
}
```

### Data Access Objects (DAOs)

#### ParentDAO

```java
public class ParentDAO {
    // CRUD operations for parents
    
    public Integer insertParent(Parent parent) throws SQLException
    public void updateParent(Parent parent) throws SQLException
    public Optional<Parent> getParentById(int id) throws SQLException
    public List<Parent> getParentsBySession(int sessionId) throws SQLException
    public List<Parent> getCandidatesBySession(int sessionId) throws SQLException
    public List<Parent> getVotersBySession(int sessionId) throws SQLException
    
    // Status updates
    public void markAsCandidate(int parentId, boolean isCandidate) throws SQLException
    public void markAsVoted(int parentId) throws SQLException
    
    // Statistics
    public int getParentCount(int sessionId) throws SQLException
    public int getVotedCount(int sessionId) throws SQLException
    
    // Cleanup
    public void deleteParent(int parentId) throws SQLException
    public void deleteParentsBySession(int sessionId) throws SQLException
}
```

#### VoteDAO

```java
public class VoteDAO {
    // CRUD operations for votes
    
    public Integer recordVote(Vote vote) throws SQLException
    public List<Vote> getVotesBySession(int sessionId) throws SQLException
    public Map<Integer, Integer> getVoteCountsBySession(int sessionId) throws SQLException
    public int getVoteCount(int candidateId, int sessionId) throws SQLException
    public int getTotalVotes(int sessionId) throws SQLException
    public boolean hasVoted(int voterId, int sessionId) throws SQLException
    
    // Cleanup
    public void deleteVotesBySession(int sessionId) throws SQLException
}
```

#### VotingSessionDAO

```java
public class VotingSessionDAO {
    // CRUD operations for voting sessions
    
    public Integer createSession(VotingSession session) throws SQLException
    public void updateSessionStatus(int sessionId, VotingSession.Status status) throws SQLException
    public void completeSession(int sessionId) throws SQLException
    public Optional<VotingSession> getCurrentSession() throws SQLException
    public List<VotingSession> getAllSessions() throws SQLException
    
    // Cleanup
    public void deleteSession(int sessionId) throws SQLException
}
```

### Business Logic Services

#### SessionManager (Singleton)

```java
public class SessionManager {
    // Central session management
    
    public static SessionManager getInstance()
    
    // Session lifecycle
    public VotingSession createNewSession(String className) throws SQLException
    public void startVoting() throws SQLException
    public void completeSession() throws SQLException
    public void resetSession() throws SQLException
    public void clearSession()
    
    // Current state
    public VotingSession getCurrentSession()
    public boolean hasActiveSession()
    
    // Voting workflow
    public Parent getCurrentVoter()
    public Parent getNextVoter() throws SQLException
    public void skipCurrentVoter() throws SQLException
    public void recordVote(Integer candidateId) throws SQLException
    
    // Statistics
    public int getTotalParentCount() throws SQLException
    public int getVotedCount() throws SQLException
    public int getRemainingVoterCount() throws SQLException
}
```

#### PdfExportService

```java
public class PdfExportService {
    // PDF generation service
    
    public File exportResults(VotingSession session) throws Exception
}
```

### Controllers

#### AdminController

```java
public class AdminController implements Initializable {
    // Admin panel controller
    
    public void setViewFactory(ViewFactory viewFactory)
    
    // FXML event handlers
    @FXML private void handleCreateSession()
    @FXML private void handleAddParent()
    @FXML private void handleStartVoting()
    @FXML private void handleResetSession()
    @FXML private void handleContinueVoting()
    @FXML private void handleViewResults()
}
```

#### VotingController

```java
public class VotingController implements Initializable {
    // Voting interface controller
    
    public void setViewFactory(ViewFactory viewFactory)
    
    // FXML event handlers
    @FXML private void handleVoteForCandidate()
    @FXML private void handleSkipVoter()
    @FXML private void handleEndVoting()
}
```

#### ResultsController

```java
public class ResultsController implements Initializable {
    // Results display controller
    
    public void setViewFactory(ViewFactory viewFactory)
    
    // FXML event handlers
    @FXML private void handleNewSession()
    @FXML private void handleExportResults()
}
```

### Utility Classes

#### DatabaseManager (Singleton)

```java
public class DatabaseManager {
    // Database connection management
    
    public static DatabaseManager getInstance()
    public Connection getConnection() throws SQLException
    public void closeConnection()
}
```

#### ViewFactory

```java
public class ViewFactory {
    // JavaFX view management
    
    public ViewFactory(Stage primaryStage)
    public void showAdminView()
    public void showVotingView()
    public void showResultsView()
    public Stage getPrimaryStage()
}
```

## 🔍 Troubleshooting

### Common Issues

#### 1. JavaFX Module Issues

**Problem**: `Error: JavaFX runtime components are missing`

**Solution**:
```bash
# Option 1: Use Maven JavaFX plugin (recommended)
./mvnw javafx:run

# Option 2: Add VM arguments
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar app.jar

# Option 3: Set environment variable
export PATH_TO_FX=/path/to/javafx/lib
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar app.jar
```

#### 2. Database Connection Issues

**Problem**: `SQLException: database is locked`

**Solution**:
```bash
# Check if database file exists and is writable
ls -la voting_system.db

# Ensure no other instances are running
ps aux | grep java

# Delete and recreate database (loses data!)
rm voting_system.db
./mvnw javafx:run
```

**Problem**: `Database file not found`

**Solution**:
```bash
# Ensure you're in the project root directory
pwd
# Should show: /path/to/school-voting

# Check if database files exist
ls -la *.db

# Let the app create database automatically
./mvnw javafx:run
```

#### 3. Build Issues

**Problem**: `No compiler is provided in this environment`

**Solution**:
```bash
# Verify JDK (not JRE) is installed
javac -version

# Set JAVA_HOME to JDK directory
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH

# Use Maven wrapper
./mvnw clean compile
```

**Problem**: `Maven dependencies not downloading`

**Solution**:
```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Force update
./mvnw clean compile -U

# Check proxy settings in ~/.m2/settings.xml if behind corporate firewall
```

#### 4. Runtime Issues

**Problem**: Application crashes on startup

**Check logs**:
```bash
# Run with verbose logging
./mvnw javafx:run -Dlogging.level.com.school.voting=DEBUG

# Check for stack traces in console output
# Common issues:
# - Missing JavaFX modules
# - Database permissions
# - Font loading issues
```

**Problem**: PDF export fails

**Solution**:
```bash
# Check write permissions in current directory
ls -la .

# Ensure iText dependencies are available
./mvnw dependency:tree | grep itext

# Check if Desktop.open() is supported
# (Some headless systems don't support desktop integration)
```

#### 5. UI Issues

**Problem**: Buttons or interface elements not responding

**Check**:
1. FXML file syntax (missing fx:id or onAction)
2. Controller method signatures (must be public void)
3. ViewFactory initialization
4. JavaFX Application Thread usage

**Problem**: Styling not applied

**Check**:
1. CSS file location in resources
2. CSS selectors match FXML styleClass
3. JavaFX CSS property names (use -fx- prefix)

### Platform-Specific Issues

#### Windows

**Issue**: Application won't start
- Install Microsoft Visual C++ Redistributable
- Check Windows Defender exceptions
- Run as Administrator if permission issues

**Issue**: Database access denied
- Check file permissions
- Disable real-time antivirus scanning for project folder

#### macOS

**Issue**: "App is damaged" message
- Allow app in Security & Privacy settings
- Use `xattr -d com.apple.quarantine app.jar` to remove quarantine

**Issue**: JavaFX fonts look incorrect
- Install Java from Oracle or use system OpenJDK
- Check display scaling settings

#### Linux

**Issue**: Missing JavaFX libraries
```bash
# Install OpenJFX
sudo apt install openjfx  # Ubuntu/Debian
sudo yum install java-openjfx  # RedHat/CentOS
```

**Issue**: Database permissions
```bash
# Ensure user has write permissions
chmod 755 .
chmod 644 voting_system.db  # if exists
```

### Debug Mode

**Enable detailed logging**:
```bash
# Set system property for debug output
./mvnw javafx:run -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

# Or create logback.xml in src/main/resources:
```

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.school.voting" level="DEBUG"/>
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### Getting Help

**If you're still stuck**:

1. **Check GitHub Issues**: Search existing issues for similar problems
2. **Create New Issue**: Provide full error messages and system information
3. **Include Information**:
   - Operating system and version
   - Java version (`java --version`)
   - Maven version (`./mvnw --version`)
   - Full error stack trace
   - Steps to reproduce

**System Information Template**:
```bash
# Run this and include output in issue reports
echo "OS: $(uname -a)"
echo "Java: $(java --version)"
echo "JavaFX: $(find /usr -name "javafx*" 2>/dev/null | head -5)"
echo "Maven: $(./mvnw --version | head -1)"
echo "Current Dir: $(pwd)"
echo "Files: $(ls -la | head -10)"
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Third-Party Licenses

- **iText**: AGPL/Commercial license for PDF generation
- **JavaFX**: GPL v2 with Classpath Exception  
- **SQLite**: Public Domain
- **SLF4J**: MIT License
- **JUnit**: Eclipse Public License 2.0

---

## 🙏 Acknowledgments

- **JavaFX Community** for excellent desktop framework
- **SQLite Team** for reliable embedded database
- **iText Developers** for professional PDF generation
- **Maven Team** for build system excellence
- **JUnit Contributors** for testing framework

---

**Made with ❤️ for democratic school governance**

*This README was crafted to help developers of all backgrounds contribute to making school elections more accessible and transparent.*