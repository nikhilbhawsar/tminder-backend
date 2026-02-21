# Setup Guide

This guide covers the instructions to get the TMinder Backend running on your local machine.

## 🛠 Prerequisites

### macOS
Install tools via Homebrew:
```bash
brew install openjdk@21 gradle bruno docker
```

### Windows
Install tools via [Scoop](https://scoop.sh/) or [Winget](https://github.com/microsoft/winget-cli):
```powershell
# Using Scoop
scoop install openjdk21 gradle bruno docker
```

---

## ⚙️ Environment Configuration

### macOS (Zsh/Bash)
Add to your `~/.zshrc` or `~/.bashrc`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

### Windows (PowerShell)
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Path\To\JDK\21", "User")
$env:Path += ";$env:JAVA_HOME\bin"
```

---

## 💻 IDE Setup (IntelliJ IDEA)
1. Open IntelliJ IDEA and select **Open**.
2. Navigate to the `tminder-backend` folder.
3. Trust the project and allow Gradle to finish importing.
4. Set the **Project SDK** to 21 in *Project Structure* (`Cmd + ;` on Mac, `Ctrl + Alt + Shift + S` on Windows).
