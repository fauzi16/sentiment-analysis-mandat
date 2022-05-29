package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

public class BasePipeline {
    
    public static SparkConf SPARK_CONFIG = new SparkConf().setAppName("Multi class Classification").set("spark.master", "local");
    public static SparkContext SPARK_CONTEXT = new SparkContext(SPARK_CONFIG);

    protected String modelLocation(String dataset, String evaluatedColumn) {
        String filesLocation = "./document/text-classification/machine-learning/naive-bayes/model/" + dataset + "/" + evaluatedColumn;
        File folder = new File(filesLocation);
        folder.mkdirs();
        return filesLocation; 
    }

    /**
     * 
     * @param saveLocation
     * @param labeledDocumentResultHolder
     * @throws IOException
     */
    protected void saveClassification(String saveLocation, LoadDocumentResultHolder labeledDocumentResultHolder) throws IOException {
        StringBuilder classificationPoint = new StringBuilder();
        Map<Double, String> classMap = labeledDocumentResultHolder.getClassPointMap();
        classificationPoint.append("label_no,label\n");
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
    protected Map<Double, String> loadClassification(String saveLocation) throws IOException {
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

    public BasePipeline() {
    }

}
