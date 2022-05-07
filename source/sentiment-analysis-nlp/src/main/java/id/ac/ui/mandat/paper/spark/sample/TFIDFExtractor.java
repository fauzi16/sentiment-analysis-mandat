package id.ac.ui.mandat.paper.spark.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.collection.mutable.ArraySeq;

/**
 * TFIDFExtractor
 */
public class TFIDFExtractor implements Serializable {

        public static void main(String[] args) {
                TFIDFExtractor extractor = new TFIDFExtractor();
                extractor.exec();
        }

        public void exec() {
                SparkSession spark = SparkSession.builder().appName("TF IDF Extractor")
                                .config("spark.master", "local").getOrCreate();

                List<Row> data = Arrays.asList(
                                RowFactory.create(0.0, "fauzi fauzi fauzi tidak anjing senang"),
                                RowFactory.create(0.0, "bola suka fauzi"),
                                RowFactory.create(0.0, "rina suka suka bola"));

                StructType schema = new StructType(new StructField[] {
                                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                });
                Dataset<Row> sentenceData = spark.createDataFrame(data, schema);

                Tokenizer tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
                Dataset<Row> wordsData = tokenizer.transform(sentenceData);

                HashingTF hashingTF = new HashingTF()
                                .setInputCol("words")
                                .setOutputCol("rawFeatures");

                Dataset<Row> featurizedData = hashingTF.transform(wordsData);
                // alternatively, CountVectorizer can also be used to get term frequency vectors
                featurizedData.show(false);

                IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
                IDFModel idfModel = idf.fit(featurizedData);

                Dataset<Row> rescaledData = idfModel.transform(featurizedData);
                rescaledData.select("label", "sentence", "words").show(false);

                rescaledData.select("label", "rawFeatures").show(false);

                rescaledData.select("label", "features").show(false);

                Map<String, TermIndex> termIndecies = new LinkedHashMap<>();
                List<Row> wordsRow = rescaledData.select("words").collectAsList();
                int rowIndex = 1;
                for (Row row : wordsRow) {
                        ArraySeq<String> words = row.getAs("words");
                        String[] wordsArray = (String[]) words.array();
                        for (String word : wordsArray) {
                                TermIndex ti = new TermIndex();
                                ti.setWord(word);
                                ti.setIndex(rowIndex + "-" + hashingTF.indexOf(word));
                                termIndecies.put(ti.getIndex(), ti);
                        }
                        rowIndex++;
                }

                Dataset<Row> tiDataset = spark.createDataFrame(new ArrayList<>(termIndecies.values()), TermIndex.class);
                tiDataset.show(false);
        }
}