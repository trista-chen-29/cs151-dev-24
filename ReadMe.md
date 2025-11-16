Here is the **clean, paste-ready README version** — **no Chinese, no explanation text**, only FINAL content.
You can copy and paste this **directly into your README.md**.

---

# Name of application: Students' Knowledgebase for Faculties

---

# Version: 0.8

## Overview

Prof-Support helps faculty manage students’ academic profiles, programming-language knowledge, and professional information.
This milestone (v0.8) adds the Add Comments feature, enabling faculty to view and store comments for each student with an auto-generated date stamp (date only).

## New in v0.8

* Added Add Comments functionality (new comment input + display pane).
* Implemented permanent comment storage using SQLite via new Comment DAO and Service layers.
* Updated View Student Profiles page to include a Comments section showing all stored comments.
* Added comments panel integration in the student profile detail view.
* Ensured all comments follow the required format: `YYYY-MM-DD — comment text`.
* Verified all 3 programming languages and 5 student profiles remain intact.
* Cleaned and packaged the project folder (`dev-24-0.8`) according to rubric.

## Who did what

1. Chenying Wang – Updated README for v0.8; verified UI against rubric requirements; ensured correct comment formatting and project consistency.
2. Trista Chen – Implemented Add Comments UI (FXML + Controller); designed interface for comment entry and comment history display.
3. Jaime Gonzalez – Implemented backend Comment Service & Comment DAO; added permanent SQLite persistence for comments.
4. Vraj Mistry – Performed full-system testing; cleaned project folder; verified package structure; prepared final submission (`dev-24-0.8`).

---

# Version History (v0.7 → v0.1)

## Version 0.7

* Added Edit Student Profile functionality.
* Improved Search Students Profiles with case-insensitive filters.
* Added whitelist/blacklist filters.
* Added Back navigation buttons.
* Verified SQLite persistence for profile updates.

## Version 0.6

* Added Search Students Profiles page.
* Added Delete Student Profile functionality.
* Implemented TableView to display students.
* Refined Home Page UI.
* Verified 3 programming languages and 5 student profiles.

## Version 0.5

* Added Define Student Profile page.
* Added View Student Profiles page (TableView).
* Implemented profile persistence using SQLite.
* Updated Home Page navigation.

## Version 0.4

* Completed Functional Specification Draft.
* Created 3 mockups (Define PL, Home, Define Student Profile).
* Completed Technical Specification Draft.
* Added data model, UML class diagram, and sequence diagram.

## Version 0.3

* Implemented Define Programming Languages page.
* Added permanent storage for programming languages.
* Displayed languages in an A–Z sorted TableView.
* Cleaned project folder and added README.

## Version 0.2

* Imported starter project template (dev-00-0.2).
* Added navigation to Define Programming Languages page.
* Allowed entering programming language names.
* Ensured project compiles with Zulu 23.

## Version 0.1

* Wrote first Functional Specification Draft.
* Created required mockups (Define PL, Home, Define Student Profile).
* Wrote Technical Specification Draft (data model + UML diagrams).
* Defined initial system architecture.

---

# Technical-Spec

1. Application Architecture: JavaFX + SQLite; Maven build; Zulu JDK 23.
2. Data Persistence: SQLite database for languages, student profiles, and comments.
3. Navigation: All pages include a Back or Home button.
4. Main Class: `cs151.application.Main`.
5. Tools: JavaFX, FXML, DAO/Service structure.

---

# Functional-Spec

1. Version 0.8:

   * View and add comments with date-only timestamps.
   * Permanent comment storage.

2. Version 0.6:

   * Search and filter profiles.
   * Delete student profiles.

3. Earlier versions:

   * Define programming languages.
   * Define and update student profiles.
   * Store academic status, job status, roles, and skills.

---

# Any other instruction that users need to know

* Ensure the project folder contains only source code before zipping (no `.idea`, `.git`, `.mvn`, `target`, etc.).
* After submission, re-download and verify that the project runs as expected.
* All comments must use date-only format (`YYYY-MM-DD`).
* SQLite database must be included or generated automatically on first run.



## Name of application: Prof-Support

**Version: 0.7**

---

### Overview

Prof-Support is a JavaFX application that helps faculty manage students’ academic profiles, programming-language knowledge, and professional data in a simple and persistent way using SQLite.

**This milestone (v0.7)** focuses on the **Edit Students Profiles** implementation and required submission structure. It builds directly on v0.6, maintaining all previous features while introducing full profile editing and saving, refined search, and packaging cleanup.

---

### New in v0.7

* Added **Edit Students Profiles** functionality
  – From the Search page, users can open a profile, modify fields, and permanently save changes to SQLite.
