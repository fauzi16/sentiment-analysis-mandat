package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import scala.collection.immutable.ArraySeq;

public class AddNotPrefixForSentiment implements BaseTransformer {

    private String inputColumn;
    /**
     * 
     * @param spark
     * @param data
     * @return
     * @throws IOException
     */
    public Dataset<Row> exec(Dataset<Row> data) {
        data = data.withColumn(ColumnName.NEGETION_PERFIX, data.col(this.inputColumn));
        UserDefinedFunction udf = functions.udf((ArraySeq<String> v1) -> {
            List<String> withPrefixWithNegToken = new ArrayList<>();
            boolean isNegative = false;
            for (int i = 0; i < v1.size(); i++) {
                String token = v1.apply((Integer)i);
                
                if (token.equals("tidak") || token.equals("tdk")){
                    isNegative = true;
                } if(isNegative) {
                    token = "tidak_" + token;
                }
                withPrefixWithNegToken.add(token);
            }
            return withPrefixWithNegToken;
        }, DataTypes.createArrayType(DataTypes.StringType)); 
        Column negationPrexifColumn = udf.apply(data.col(ColumnName.NEGETION_PERFIX));
        data = data.withColumn(ColumnName.NEGETION_PERFIX, negationPrexifColumn);
        return data;
    }


    @Override
    public void setInputColumn(String inputColumn) {
        this.inputColumn = inputColumn;
    }

    @Deprecated
    public String getInputColumn() {
        return this.inputColumn;
    }

    @Override
    public String getOutputColumn() {
        return ColumnName.NEGETION_PERFIX;
    }
    
}
