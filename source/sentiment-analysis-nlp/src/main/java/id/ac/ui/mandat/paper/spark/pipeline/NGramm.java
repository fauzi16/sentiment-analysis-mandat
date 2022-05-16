package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.NGram;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NGramm {
    
    public static Dataset<Row> exec(Dataset<Row> data, int ngram) {
        NGram ngramTransformer = new NGram().setN(ngram).setInputCol("stopword_removal").setOutputCol("ngrams_" + ngram);
        return ngramTransformer.transform(data);
    }

}
