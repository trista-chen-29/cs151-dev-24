package cs151.application.studentprofile;

import cs151.application.persistence.CommentDAO;

import java.util.List;

public class CommentsService {
    CommentDAO commentDAO = new CommentDAO();

    public List<Comment> listComments(long id){
       return commentDAO.listByStudent(id);
    }
    public boolean addComment(long id, String comment){
        return commentDAO.add(id, comment) != -1;
    }

}
