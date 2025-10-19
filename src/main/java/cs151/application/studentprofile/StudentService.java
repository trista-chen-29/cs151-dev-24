package cs151.application.studentprofile;

import cs151.application.persistence.StudentProfileDAO;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

    private final StudentProfileDAO dao = new StudentProfileDAO();

    /**
     * Validates the Student object.
     * Returns an empty string if valid, or an error message otherwise.
     */
    public String validate(Student s) {
        List<String> errors = new ArrayList<>();

        if (s.getName() == null || s.getName().trim().isEmpty()) {
            return "Full name is required.";
        } else if (dao.existsByName(s.getName())) {
            return "Duplicate entry name.";
        }

        if (s.getAcademicStatus() == null) {
            return "Academic status must be selected.";
        }
        if (s.getProfessionalRole() == null) {
            return "Preferred professional role must be selected.";
        }

        if (s.getEmploymentStatus() && (s.getJobDetails() == null || s.getJobDetails().trim().isEmpty())) {
            return "Job details are required if employed.";
        }


        return "";
    }

    public void save(Student s) {
        StudentProfileDAO.insert(s.getName(), s.getAcademicStatus(), s.getEmploymentStatus(), s.getJobDetails(), s.getProfessionalRole(),
                s.getWhiteList(), s.getBlackList(), s.getLanguages(), s.getStudentDbs(),
                s.getComments());
    }
}
