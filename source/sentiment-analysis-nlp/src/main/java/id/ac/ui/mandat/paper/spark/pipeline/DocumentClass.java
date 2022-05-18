package id.ac.ui.mandat.paper.spark.pipeline;

public class DocumentClass {

    private static final String SENTIMENT_POSITIVE = "positive";
    private static final String SENTIMENT_NEGATIVE = "negative";

    private String document;
    private String classification;
    private Double classification_no;
    private String sentiment;
    private Double sentiment_no;

    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }
    public String getClassification() {
        return classification;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }
    public Double getClassification_no() {
        return classification_no;
    }
    public void setClassification_no(Double classification_no) {
        this.classification_no = classification_no;
    }
    public static String getSentimentPositive() {
        return SENTIMENT_POSITIVE;
    }
    public static String getSentimentNegative() {
        return SENTIMENT_NEGATIVE;
    }
    public String getSentiment() {
        return sentiment;
    }
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    public Double getSentiment_no() {
        return sentiment_no;
    }
    public void setSentiment_no(Double sentiment_no) {
        this.sentiment_no = sentiment_no;
    }
    
    
}
