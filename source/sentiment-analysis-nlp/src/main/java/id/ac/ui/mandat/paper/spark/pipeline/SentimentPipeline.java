package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SentimentPipeline extends BasePipeline {

    public void execSupervisedMachneLearning(String evaluatedColumn) throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(evaluatedColumn);

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> sentenceData = spark.createDataFrame(labeledDocumentResultHolder.getDocumentClasses(),
                DocumentClass.class);

        Tokenize tokenize = new Tokenize();
        Dataset<Row> tokenized = tokenize.exec(sentenceData);

        RemoveDuplicateToken removeDuplicateToken = new RemoveDuplicateToken();
        tokenized = removeDuplicateToken.exec(spark, tokenized);

        AddNotPrefixForSentiment addNotPrefixForSentiment = new AddNotPrefixForSentiment();
        tokenized = addNotPrefixForSentiment.exec(spark, tokenized);

        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        StopWordRemoval stopWordRemoval = new StopWordRemoval();
        stopWordRemoval.setInputColumn(ColumnName.LEMMATIZED);
        Dataset<Row> stopword = stopWordRemoval.exec(lemmatized);

        NGramm nGramm = new NGramm();
        nGramm.setInputColumn(ColumnName.STOPWORDREMOVAL);
        Dataset<Row> ngrammResult = nGramm.exec(stopword, 4);

        Dataset<Row> tfidf = TFIDF.exec(ngrammResult, 4);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(0.2);

        trainingset.show();
        testset.show();

        String modelSaveLocation = this.modelLocation("sample_case", evaluatedColumn);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrain(trainingset, testset, modelSaveLocation,
                evaluatedColumn);
        naivebayes.show(false);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap(), evaluatedColumn);
    }

    public static void main(String[] args) throws IOException {
        new SentimentPipeline().execSupervisedMachneLearning(ColumnName.SENTIMENT_NO);
    }

}
