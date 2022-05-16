package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.List;

import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Tokenize {

    public static Dataset<Row> execute(SparkSession spark, List<DocumentClass> documentClass) {

        Dataset<Row> sentenceData = spark.createDataFrame(documentClass, DocumentClass.class);

        Tokenizer tokenizer = new Tokenizer().setInputCol("document").setOutputCol("tokenized");
        Dataset<Row> wordsData = tokenizer.transform(sentenceData);
        return wordsData;
    }

}
