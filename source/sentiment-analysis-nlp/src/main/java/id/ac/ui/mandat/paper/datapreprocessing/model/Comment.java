package id.ac.ui.mandat.paper.datapreprocessing.model;

import java.util.List;

public class Comment {
    
    private String username;
    private String sentiment;
    private String comment;
    private List<SentenceAndClassDimension> sentenceAndClassDimensions;
    private int rowIndex;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public List<SentenceAndClassDimension> getSentenceAndClassDimensions() {
        return sentenceAndClassDimensions;
    }
    public void setSentenceAndClassDimensions(List<SentenceAndClassDimension> sentenceAndClassDimensions) {
        this.sentenceAndClassDimensions = sentenceAndClassDimensions;
    }
    public String getSentiment() {
        return sentiment;
    }
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getRowIndex() {
        return rowIndex;
    }
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
    

    
    
}
