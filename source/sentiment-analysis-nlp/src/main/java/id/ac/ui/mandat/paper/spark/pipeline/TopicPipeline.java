package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TopicPipeline extends BasePipeline {

    public void execLabeledClassification() throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(ColumnName.CLASSIFICATION_NO);

        RemoveAlphaNumeric.exec(labeledDocumentResultHolder.getDocumentClasses());

        System.out.println("Total of labeled dataset : " + labeledDocumentResultHolder.getDocumentClasses().size());

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

        TFIDF tfidfTransformer = new TFIDF();
        tfidfTransformer.setFeatureThreashold(0.2);
        Dataset<Row> tfidf = tfidfTransformer.exec(ngrammResult, 1);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(1.0);

        // trainingset.show();
        // testset.show();

        String modelSaveLocation = this.modelLocation("russia_ukraine_war_case", ColumnName.CLASSIFICATION_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrainSaveAndTest(trainingset, testset, modelSaveLocation,
            ColumnName.CLASSIFICATION_NO);
        naivebayes.show(false);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap(), ColumnName.CLASSIFICATION_NO);

        saveClassification(modelSaveLocation, labeledDocumentResultHolder);
    }

    

    public void execUnlabeledClassification() throws IOException {
        LoadDocumentResultHolder unlabeledDocumentResultHolder = DocumentLoader.loadDocumentUnlabeled();

        RemoveAlphaNumeric.exec(unlabeledDocumentResultHolder.getDocumentClasses());

        System.out.println("Total of unlabeled dataset : " + unlabeledDocumentResultHolder.getDocumentClasses().size());

        SparkSession spark = SparkSession.builder().appName("Main Pipeline")
                .config(SPARK_CONFIG).getOrCreate();

        Dataset<Row> sentenceData = spark.createDataFrame(unlabeledDocumentResultHolder.getDocumentClasses(),
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

        Dataset<Row> tfidf = new TFIDF().exec(ngrammResult, 1);

        String modelSaveLocation = this.modelLocation("russia_ukraine_war_case", ColumnName.CLASSIFICATION_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execLoadAndTest(tfidf, modelSaveLocation);
        // naivebayes.show(false);

        Map<Double, String> classPointMap = loadClassification(modelSaveLocation);
        Map<String, Long> resultMap = new LinkedHashMap<>();
        for (Entry<Double, String> classEntry : classPointMap.entrySet()) {
            double labelNo = classEntry.getKey();
            long count = naivebayes.filter((Row value) -> {
                return value.getAs(ColumnName.PREDICTION).equals(labelNo);
            }).count();
            resultMap.put(classEntry.getValue(), count);
        }
        System.out.println("label,count");
        for (Entry<String, Long> entry : resultMap.entrySet()) {
            System.out.println(String.format("%s,%d", entry.getKey(), entry.getValue()));    
        }

        // result of sejjarah indonesia dan rusia
        // naivebayes.select(ColumnName.DOCUMENT, ColumnName.CLASSIFICATION, ColumnName.CLASSIFICATION_NO, ColumnName.PREDICTION).filter((Row value) -> {
        //     return value.getAs(ColumnName.PREDICTION).equals(3.0);
        // }).show(false);

        // result of standar ganda negara barat
        // naivebayes.select(ColumnName.DOCUMENT, ColumnName.CLASSIFICATION, ColumnName.CLASSIFICATION_NO, ColumnName.PREDICTION).filter((Row value) -> {
        //     return value.getAs(ColumnName.PREDICTION).equals(5.0);
        // }).show(false);
        
    }

    public static void main(String[] args) throws IOException {
        // new TopicPipeline().execLabeledClassification();
        new TopicPipeline().execUnlabeledClassification();
    }

}
