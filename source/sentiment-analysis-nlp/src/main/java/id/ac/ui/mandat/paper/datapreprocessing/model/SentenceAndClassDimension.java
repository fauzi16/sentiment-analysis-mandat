package id.ac.ui.mandat.paper.datapreprocessing.model;

public class SentenceAndClassDimension {
    
    private String sentence;
    private String dimension;

    public SentenceAndClassDimension(String sentence, String dimension) {
        this.sentence = sentence;
        this.dimension = dimension;
    }

    public String getSentence() {
        return sentence;
    }
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    public String getDimension() {
        return dimension;
    }
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    
}
