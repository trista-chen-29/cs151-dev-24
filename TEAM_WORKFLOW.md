# 🛠️ Team Workflow Guide
*(For team use only – not for professor submission)*

This document explains how we collaborate, use branches, and keep the repo clean.

---

## 🌳 Branching Strategy
- **main** → Always stable. Used for professor grading.  
- **ui** → UI layouts (FXML, navigation).  
- **define-pl** → Define Programming Languages page logic.  
- **persist** → Data persistence (SQLite).  
- **release** → Final integration/QA before merging to `main`.  

📌 **Rule**: Nobody commits directly to `main`. All changes come from Pull Requests.

---

## 🔄 Daily Workflow

### 1. Update your local repo before working
```bash
git checkout main
git pull
git checkout your-branch
git merge main
```

⚠️ Always update to the latest code before pushing.

### 2. Work on your feature branch
```bash
git checkout your-branch
# make changes
git add .
git commit -m "Describe what you changed"
```

### 3. Push your branch
```bash
git push
```

### 4. Open a Pull Request (PR)
- When your feature is ready:
  - Go to GitHub → Open a **Pull Request** from your branch → `release`.  
  - Another teammate reviews and approves.  
  - Merge into `release` once the code builds and runs correctly.  

- The **release branch** is used for integration and testing.  
  - Everyone’s work (UI, define-pl, persist) is combined here first.  
  - If conflicts or bugs appear, they are fixed in `release`.  

- When `release` is stable and tested:
  - Open a **final Pull Request** from `release` → `main`.  
  - Only the assigned reviewer merges into `main`.  
  - `main` should always be **professor-ready** (clean and working).

---

## 📍 Things to Watch Out For
- Don’t push to main directly.
- Test your code locally before committing.
- Don’t commit build/IDE junk:
  - target/
  - .idea/
  - *.iml
- Use meaningful commit messages:
  - ✅ Add FXML for Define PL page
  - ❌ stuff works
- Keep ReadMe.md clean for professor submission.
- Use this file (TEAM_WORKFLOW.md) for internal notes.

---

## 📦 Submission Checklist
Before turning in:
- `[ ]` All features merged into main.
- `[ ]` Project compiles and runs on Zulu 23.
- `[ ]` target/, .idea/, *.iml (non-source-code) removed.
- `[ ]` ReadMe.md updated with who did what.
- `[ ]` Zip dev-24-0.2 folder.
- `[ ]` Test unzip → import → run before submission.

---

🔀 Simple Branch Flow
```css
feature branches → release → main
```
Example:
```sql
ui --------┐
define-pl -┤ → release → main
persist ---┘
```

---

✅ Follow this guide and everyone can collaborate smoothly without overwriting each other’s work.
