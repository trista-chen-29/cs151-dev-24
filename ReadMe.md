# Name of application: Prof-Support

**Version: 0.5 (Student Profile Final Fix)**

---

## 📘 Overview
Prof-Support helps faculty (professors) manage students’ profile data, academic skills, and programming-language knowledge.

**This milestone (v0.5)** finalizes the Student Profile module, refines the navigation between pages, and unifies the JavaFX theme.

### ✅ New in v0.5
- Removed legacy **“Other”** option from `studentprofile.fxml`
- Added new `student-profile.css` for modular UI styling
- Verified `HomePageController` navigation for full-scene switch
- Attached both `theme.css` and `student-profile.css` in FXML
- Maven build tested successfully with **Zulu JDK 23**

### 🔍 Local Verification
| Test | Command | Expected | Result |
|------|----------|-----------|---------|
| FXML check | `Select-String -Path "studentprofile.fxml" -Pattern "Other" -Quiet` | False | ✅ Pass |
| CSS exists | `Test-Path "student-profile.css"` | True | ✅ Pass |
| Build run | `mvn clean javafx:run` | BUILD SUCCESS | ✅ Pass |

---

## 🧠 Feature Summary
- Navigate between **Home**, **Programming Languages**, and **Student Profile**
- Store and retrieve data from **SQLite**
- Maintain consistent **JavaFX styling** across all scenes
- Implement modular controllers and verified navigation logic

---

## 👥 Who did what
| Member | Contribution |
|---------|---------------|
| **Trista Chen** | Organized packages, cleaned files, wrote README, handled packaging/zip for submission |
| **Jaime Gonzalez** | Converted Define-PL to TableView (from ListView) |
| **Vraj Mistry** | Refreshed HomePage UI and clarified *Add Programming Languages* navigation |
| **Chenying Wang** | Implemented and verified **Student Profile UI v2**, removed legacy *Other*, added CSS linking, and validated navigation functionality |

---

## 🧰 Tech Stack
- Java 23 (Zulu 23)
- JavaFX 23 with Maven (`javafx-maven-plugin 0.0.8`)
- SQLite JDBC (3.46.1.0)
- SceneBuilder / FXML
- CSS for UI styling

---

## 🧾 Build Instructions
```bash
# Run from project root
mvn clean javafx:run


# Name of application: Prof-Support
---
### Version: 0.3
---
## Overview
Prof-Support helps faculty (professor) manage student info and skills.

This milestone focuses on the Programming Languages feature and the Home screen shell:
- Navigate Home → Define Programming Languages and back.
- Add a programming language; it’s saved to SQLite.
- Languages are shown in a JavaFX TableView, sorted A→Z.
- Basic Home screen UI with “Add Programming Languages” entry point.

---
## Who did what:
1. Trista Chen - reorganized packages, cleaned files, wrote README, packaging/zip for submission.
2. Jaime Gonzalez - converted Define-PL to TableView (from ListView).
3. Vraj Mistry - refreshed the Homepage UI and made “Add Programming Languages” navigation clear.
4. Chenying Wang - ensured language names are sorted alphabetically (ascending).

---
## Tech stack
- JDK: Zulu 23 (Java 23)
- Build: Maven
- UI: JavaFX (FXML + CSS)
- DB: SQLite (via org.xerial:sqlite-jdbc)
- Modules: module-info.java configured for JavaFX + JDBC
---
## Project structure
src/main/java/cs151/application
├─ Main.java
├─ homepage/               
│   ├─ HomePageController.java  # UI controllers (Home)
│   ├─ HomePageService.java
│   └─ SearchService.java
├─ persistence/  # DB connector, schema, DAO
│   ├─ DatabaseConnector.java
│   ├─ DbInit.java
│   └─ ProgrammingLanguagesDAO.java
├─ programminglanguages/
│   ├─ PLController.java  # UI controllers (Define Programming Language)
│   └─ Language.java
└─ studentprofile/
    └─ Student.java 

src/main/resources/cs151/application
├─ homepage.fxml
├─ define-pl.fxml
├─ homepage.css
├─ define-pl.css
└─ theme.css

---
## How to run
## Prerequisites
- Install Zulu JDK 23.
- Ensure Maven is available (mvn -v).

### Run (Maven)
```bash
mvn clean javafx:run
```

### What happens on startup
- DbInit.ensureSchema() creates tables if missing.
- The app opens homepage.fxml.
- Use the “Add Programming Languages” link to go to Define Programming Language Page.

---
## Using the app (v0.3)
- Define Programming Languages
  - Enter a name (e.g., Java) → Save.
  - Duplicate names are ignored by the DAO.
  - The table refreshes and stays A→Z.
- Home
  - UI shell with search/filters. (Search integration will grow in later versions.)
  - Click the top link to open Define-PL.

---
## Where data is stored
SQLite DB file lives in your user home (created automatically):
```bash
~/.profsupport/profsupport.db
```
To reset the data, close the app and delete that file.

---
## Known limitations (v0.3)
- Home search/filters are stubbed; full backend hookup is planned for the next milestone.
- Student profile TableView not yet implemented (model exists).

---

# Name of application: Prof-Support
# Version: 0.2

# who did what:
A. UI/Navigation - Trista Chen

B. Define Page Behavior - Jaime Gonzalez & Vraj Mistry

C. Optional Persistence - Trista Chen & Chenying Wang

## Define Programming Language Page (UI Demo) - Chenying Wang

This page allows the user to input a programming language, creator, year, and a sample "Hello World" example.

**FXML File:** `hello-view.fxml`  
**Controller:** `MainController.java`  
**Main Class:** `Main.java`

**Result:**  
When running `mvn javafx:run`, the following JavaFX window appears:
<img width="1922" height="937" alt="prat C" src="https://github.com/user-attachments/assets/8e9f6988-a62e-4bb6-bc8b-841c1961cbf5" />
````markdown
### Part C – Optional Persistence (UI + Controller Integration)

**FXML File:** `hello-view.fxml`  
**Controller:** `MainController.java`  
**Main Class:** `Main.java`

#### MainController.java (Excerpt)
```java
@FXML
private TextField nameField;
@FXML
private TextField creatorField;
@FXML
private TextField yearField;
@FXML
private TextArea exampleArea;

@FXML
private void handleSave(ActionEvent event) {
    try {
        String name = nameField.getText();
        String creator = creatorField.getText();
        String year = yearField.getText();
        String example = exampleArea.getText();

        System.out.println("Saved: " + name + " by " + creator + " (" + year + ")");
        System.out.println("Example:\n" + example);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data saved successfully!");
        alert.showAndWait();
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save data.");
        alert.showAndWait();
    }
}
````

#### hello-view.fxml (Excerpt)

```xml
<GridPane fx:controller="cs151.application.MainController"
          xmlns:fx="http://javafx.com/fxml" alignment="CENTER" hgap="10" vgap="10">
    <TextField fx:id="nameField" promptText="Language Name"/>
    <TextField fx:id="creatorField" promptText="Creator"/>
    <TextField fx:id="yearField" promptText="Year"/>
    <TextArea  fx:id="exampleArea" promptText="Hello World Example"/>
    <Button text="Save" onAction="#handleSave"/>
</GridPane>
```

```
```






# D. Build/Packaging + README + QA — Chenying Wang
<img width="2047" height="1213" alt="part D hellow" src="https://github.com/user-attachments/assets/529f6206-3c29-4d3c-b146-cfc6cc262c74" />



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
