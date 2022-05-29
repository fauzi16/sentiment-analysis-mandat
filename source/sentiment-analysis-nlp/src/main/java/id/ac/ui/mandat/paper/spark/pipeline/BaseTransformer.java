package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.Serializable;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface BaseTransformer extends Serializable {

    public Dataset<Row> exec(Dataset<Row> param);
    
    public void setInputColumn(String inputColumn);

    public String getInputColumn();

    public String getOutputColumn();

}
