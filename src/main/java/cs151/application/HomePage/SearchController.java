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
     * @param Languages
     * @param Databases
     * @param Interests
     * @param blacklistedAllowed
     * @return list of student
     * @throws SQLException
     */
    public List<Student> filterstudent (String searchQuery, List<Language> Languages, List<String> Databases, List<String> Interests, Boolean blacklistedAllowed ) throws SQLException {

        List<Student> result = new ArrayList<>();

        try(Connection conn = DatabaseConnector.getConnection()){
            String sql = "SELECT * FROM student WHERE 1=1";
            if(searchQuery != null && !searchQuery.isEmpty()){
                sql += " AND name LIKE ?";
            }
            if(blacklistedAllowed != null){
                sql += " AND isBlacklisted = ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            int  i = 1;
            if(searchQuery != null && !searchQuery.isEmpty()){
                stmt.setString(i++, searchQuery);
            }
            if(blacklistedAllowed != null){
                stmt.setString(i++, blacklistedAllowed.toString());
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("id");
                String studentName = rs.getString("name");
                boolean Blacklisted = rs.getInt("blacklisted") == 1;

                // Fetch associated lists
                List<String> studentLangs = getStudentAttributes(conn, "programming_languages", "language", studentId);
                List<String> studentDbs = getStudentAttributes(conn, "databases", "database_name", studentId);
                List<String> studentInterests = getStudentAttributes(conn, "interests", "interest", studentId);

                // Check filter matches
                if (!Languages.isEmpty() && studentLangs.stream().noneMatch(Languages::contains)) continue;
                if (!Databases.isEmpty() && studentDbs.stream().noneMatch(Databases::contains)) continue;
                if (!Interests.isEmpty() && studentInterests.stream().noneMatch(Interests::contains)) continue;

                result.add(new Student(studentId, studentName, Blacklisted,
                        studentLangs, studentDbs, studentInterests));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    /*
    Returns a list of all student that have an attribute from the database. Supporter method to filterstudent() method.
     */
    private List<String> getStudentAttributes(Connection conn, String table, String column, int studentId) throws SQLException {
        List<String> attributes = new ArrayList<>();
        String sql = "SELECT " + column + " FROM " + table + " WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attributes.add(rs.getString(column));
            }
        }
        return attributes;
    }
}
