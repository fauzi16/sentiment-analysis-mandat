package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class TFIDF {

    public static Dataset<Row> exec(Dataset<Row> data, int ngram) {
        HashingTF hashingTF = new HashingTF()
                .setInputCol(ColumnName.ngram(ngram))
                .setOutputCol(ColumnName.TF);

        Dataset<Row> featurizedData = hashingTF.transform(data);
        // alternatively, CountVectorizer can also be used to get term frequency vectors
        featurizedData.show(false);

        IDF idf = new IDF().setInputCol(ColumnName.TF).setOutputCol(ColumnName.TFIDF);
        IDFModel idfModel = idf.fit(featurizedData);

        return idfModel.transform(featurizedData);
    }

}
