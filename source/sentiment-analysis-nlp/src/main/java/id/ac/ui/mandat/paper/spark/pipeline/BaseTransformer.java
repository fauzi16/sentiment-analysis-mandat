package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface BaseTransformer {

    public Dataset<Row> exec(Dataset<Row> param);
    
    public void setInputColumn(String inputColumn);

    public String getInputColumn();

    public String getOutputColumn();

}
