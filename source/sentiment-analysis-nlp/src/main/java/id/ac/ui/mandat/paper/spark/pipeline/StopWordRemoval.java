package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import id.ac.ui.mandat.paper.spark.sample.StopWordFactoryBahasa;

public class StopWordRemoval implements BaseTransformer {

    private StopWordsRemover remover = new StopWordsRemover();

    public Dataset<Row> exec(Dataset<Row> data) {
        remover.setOutputCol(ColumnName.STOPWORDREMOVAL);
        remover.setStopWords(StopWordFactoryBahasa.STOP_WORD);

        return remover.transform(data);
    }

    @Override
    public void setInputColumn(String inputColumn) {
        remover.setInputCol(inputColumn);
    }

    @Override
    public String getInputColumn() {
        return remover.getInputCol();
    }

    @Override
    public String getOutputColumn() {
        return remover.getOutputCol();
    }

}