* Confirmed **5 student profiles** and **3 programming languages** pre-populated via `StudentSeeder.java`.
* Added **Back navigation buttons** on all pages for consistent user flow.
* Improved **Search Students Profiles** with case-insensitive match and smoother UI interaction.
* Verified data persistence and SQLite foreign-key integrity.
* Cleaned the project folder (`dev-24-0.7`) according to the rubric — removed `.idea`, `.mvn`, `target`, and other non-source files.
* Updated **README** to document new version and “who did what.”

---

### Who did what

| Member             | Contribution                                                                                                             |
| :----------------- | :----------------------------------------------------------------------------------------------------------------------- |
| **Chenying Wang**  | Populated 3 Programming Languages and 5 Student Profiles; updated README (v0.7); verified UI and navigation consistency. |
| **Trista Chen**    | Implemented **Edit Students Profiles** functionality and page logic; connected UI fields to DB update operations.        |
| **Vraj Mistry**    | Assisted in implementing **Edit Students Profiles** backend (DAO update methods and validation).                         |
| **Jaime Gonzalez** | Cleaned and packaged project (`dev-00-0.7`); verified folder structure and zipping; confirmed SQLite dependency in POM.  |

---

### Tech Stack

* Java 23 (Zulu 23)
* JavaFX 23 with Maven (`javafx-maven-plugin 0.0.8`)
* SQLite JDBC (3.46.1.0)
* SceneBuilder / FXML
* CSS for UI styling

---

### Build Instructions

```bash
# Run from project root
mvn clean javafx:run
```

---

### Using the App (v0.7)

#### Home Page

* Launch screen that provides navigation to:

  * **Search Students Profiles**
  * **Define Programming Languages**
  * **Add/Edit Student Profile**

#### Search Students Profiles

* Displays all stored profiles in a TableView.
* Search by name, email, or major (case-insensitive).
* Selecting a student opens the **Edit Profile** page for modifications.

#### Edit Student Profile

* Allows editing of any student information (except ID).
* Saves changes permanently to SQLite using `StudentProfileDAO`.
* Includes a **Back** button to return to the search list.

#### Define Programming Languages

* Manage 3 programming languages pre-populated by the seeder.
* Add or modify entries using the JavaFX TableView interface.

---

### Known Limitations (v0.7)

* Comment feature not implemented (per rubric note).
* Advanced multi-criteria search still under development.
* Profile pictures remain static (default image only).

---

### Where Data is Stored

SQLite DB file:
`./dev-24-0.7/profsupport.db`

Seeder class:
`StudentSeeder.java` (replaces old `DevSeeder.java`)

---

### Project Structure

```
src/main/java/cs151/application
 ├─ AppState.java
 ├─ Main.java
 ├─ homepage/
 │   ├─ HomePageController.java
 │   ├─ HomePageService.java
 ├─ persistence/
 │   ├─ DatabaseConnector.java
 │   ├─ DbInit.java
 │   ├─ StudentSeeder.java
 │   ├─ ProgrammingLanguagesDAO.java
 │   ├─ StudentProfileDAO.java
 ├─ programminglanguages/
 │   ├─ Language.java
 │   ├─ PLController.java
 └─ studentprofile/
     ├─ Student.java
     ├─ StudentProfileController.java
     ├─ ViewStudentProfileController.java
     ├─ StudentDirectoryService.java
     ├─ StudentService.java
     ├─ Comment.java
     ├─ StudentRow.java

src/main/resources/cs151/application
 ├─ homepage.fxml
 ├─ define-pl.fxml
 ├─ define-student-profile.fxml
 ├─ view-student-profile.fxml
 ├─ homepage.css
 ├─ define-pl.css
 ├─ define-student-profile.css
 ├─ view-student-profile.css
 └─ theme.css
```

---

### Packaging (Rubric Compliance)

* Project folder renamed to `dev-00-0.7` before submission.
* All non-source files removed.
* README updated per rubric item 6.
* Verified inclusion of SQLite JDBC dependency in `pom.xml`.
* Zipped as `dev-00-0.7.zip`, ensuring folder structure is preserved after extraction.

---





## Name of application: Prof-Support

**Version: 0.6**

---

### Overview

Prof-Support helps faculty (professors) manage students’ profile data, academic skills, and programming-language knowledge.

**This milestone (v0.6)** adds the **“Search Students Profiles”** page, allows deleting profiles from the database, and refines overall UI and structure based on previous milestones (v0.5 → v0.6).

---

### New in v0.6

* Added **Search Students Profiles** page with filters (by name, language, database, and preferred roles)
* Implemented **JavaFX TableView** to display all students in tabular format
* Enabled **Delete Profile** feature – permanently removes selected student from SQLite DB
* Refined **Home Page UI** with active search components and linked navigation
* Verified 3 Programming Languages and 5 Student Profiles pre-loaded
* Cleaned project folder (`dev-24-0.6`) and updated README
* General restructuring and test verification

---

### Who did what

