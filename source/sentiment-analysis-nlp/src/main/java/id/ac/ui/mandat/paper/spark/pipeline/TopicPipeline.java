package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TopicPipeline extends BasePipeline {

    public void execSupervisedMachneLearning(String evaluatedColumn) throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(evaluatedColumn);

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> sentenceData = spark.createDataFrame(labeledDocumentResultHolder.getDocumentClasses(),
                DocumentClass.class);

        Tokenize tokenize = new Tokenize();
        Dataset<Row> tokenized = tokenize.exec(sentenceData);

        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        StopWordRemoval stopWordRemoval = new StopWordRemoval();
        stopWordRemoval.setInputColumn(ColumnName.LEMMATIZED);
        Dataset<Row> stopword = stopWordRemoval.exec(lemmatized);

        NGramm nGramm = new NGramm();
        nGramm.setInputColumn(ColumnName.STOPWORDREMOVAL);
        Dataset<Row> ngrammResult = nGramm.exec(stopword, 1);

        Dataset<Row> tfidf = TFIDF.exec(ngrammResult, 1);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(0.2);

        trainingset.show();
        testset.show();

        String modelSaveLocation = this.modelLocation("case1", evaluatedColumn);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrain(trainingset, testset, modelSaveLocation,
                evaluatedColumn);
        naivebayes.show(false);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap(), evaluatedColumn);
    }

    public void execUnsupervisedMachineLearning(String evaluatedColumn) throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentUnlabeled();

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> sentenceData = spark.createDataFrame(labeledDocumentResultHolder.getDocumentClasses(),
                DocumentClass.class);

        Tokenize tokenize = new Tokenize();
        Dataset<Row> tokenized = tokenize.exec(sentenceData);

        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        StopWordRemoval stopWordRemoval = new StopWordRemoval();
        stopWordRemoval.setInputColumn(ColumnName.LEMMATIZED);
        Dataset<Row> stopword = stopWordRemoval.exec(lemmatized);

        NGramm nGramm = new NGramm();
        nGramm.setInputColumn(ColumnName.STOPWORDREMOVAL);
        Dataset<Row> ngrammResult = nGramm.exec(stopword, 1);

        Dataset<Row> tfidf = TFIDF.exec(ngrammResult, 1);

        String modelSaveLocation = this.modelLocation("case1", evaluatedColumn);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execLoad(tfidf, modelSaveLocation);
        naivebayes.show(false);
    }

    public static void main(String[] args) throws IOException {
        new TopicPipeline().execSupervisedMachneLearning(ColumnName.CLASSIFICATION_NO);
    }

}
