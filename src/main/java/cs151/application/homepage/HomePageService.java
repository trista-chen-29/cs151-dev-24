package cs151.application.homepage;

import cs151.application.persistence.ProgrammingLanguagesDAO;
import java.util.List;



public class HomePageService {
    private final ProgrammingLanguagesDAO pl = new ProgrammingLanguagesDAO();

    public List<String> getProgrammingLanguages() { return pl.listAll(); }
    public List<String> getDatabases() { return List.of("MySQL","PostgreSQL","MongoDB","SQLite"); } // optional
}