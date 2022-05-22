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
    
    private static final String LEMMATIZED_SOURCE = "/root-words.txt";

    public Dataset<Row> execute(SparkSession spark, Dataset<Row> data) throws IOException{
        Set<String> dictionary = new HashSet<String>();

        InputStream in = Lemmatizer.class.getResourceAsStream(LEMMATIZED_SOURCE);
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
            String classification = row.getAs(ColumnName.CLASSIFICATION);
            String document = row.getAs(ColumnName.DOCUMENT);
            Double classificationNo = row.getAs(ColumnName.CLASSIFICATION_NO);
            String sentiment = row.getAs(ColumnName.SENTIMENT);
            Double sentimentNo = row.getAs(ColumnName.SENTIMENT_NO);
            ArraySeq<String> tokenized = row.getAs(ColumnName.TOKENIZED);
            scala.collection.immutable.List<String> list = tokenized.toList();
            for (int i = 0; i < list.length(); i++) {
                String token = list.apply(i);
                String lemma = lemmatizer.lemmatize(token);
                lemmaDocument.add(lemma);
            }
            Row newRow = RowFactory.create(classification, classificationNo, document, sentiment, sentimentNo, tokenized, lemmaDocument);
            results.add(newRow);
        }

        StructType newSchema = data.schema();
        newSchema = newSchema.add(new StructField(
            ColumnName.LEMMATIZED, DataTypes.createArrayType(DataTypes.StringType), false, Metadata.empty()));
        Dataset<Row> lemmatized = spark.createDataFrame(results, newSchema);
        
        return lemmatized;
    }

    public static Dataset<Row> exec(SparkSession spark, Dataset<Row> data) throws IOException {
        return new StemmingBahasaIndonesia().execute(spark, data);
    }
    
}
