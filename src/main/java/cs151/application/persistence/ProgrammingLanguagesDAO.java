package cs151.application.persistence;

import cs151.application.programminglanguages.Language;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingLanguagesDAO {
    /** Return all language names, alphabetically. */
    public List<String> listAll() {
        List<String> out = new ArrayList<>();
        String sql = "SELECT name FROM language_catalog ORDER BY name ASC";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
    /** Returns a List of Language objects in ascending aphabetical order. */
    public List<Language> listLanguageObjects(){
        List<Language> out = new ArrayList<>();
        String sql = "SELECT name FROM language_catalog ORDER BY LOWER(name) ASC";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(new Language(rs.getString("name")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
    /** Save a name; returns true if inserted, false if it already existed or invalid. */
    public boolean save(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return false;

        String sql = "INSERT OR IGNORE INTO language_catalog(name) VALUES (?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trimmed);
            return ps.executeUpdate() > 0;   // >0 means it inserted; 0 means it already existed
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