| Member             | Contribution                                                                                                                                      |
| :----------------- | :------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Trista Chen**    | Restructured project folders and code organization; cleaned non-source files; verified Maven build and zipping process.                           |
| **Jaime Gonzalez** | Implemented **Delete Student Profile** feature using TableView selection; ensured DB row removal and UI refresh after deletion.                   |
| **Vraj Mistry**    | Implemented **Search Students Profiles page** (front-end and backend filter logic); added TableView bindings; managed testing and zipped release. |
| **Chenying Wang**  | Populated 3 Programming Languages and 5 Student Profiles; updated README (v0.6); verified UI layout and navigation consistency.                   |

---

### Tech Stack

* Java 23 (Zulu 23)
* JavaFX 23 with Maven (`javafx-maven-plugin 0.0.8`)
* SQLite JDBC (3.46.1.0)
* SceneBuilder / FXML
* CSS for UI styling

---

### Build Instructions

```bash
# Run from project root
mvn clean javafx:run
```

---

### Using the App (v0.6)

#### Home Page

* Provides quick navigation to **Define Programming Languages**, **Add/Edit Student**, and **Search Students Profiles**.
* Includes filter panel (search by name, language, database, and preferred roles) with All / Whitelist / Blacklist toggle.

#### Define Programming Languages

* Add or update programming languages (e.g., C++, Java, Python) via JavaFX TableView.
* Entries are stored in SQLite DB and sorted A → Z.

#### Define Student Profile

* Create or update student profiles with academic status, job status, and preferred roles.
* Persist data to SQLite DB after validation.

#### Search Students Profiles

* Displays all stored profiles in a TableView with real-time filtering.
* Clicking a row shows student details and skills.
* Supports deletion of selected student profile.

---

### Known Limitations (v0.6)

* Advanced filter combinations (e.g., multi-criteria queries) still in progress.
* Photo upload feature not yet linked to storage.

---

### Where Data is Stored

SQLite DB file location:
`./dev-24-0.6/profsupport.db`

To reset data: close the app and delete this file.

---

### Project Structure

```
src/main/java/cs151/application
 ├─ Main.java
 ├─ homepage/
 │   ├─ HomePageController.java
 │   ├─ HomePageService.java
 │   ├─ SearchService.java
 ├─ persistence/
 │   ├─ DatabaseConnector.java
 │   ├─ DbInit.java
 ├─ programminglanguages/
 │   ├─ PLController.java
 │   ├─ ProgrammingLanguagesDAO.java
 │   ├─ Language.java
 └─ studentprofile/
     ├─ Student.java

src/main/resources/cs151/application
 ├─ homepage.fxml
 ├─ define-pl.fxml
 ├─ define-student.fxml
 ├─ view-student.fxml
 ├─ homepage.css
 ├─ define-pl.css
 └─ theme.css
```

---



---

## Name of application: Prof-Support

**Version: 0.5**

---

### Overview

Prof-Support helps faculty (professors) manage students’ profile data, academic skills, and programming-language knowledge.

**This milestone (v0.5)** add *“define student profile”* and *“View Student Profile”* pages.

---

### New in v0.5

* Added **“Define Student Profile”** page
* Added **“View Student Profile”** page (with tableview)
* Added persistence for student profiles
* Updated HomePage UI

---

### Who did what

| Member             | Contribution                                                                                                                              |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Trista Chen**    | Created FXML and Controller for “View Student Profiles” page, created DAO for controllers                                                 |
| **Jaime Gonzalez** | Implemented “StudentProfileController”, verified ViewStudentProfileController, updated ReadMe                                             |
| **Vraj Mistry**    | Implemented StudentService, changed location of database, cleaned and zipped files for submission                                         |
| **Chenying Wang**  | Implemented and verified **Student Profile UI v2**, removed legacy **“Other”**, added CSS linking, and validated navigation functionality |

---

## Tech Stack

* Java 23 (Zulu 23)
* JavaFX 23 with Maven (`javafx-maven-plugin 0.0.8`)
* SQLite JDBC (3.46.1.0)
* SceneBuilder / FXML
* CSS for UI styling

---

## Build Instructions

```bash
# Run from project root
mvn clean javafx:run
```


## Using the app (v0.5)
- Define Students Profile:
  - Navigate to Define Students Profile page through HomePage
  - Input all required fields as per the problem statement
  - On clicking save: app verifies all required fields are inputted and persists student profile in SQLite database.
  - App shows errors if there are missing inputs
- View Student Profile:
  - Navigate to "View Student Profiles" page through HomePage
  - Left of UI shows a TableView of all defined students
  - On clicking a desired student in the TableView right of UI will show all other details of student
  - You can optionally sort the students by whitelist blacklist or all.
---

## Known limitations (v0.5)
- Home search/filters are stubbed; full backend hookup is planned for the next milestone.
- Adding a student photo does nothing.

## Where data is stored
SQLite DB file has moved from:

~/.profsupport/profsupport.db
to
./dev-24-0.5/profsupport.db

To reset the data, close the app and delete that file.


---- Older Versions ReadMe ----
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
