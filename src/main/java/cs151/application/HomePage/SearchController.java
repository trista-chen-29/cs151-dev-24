package cs151.application.HomePage;

import cs151.application.ProgrammingLanguages.Language;
import cs151.application.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


// Waiting for DatabaseConnector. DatabaseConnector must include getConnection() method to implement connection.


public class SearchController {

    /**
     * filterstudent() filters students by their name, programming languages known, databases known, and professional interest.
     * if blacklistedAllowed == true, then student blacklist would also be searched.
     *
     * @param searchQuery
     * @param languages
     * @param databases
     * @param interests
     * @param blacklistedAllowed
     * @return list of student
     * @throws SQLException
     */
    public List<Student> filterstudents(
            String searchQuery,
            List<String> languages,
            List<String> databases,
            List<String> interests,
            Boolean blacklistedAllowed,
            List<Student> allStudents
    ) {

        List<Student> result = new ArrayList<>();

        for (Student student : allStudents) {

            // Filter by name (case-insensitive substring)
            if (searchQuery != null && !searchQuery.isEmpty()) {
                if (!student.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    continue;
                }
            }

            // Filter by blacklist status
            if (blacklistedAllowed != null) {
                if (blacklistedAllowed && !student.isBlacklisted()) continue;
                if (!blacklistedAllowed && student.isBlacklisted()) continue;
            }

            // Filter by languages
            if (languages != null && !languages.isEmpty()) {
                boolean hasLanguage = student.getProgrammingLanguages().stream()
                        .anyMatch(languages::contains);
                if (!hasLanguage) continue;
            }

            // Filter by databases
            if (databases != null && !databases.isEmpty()) {
                boolean hasDatabase = student.getDatabases().stream()
                        .anyMatch(databases::contains);
                if (!hasDatabase) continue;
            }

            // Filter by interests
            if (interests != null && !interests.isEmpty()) {
                boolean hasInterest = student.getInterests().stream()
                        .anyMatch(interests::contains);
                if (!hasInterest) continue;
            }

            // If all filters pass
            result.add(student);
        }

        return result;
    }

}
