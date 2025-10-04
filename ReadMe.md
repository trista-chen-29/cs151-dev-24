# Name of application: 
# Version: 0.2

# who did what:
A. UI/Navigation - Trista Chen

B. Define Page Behavior - Jaime Gonzalez & Vraj Mistry

C. Optional Persistence - Trista Chen

D. Build/Packaging + README + QA - Chenying Wang

# D. Build/Packaging + README + QA — Chenying Wang

## HelloWorld (CS151 v0.2)

### Version
0.2

### Who Did What
- **A. UI/Navigation** – Trista Chen  
- **B. Define Page Behavior** – Jaime Gonzalez & Vraj Mistry  
- **C. Optional Persistence** – Trista Chen  
- **D. Build/Packaging + README + QA** – Chenying Wang

### Prerequisites
- Zulu JDK 23 (23.0.1)
- Maven 3.x
- IntelliJ IDEA (any recent version)

### Environment
- Zulu JDK 23 (23.0.1)
- Maven 3.x
- IntelliJ IDEA

## How to Run
1. In IntelliJ, set **Project SDK = Zulu JDK 23**.  
2. From the project root, run:
   ```bash
   mvn clean javafx:run
````

**Expected result:** A JavaFX window titled **Hello Chenying Wang** with a button.

## Packaging for submission

Create a clean zip named **dev-00-0.2.zip** that contains only:

* `src/`
* `pom.xml`
* `ReadMe.md`

Exclude: `.idea/`, `target/`, `out/`, `.git/`, `.mvn/`, `mvnw`, `mvnw.cmd`.

### Windows (PowerShell)

```powershell
cd <project-root>
New-Item -ItemType Directory dev-00-0.2 | Out-Null
Copy-Item -Recurse -Force .\src .\dev-00-0.2\
Copy-Item -Force .\pom.xml .\dev-00-0.2\
Copy-Item -Force .\ReadMe.md .\dev-00-0.2\
Compress-Archive -Path .\dev-00-0.2 -DestinationPath .\dev-00-0.2.zip -Force
```

## Troubleshooting

* **Module not found:** ensure `src/main/java/module-info.java` module name matches the **left** side of `<mainClass>` in `pom.xml` (currently `s25.cs151.application/...`). Reload Maven changes and run again.
* Warnings about native access or terminal digits in the module name can be ignored for v0.2.


