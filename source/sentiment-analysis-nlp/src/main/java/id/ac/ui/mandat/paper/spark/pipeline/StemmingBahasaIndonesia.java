package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import jsastrawi.morphology.Lemmatizer;
import scala.collection.mutable.ArraySeq;

public class StemmingBahasaIndonesia implements Serializable {

    private Lemmatizer lemmatizer;

    public Dataset<Row> execute(SparkSession spark, Dataset<Row> data) throws IOException{
        Set<String> dictionary = new HashSet<String>();

        InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = br.readLine()) != null) {
            dictionary.add(line);
        }

        lemmatizer = new SerializedLemmatizer(dictionary);
        List<Row> results = new ArrayList<>();
        List<Row> tokenizedRows = data.collectAsList();
        for (Row row : tokenizedRows) {
            List<String> lemmaDocument = new ArrayList<>();
            String classification = row.getAs("classification");
            String document = row.getAs("document");
            Double classificationNo = row.getAs("classification_no");
            ArraySeq<String> tokenized = row.getAs("tokenized");
            scala.collection.immutable.List<String> list = tokenized.toList();
            for (int i = 0; i < list.length(); i++) {
                String token = list.apply(i);
                String lemma = lemmatizer.lemmatize(token);
                lemmaDocument.add(lemma);
            }
            Row newRow = RowFactory.create(classification, classificationNo, document, tokenized, lemmaDocument);
            results.add(newRow);
        }

        StructType newSchema = data.schema();
        data.explain();
        newSchema = newSchema.add(new StructField(
            "lemmatized", DataTypes.createArrayType(DataTypes.StringType), false, Metadata.empty()));
        Dataset<Row> lemmatized = spark.createDataFrame(results, newSchema);
        
        return lemmatized;
    }

    public static Dataset<Row> exec(SparkSession spark, Dataset<Row> data) throws IOException {
        return new StemmingBahasaIndonesia().execute(spark, data);
    }
    
}
