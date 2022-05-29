package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import scala.collection.immutable.ArraySeq;


public class RemoveDuplicateToken implements BaseTransformer {

    private String inputColumn;

    /**
     * 
     * @param spark
     * @param data
     * @return
     * @throws IOException
     */
    public Dataset<Row> exec(SparkSession spark, Dataset<Row> data) throws IOException{
        data = data.withColumn(ColumnName.DUPLICATE_REMOVED, data.col(this.inputColumn));
        UserDefinedFunction udf = functions.udf((ArraySeq<String> v1) -> {
            List<String> removedDuplcates = new ArrayList<>();
            for (int i = 0; i < v1.size(); i++) {
                String token = v1.apply((Integer) i);

                if(!removedDuplcates.contains(token) && !token.isEmpty()) {
                    removedDuplcates.add(token);
                }
            }
            return removedDuplcates;
        }, DataTypes.createArrayType(DataTypes.StringType)); 
        Column removeDuplicateColumn = udf.apply(data.col(ColumnName.DUPLICATE_REMOVED));
        data = data.withColumn(ColumnName.DUPLICATE_REMOVED, removeDuplicateColumn);
        
        return data;
    }

    @Override
    public void setInputColumn(String inputColumn) {
        this.inputColumn = inputColumn;
    }

    @Override
    public String getInputColumn() {
        return this.inputColumn;
    }

    @Override
    public String getOutputColumn() {
        return ColumnName.DUPLICATE_REMOVED;
    }

    @Deprecated
    public Dataset<Row> exec(Dataset<Row> param) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
