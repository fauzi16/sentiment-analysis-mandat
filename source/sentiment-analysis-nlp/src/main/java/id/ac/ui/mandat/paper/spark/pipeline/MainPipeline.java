package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class MainPipeline {

    public void exec() throws IOException {
        LoadDocumentResultHolder holder = LoadDocument.loadDocument();
        RemoveAlphaNumeric.exec(holder.getDocumentClasses());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                                .config("spark.master", "local").getOrCreate();
        Dataset<Row> tokenized = Tokenize.execute(spark, holder.getDocumentClasses());
        Dataset<Row> lemmatized = StemmingBahasaIndonesia.exec(spark, tokenized);

        Dataset<Row> stopword = StopWordRemoval.exec(lemmatized);

        Dataset<Row> ngramm = NGramm.exec(stopword, 1);
        Dataset<Row> tfidf = TFIDF.exec(ngramm, 1);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(0.2);

        trainingset.show();
        testset.show();

        Dataset<Row> naivebayes = NaiveBayesClassifier.exec(trainingset, testset);
        naivebayes.show(false);
        naivebayes.select("classification", "classification_no", "probability", "prediction").show(false);

        MultiClassEvaluator.exec(naivebayes, holder.getClassPointMap());
    }
    
    public static void main(String[] args) throws IOException {
        new MainPipeline().exec();
    }


}
