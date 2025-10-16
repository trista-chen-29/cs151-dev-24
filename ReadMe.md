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