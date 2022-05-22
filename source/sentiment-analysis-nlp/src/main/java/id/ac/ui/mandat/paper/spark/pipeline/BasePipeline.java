package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.File;

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

    public BasePipeline() {
    }

}
