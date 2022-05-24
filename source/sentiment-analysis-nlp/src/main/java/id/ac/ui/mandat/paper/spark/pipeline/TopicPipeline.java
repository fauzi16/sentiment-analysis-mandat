package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TopicPipeline extends BasePipeline implements Serializable {

    public void execLabeledClassification() throws IOException {
        LoadDocumentResultHolder labeledDocumentResultHolder = DocumentLoader.loadDocumentLabeled(ColumnName.CLASSIFICATION_NO);

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

        String modelSaveLocation = this.modelLocation("case1", ColumnName.CLASSIFICATION_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execTrain(trainingset, testset, modelSaveLocation,
            ColumnName.CLASSIFICATION_NO);
        naivebayes.show(false);

        MultiClassEvaluator.exec(naivebayes, labeledDocumentResultHolder.getClassPointMap(), ColumnName.CLASSIFICATION_NO);

        saveClassification(modelSaveLocation, labeledDocumentResultHolder);
    }

    /**
     * 
     * @param saveLocation
     * @param labeledDocumentResultHolder
     * @throws IOException
     */
    private void saveClassification(String saveLocation, LoadDocumentResultHolder labeledDocumentResultHolder) throws IOException {
        StringBuilder classificationPoint = new StringBuilder();
        Map<Double, String> classMap = labeledDocumentResultHolder.getClassPointMap();
        classificationPoint.append("label_no,label");
        for (Entry<Double, String> classEntry : classMap.entrySet()) {
            classificationPoint.append(String.format("%,.1f,%s\n", classEntry.getKey(), classEntry.getValue()));
        }
        File csvFile = new File(saveLocation, "class.csv");
        csvFile.createNewFile();
        FileUtils.writeStringToFile(csvFile, classificationPoint.toString(), Charset.forName("UTF-8"));
    }

    /**
     * 
     * @param saveLocation
     * @return
     * @throws IOException
     */
    private Map<Double, String> loadClassification(String saveLocation) throws IOException {
        Map<Double, String> classPointMap = new LinkedHashMap<>();
        List<String> classLines = FileUtils.readLines(new File(saveLocation, "class.csv"), StandardCharsets.UTF_8);
        for (int i = 0; i < classLines.size(); i++) {
            if(i == 0) continue;
            String classLine = classLines.get(i);
            String[] classParts = classLine.split(",");
            double point = Double.valueOf(classParts[0]);
            String label = classParts[1];
            classPointMap.put(point, label);
        }
        return classPointMap;
    }

    public void execUnlabeledClassification() throws IOException {
        LoadDocumentResultHolder unlabeledDocumentResultHolder = DocumentLoader.loadDocumentUnlabeled();

        RemoveAlphaNumeric.exec(unlabeledDocumentResultHolder.getDocumentClasses());

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

        Dataset<Row> tfidf = TFIDF.exec(ngrammResult, 1);

        String modelSaveLocation = this.modelLocation("case1", ColumnName.CLASSIFICATION_NO);
        Dataset<Row> naivebayes = NaiveBayesClassifier.execLoad(tfidf, modelSaveLocation);
        naivebayes.show(false);

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
        
    }

    /**
     * 
     */
    class FilterFunction2 implements FilterFunction<Row> {

        Double labelNo;

        @Override
        public boolean call(Row value) throws Exception {
            double prediction = value.getAs(ColumnName.PREDICTION);
            return labelNo.equals(prediction);
        }

    }

    public static void main(String[] args) throws IOException {
        new TopicPipeline().execUnlabeledClassification();
    }

}
