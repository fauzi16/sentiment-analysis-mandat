package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SentimentPipeline extends BasePipeline {

    /**
     * 
     * @param labeledDocumentResultHolder
     * @return
     * @throws IOException
     */
    private Dataset<Row> extractTFIDForSentiment(LoadDocumentResultHolder labeledDocumentResultHolder) throws IOException {
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

        RemoveDuplicateToken removeDuplicateToken = new RemoveDuplicateToken();
        removeDuplicateToken.setInputColumn(stopWordRemoval.getOutputColumn());
        Dataset<Row> duplicateTokenRemoved = removeDuplicateToken.exec(spark, stopword);

        // duplicateTokenRemoved.select(stopWordRemoval.getOutputColumn(), removeDuplicateToken.getOutputColumn()).show(false);
        duplicateTokenRemoved.explain();

        AddNotPrefixForSentiment addNotPrefixForSentiment = new AddNotPrefixForSentiment();
        addNotPrefixForSentiment.setInputColumn(removeDuplicateToken.getOutputColumn());
        Dataset<Row> negationPrefix = addNotPrefixForSentiment.exec(duplicateTokenRemoved);

        NGramm nGramm = new NGramm();
        nGramm.setInputColumn(addNotPrefixForSentiment.getOutputColumn());
        Dataset<Row> ngrammResult = nGramm.exec(negationPrefix, 4);

        TFIDF tfidfTransformer = new TFIDF();
        tfidfTransformer.setFeatureThreashold(0.2);
        Dataset<Row> tfidf = tfidfTransformer.exec(ngrammResult, 4);

        return tfidf;
    }

    /**
     * 
     * @throws IOException
     */
    public void execLabeledDataset() throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(ColumnName.SENTIMENT_NO);

        Dataset<Row> tfidf = this.extractTFIDForSentiment(labeledDocumentResultHolder);

        Dataset<Row> trainingset = tfidf.sample(1.0);
        Dataset<Row> testset = tfidf.sample(1.0);

        String modelSaveLocation = this.modelLocation("russia_ukraine_war_case", ColumnName.SENTIMENT_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrainSaveAndTestSentiment(trainingset, testset, modelSaveLocation,
            ColumnName.SENTIMENT_NO, ColumnName.TFIDF);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap(), ColumnName.SENTIMENT_NO);

        saveClassification(modelSaveLocation, labeledDocumentResultHolder);

        long negative = naivebayes.filter((Row value) -> {
            return value.getAs(ColumnName.PREDICTION).equals(0.0);
        }).count();
        long positive = naivebayes.filter((Row value) -> {
            return value.getAs(ColumnName.PREDICTION).equals(1.0);
        }).count();
        System.out.println("result positive and negative is : " + positive + " and " + negative);
    }

    
    /**
     * 
     * @throws IOException
     */
    public void execUnlabeledDataset() throws IOException {
        // LoadDocumentResultHolder unlabeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(ColumnName.SENTIMENT_NO);
        LoadDocumentResultHolder unlabeledDocumentResultHolder = DocumentLoader.loadDocumentUnlabeled();

        Dataset<Row> tfidf = this.extractTFIDForSentiment(unlabeledDocumentResultHolder);

        String modelSaveLocation = this.modelLocation("russia_ukraine_war_case", ColumnName.SENTIMENT_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execLoadAndTest(tfidf, modelSaveLocation);

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

        // naivebayes.select(ColumnName.DOCUMENT, ColumnName.PREDICTION).filter((Row value) -> {
        //     return value.getAs(ColumnName.PREDICTION).equals(0.0);
        // }).show(false);
        // naivebayes.select(ColumnName.DOCUMENT, ColumnName.PREDICTION).filter((Row value) -> {
        //     return value.getAs(ColumnName.PREDICTION).equals(1.0);
        // }).show(false);
    }

    public static void main(String[] args) throws IOException {
        // new SentimentPipeline().execLabeledDataset();
        new SentimentPipeline().execUnlabeledDataset();
    }

}
