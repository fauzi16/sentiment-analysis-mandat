package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import scala.collection.mutable.ArraySeq;

public class AddNotPrefixForSentiment implements BaseTransformer {

    public Dataset<Row> exec(SparkSession spark, Dataset<Row> data) throws IOException{
        List<Row> results = new ArrayList<>();
        List<Row> tokenizedRows = data.collectAsList();
        for (Row row : tokenizedRows) {
            List<String> withPrefixWithNegToken = new ArrayList<>();
            String classification = row.getAs(ColumnName.CLASSIFICATION);
            String document = row.getAs(ColumnName.DOCUMENT);
            Double classificationNo = row.getAs(ColumnName.CLASSIFICATION_NO);
            String sentiment = row.getAs(ColumnName.SENTIMENT);
            Double sentimentNo = row.getAs(ColumnName.SENTIMENT_NO);
            ArraySeq<String> tokenized = row.getAs(ColumnName.TOKENIZED);
            scala.collection.immutable.List<String> list = tokenized.toList();
            boolean isNegative = false;
            for (int i = 0; i < list.length(); i++) {
                String token = list.apply(i);
                if(isNegative) {
                    token = "tidak_" + token;
                } else if (token.equals("tidak") || token.equals("tdk")){
                    isNegative = true;
                }
                withPrefixWithNegToken.add(token);
            }
            Row newRow = RowFactory.create(classification, classificationNo, document, sentiment, sentimentNo, withPrefixWithNegToken);
            results.add(newRow);
        }

        StructType newSchema = data.schema();
        Dataset<Row> removedDuplcates = spark.createDataFrame(results, newSchema);
        
        return removedDuplcates;
    }

    @Deprecated
    public void setInputColumn(String inputColumn) {
        throw new RuntimeException();
    }

    @Deprecated
    public String getInputColumn() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public String getOutputColumn() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public Dataset<Row> exec(Dataset<Row> param) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
