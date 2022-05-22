package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.NGram;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NGramm implements BaseTransformer {
    
    private NGram ngramTransformer = new NGram();

    public Dataset<Row> exec(Dataset<Row> data, int ngram) {
        ngramTransformer.setN(ngram).setOutputCol(ColumnName.ngram(ngram));
        return ngramTransformer.transform(data);
    }

    @Override
    public void setInputColumn(String inputColumn) {
        ngramTransformer.setInputCol(inputColumn);
    }

    @Deprecated
    public void setOutputColumn(String outputColumn) {
        throw new RuntimeException();
    }

    @Override
    public String getInputColumn() {
        return ngramTransformer.getInputCol();
    }

    @Override
    public String getOutputColumn() {
        return ngramTransformer.getOutputCol();
    }

    @Override
    public Dataset<Row> exec(Dataset<Row> param) {
        // TODO Auto-generated method stub
        return null;
    }

}
