package id.ac.ui.mandat.paper.datacollection;

public class UserComment {

    public UserComment(String username, StringBuilder comment) {
        this.username = username;
        this.comment = comment;
    }

    private String username;
    private StringBuilder comment;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public StringBuilder getComment() {
        return comment;
    }
    public void setComment(StringBuilder comment) {
        this.comment = comment;
    }

}
