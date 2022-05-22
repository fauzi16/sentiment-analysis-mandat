package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class Tokenize implements BaseTransformer {

    private Tokenizer tokenizer = new Tokenizer();

    public Dataset<Row> exec(Dataset<Row> sentenceData) {
        tokenizer.setInputCol(ColumnName.DOCUMENT);
        tokenizer.setOutputCol(ColumnName.TOKENIZED);
        Dataset<Row> wordsData = tokenizer.transform(sentenceData);
        return wordsData;
    }

    @Override
    public void setInputColumn(String inputColumn) {
        throw new RuntimeException();
    }

    @Override
    public String getInputColumn() {
        return tokenizer.getInputCol();
    }

    @Override
    public String getOutputColumn() {
        return tokenizer.getOutputCol();
    }

}
