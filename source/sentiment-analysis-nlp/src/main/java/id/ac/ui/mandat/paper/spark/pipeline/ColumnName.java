package id.ac.ui.mandat.paper.spark.pipeline;

public class ColumnName {
    
    public static final String CLASSIFICATION = "classification";
    public static final String CLASSIFICATION_NO = "classification_no";
    public static final String SENTIMENT = "sentiment";
    public static final String SENTIMENT_NO = "sentiment_no";
    public static final String DOCUMENT = "document";
    public static final String TOKENIZED = "tokenized";
    public static final String DUPLICATE_REMOVED = "duplicate_removed";
    public static final String LEMMATIZED = "lemmatized";
    public static final String STOPWORDREMOVAL = "stopword_removal";
    
    public static final String TF = "raw_feature_tf";
    public static final String TFIDF = "tfidf";
    public static final String PREDICTION = "prediction";

    public static String ngram(int ngram){
        return "ngrams_" + ngram;
    }

}
