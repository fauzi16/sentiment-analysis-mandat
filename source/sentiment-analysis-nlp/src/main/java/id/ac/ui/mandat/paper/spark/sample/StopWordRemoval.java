package id.ac.ui.mandat.paper.spark.sample;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class StopWordRemoval {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("Stop Word Removal")
                                .config("spark.master", "local").getOrCreate();
                                
        StopWordsRemover remover = new StopWordsRemover()
                .setInputCol("raw")
                .setOutputCol("filtered");
        remover.setStopWords(StopWordFactoryBahasa.STOP_WORD);

        List<Row> data = Arrays.asList(
                RowFactory.create(Arrays.asList("kita", "indonesia", "adalah", "negara", "non-blok")),
                RowFactory.create(Arrays.asList("jangan", "lupakan", "jasa", "rusia")));

        StructType schema = new StructType(new StructField[] {
                new StructField(
                        "raw", DataTypes.createArrayType(DataTypes.StringType), false, Metadata.empty())
        });

        Dataset<Row> dataset = spark.createDataFrame(data, schema);
        remover.transform(dataset).show(true);
    }

}