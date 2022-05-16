package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NaiveBayesClassifier {
    
    public static Dataset<Row> exec(Dataset<Row> trainingset, Dataset<Row> testset) {
        NaiveBayes nb = new NaiveBayes();
        nb.setFeaturesCol("tfidf");
        nb.setLabelCol("classification_no");
        // train the model
        NaiveBayesModel model = nb.fit(trainingset);

        // Select example rows to display.
        Dataset<Row> predictions = model.transform(testset);
        return predictions;
    }
}
