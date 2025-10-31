package cs151.application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public final class StudentProfileSeeder {
    private StudentProfileSeeder() {}

    public static void seedFiveIfEmpty() {
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM student");
             ResultSet rs = ps.executeQuery()) {

            int n = rs.next() ? rs.getInt(1) : 0;
            if (n > 0) {
                System.out.println("[DB] DevSeeder: skipped (students already exist).");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        StudentProfileDAO dao = new StudentProfileDAO();

        dao.insert(
                "Alice Chen", "Senior", true, "Intern @ Acme", "Front-End",
                true, false,
                List.of("Java", "Python", "C++"),
                List.of("MySQL", "SQLite"),
                List.of(
                        "Great front-end instincts and attention to detail.",
                        "Delivered a solid prototype demo this week."
                )
        );

        dao.insert(
                "Brian Lee", "Junior", false, "", "Back-End",
                false, false,
                List.of("C++", "Java"),
                List.of("Postgres", "MySQL"),
                List.of(
                        "Prefers backend tasks.",
                        "Studying database indexing strategies."
                )
        );

        dao.insert(
                "Cam Nguyen", "Graduate", true, "TA - CS Dept.", "Data",
                true, false,
                List.of("Python", "Java"),
                List.of("MongoDB", "Postgres"),
                List.of(
                        "Great progress on data course.",
                        "Presented a clean ETL mini-project."
                )
        );

        dao.insert(
                "Diego Torres", "Sophomore", true, "Full-stack project contributor", "Full-Stack",
                false, false,
                List.of("C++", "Python"),
                List.of("MySQL", "SQL Server"),
                List.of(
                        "Enjoys full-stack - needs more test coverage.",
                        "Code review went well - good PR descriptions."
                )
        );

        dao.insert(
                "Eva Park", "Senior", true, "Open-source contributor", "Other",
                false, false,
                List.of("Python", "Java"),
                List.of("SQLite", "MongoDB"),
                List.of(
                        "Strong communicator - helpful in lab sessions.",
                        "Interested in DevOps - exploring CI/CD basics."
                )
        );

        System.out.println("[DB] DevSeeder: inserted 5 default students.");
    }
}


