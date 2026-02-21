# TMinder Backend

An enterprise-grade TV Series tracking application built with **Spring Boot 3.4.2** and **Java 21**, following **Clean Architecture** principles.

---

## 🛠 Tech Stack
- **Language**: Java 21 (OpenJDK)
- **Framework**: Spring Boot 3.4.2
- **Build Tool**: Gradle 8.12.1 (Kotlin DSL)
- **Architecture**: Clean Architecture (Domain-Driven Design inspired)
- **Tools**: Docker, IntelliJ IDEA, Bruno (API Testing)

---

## 🚀 Setup Instructions

### 1. Prerequisites

#### **macOS**
Install tools via Homebrew:
```bash
brew install openjdk@21 gradle bruno docker
```

#### **Windows**
Install tools via [Scoop](https://scoop.sh/) (Recommended) or [Winget](https://github.com/microsoft/winget-cli):
```powershell
# Using Scoop
scoop install openjdk21 gradle bruno docker

# Using Winget
winget install Oracle.JDK.21 Gradle.Gradle UseBruno.Bruno Docker.DockerDesktop
```

### 2. Configure Java Environment

#### **macOS (Zsh/Bash)**
Add to your `~/.zshrc` or `~/.bashrc`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

#### **Windows (PowerShell)**
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Path\To\JDK\21", "User")
$env:Path += ";$env:JAVA_HOME\bin"
```
*(Note: Replace path with your actual installation directory, e.g., `C:\Users\<User>\scoop\apps\openjdk21\current`)*

### 3. IDE Setup (IntelliJ IDEA)
1. Open IntelliJ IDEA and select **Open**.
2. Navigate to the `tminder-backend` folder.
3. Trust the project and allow Gradle to finish importing.
4. Set the **Project SDK** to 21 in *Project Structure* (`Cmd + ;` on Mac, `Ctrl + Alt + Shift + S` on Windows).

---

## 🏗 Architecture Patterns

We follow **Clean Architecture** to ensure the business logic is independent of frameworks, UI, and databases.

- **`com.tminder.domain`**: The "Heart" of the system. Contains pure Java entities and repository interfaces. No Spring annotations.
- **`com.tminder.application`**: Business rules and use cases. Coordinates the data flow between the domain and the outside world.
- **`com.tminder.infrastructure`**: External implementation details (Persistence/Database, API Clients, External Services).
- **`com.tminder.api`**: Entry points for the application (Spring Controllers, DTOs, Web Configuration).

---

## 🏃 Running the Application

### Via Command Line
```bash
./gradlew bootRun
```

### Via IntelliJ
Open `TminderApplication.java` and click the **Green Play Arrow**.

### API Testing
Open **Bruno**, import the `TMinder API` collection located in the project root, and use it to send requests to `http://localhost:8080`.

---

## 🧪 Development Workflow
1. **Define Domain Entity**: Add core logic to `com.tminder.domain`.
2. **Define Repository Interface**: Add persistence contracts in `com.tminder.domain`.
3. **Implement Use Case**: Add logic to `com.tminder.application`.
4. **Implement Infrastructure**: Connect to the DB in `com.tminder.infrastructure`.
5. **Expose Endpoint**: Create a controller in `com.tminder.api`.
