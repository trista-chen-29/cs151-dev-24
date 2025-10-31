package cs151.application.studentprofile;

import cs151.application.persistence.ProgrammingLanguagesDAO;
import cs151.application.persistence.StudentProfileDAO;

import java.util.*;
import java.util.stream.Collectors;

public class DefineStudentProfileService {

    private final StudentProfileDAO students = new StudentProfileDAO();
    private final ProgrammingLanguagesDAO langCatalog = new ProgrammingLanguagesDAO();

    /** Result wrapper for controllers */
    public static final class Result {
        public final boolean ok;
        public final String message;
        public final long id;
        private Result(boolean ok, String message, long id) {
            this.ok = ok; this.message = message; this.id = id;
        }
        public static Result ok(long id) { return new Result(true, "Saved.", id); }
        public static Result fail(String msg) { return new Result(false, msg, -1); }
    }

    /** CREATE: validate + normalize + duplicate-name check + insert */
    public Result create(Student raw) {
        Student s = normalize(raw);

        String err = validateWithoutUniqueness(s);
        if (!err.isEmpty()) return Result.fail(err);

        if (students.existsByName(s.getName())) {
            return Result.fail("A student with that name already exists.");
        }

        long id = students.insert(
                s.getName(),
                s.getAcademicStatus(),
                s.getEmploymentStatus(),
                s.getJobDetails(),
                s.getProfessionalRole(),
                s.getWhiteList(),
                s.getBlackList(),
                s.getLanguages(),
                s.getStudentDbs(),
                s.getComments()
        );
        return (id > 0) ? Result.ok(id) : Result.fail("Database error: failed to save.");
    }

    /** UPDATE: validate (no duplicate check here), normalize, then DAO update */
    public Result update(long id, Student raw) {
        if (id <= 0) return Result.fail("Invalid id.");

        Student s = normalize(raw);

        String err = validateWithoutUniqueness(s);
        if (!err.isEmpty()) return Result.fail(err);

        // Optional: enforce name uniqueness against other rows (allow same name for same id)
        if (students.existsByNameForOther(s.getName(), id)) {
            return Result.fail("Another student already uses that name.");
        }

        boolean ok = students.update(
                id,
                s.getName(),
                s.getBlackList(),
                s.getLanguages(),
                s.getStudentDbs()
        );
        return ok ? Result.ok(id) : Result.fail("Update failed.");
    }

    /* ---------------- Validation & normalization ---------------- */

    /** Business rules only; no DB writes. Uniqueness is handled per-operation. */
    private String validateWithoutUniqueness(Student s) {
        if (s == null) return "No data.";

        if (isBlank(s.getName())) return "Full name is required.";
        if (isBlank(s.getAcademicStatus())) return "Academic status must be selected.";
        if (isBlank(s.getProfessionalRole())) return "Preferred professional role must be selected.";

        if (s.getWhiteList() && s.getBlackList()) {
            return "Whitelist and Blacklist cannot both be selected.";
        }
        if (s.getEmploymentStatus() && (s.getJobDetails() == null || s.getJobDetails().trim().isEmpty())) {
            return "Job details are required if employed.";
        }

        // Optional catalog enforcement
        var allowed = langCatalog.listAll().stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        for (String l : s.getLanguages()) {
            if (l != null && !l.isBlank() && !allowed.contains(l.toLowerCase())) {
                return "Unknown programming language: " + l;
            }
        }
        return "";
    }

    /** Trim strings, default missing fields, de-dup lists (case-insensitive). */
    private Student normalize(Student in) {
        if (in == null) {
            return new Student(
                    "", "Other", "Freshman", "", false, false, false,
                    List.of(), List.of(), List.of()
            );
        }

        String name   = trim(in.getName());
        String role   = orDefault(trim(in.getProfessionalRole()), "Other");
        String status = orDefault(trim(in.getAcademicStatus()), "Freshman");

        String job = trim(in.getJobDetails());
        if (!in.getEmploymentStatus()) {
            job = ""; // strip job details when not employed
        }

        List<String> langs = dedupCI(in.getLanguages());
        List<String> dbs   = dedupCI(in.getStudentDbs());
        List<String> cmts  = (in.getComments() == null) ? List.of()
                : in.getComments().stream().map(this::trim).filter(s -> !s.isEmpty()).toList();

        return new Student(
                name, role, status, job,
                in.getBlackList(), in.getWhiteList(), in.getEmploymentStatus(),
                langs, dbs, cmts
        );
    }

    private static List<String> dedupCI(List<String> in) {
        if (in == null) return List.of();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        for (String raw : in) {
            if (raw == null) continue;
            String t = raw.trim();
            if (t.isEmpty()) continue;
            String key = t.toLowerCase();
            map.putIfAbsent(key, t);
        }
        return new ArrayList<>(map.values());
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String trim(String s) { return s == null ? "" : s.trim(); }
    private static String orDefault(String s, String def) { return isBlank(s) ? def : s; }
}