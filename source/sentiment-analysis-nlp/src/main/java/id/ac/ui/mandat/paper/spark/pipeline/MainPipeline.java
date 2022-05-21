package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class MainPipeline {

    public static SparkConf SPARK_CONFIG = new SparkConf().setAppName("Multi class Classification").set("spark.master", "local");
    public static SparkContext SPARK_CONTEXT = new SparkContext(SPARK_CONFIG);

    public void execSupervisedMachneLearning() throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled();

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> tokenized = Tokenize.execute(spark, labeledDocumentResultHolder.getDocumentClasses());
        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        Dataset<Row> stopword = StopWordRemoval.exec(lemmatized);

        Dataset<Row> ngramm = NGramm.exec(stopword, 1);
        Dataset<Row> tfidf = TFIDF.exec(ngramm, 1);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(0.2);

        trainingset.show();
        testset.show();

        String modelSaveLocation = "./document/text-classification/machine-learning/naive-bayes/model";
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrain(trainingset, testset, modelSaveLocation);
        naivebayes.show(false);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap());
    }

    public void execUnsupervisedMachineLearning() throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentUnlabeled();

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> tokenized = Tokenize.execute(spark, labeledDocumentResultHolder.getDocumentClasses());
        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        Dataset<Row> stopword = StopWordRemoval.exec(lemmatized);

        Dataset<Row> ngramm = NGramm.exec(stopword, 1);
        Dataset<Row> tfidf = TFIDF.exec(ngramm, 1);

        String modelSaveLocation = "./document/text-classification/machine-learning/naive-bayes/model";
        Dataset<Row> naivebayes = NaiveBayesClassifier.execLoad(tfidf, modelSaveLocation);
        naivebayes.show(false);

        // MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap());
    }

    public static void main(String[] args) throws IOException {
        new MainPipeline().execUnsupervisedMachineLearning();
    }

}
