package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;

import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NaiveBayesClassifier {
    
    public static Dataset<Row> execTrainSaveAndTest(Dataset<Row> trainingset, Dataset<Row> testset, String modelSavedLocation, String evaluatedColumn) throws IOException {
        NaiveBayes nb = new NaiveBayes();
        nb.setFeaturesCol(ColumnName.TFIDF);
        nb.setLabelCol(evaluatedColumn);
        // train the model
        NaiveBayesModel model = nb.fit(trainingset);
        if(modelSavedLocation != null) {
            model.write().overwrite().save(modelSavedLocation);
        }

        // Select example rows to display.
        Dataset<Row> predictions = model.transform(testset);
        return predictions;
    }

    public static Dataset<Row> execTrainSaveAndTestSentiment(Dataset<Row> trainingset, Dataset<Row> testset, String modelSavedLocation, String evaluatedColumn, String featureColumn) throws IOException {
        NaiveBayes nb = new NaiveBayes();
        nb.setFeaturesCol(featureColumn);
        nb.setLabelCol(evaluatedColumn);
        // train the model
        NaiveBayesModel model = nb.fit(trainingset);
        if(modelSavedLocation != null) {
            model.write().overwrite().save(modelSavedLocation);
        }

        // Select example rows to display.
        Dataset<Row> predictions = model.transform(testset);
        return predictions;
    }

    public static Dataset<Row> execLoadAndTest(Dataset<Row> testset, String modelSavedLocation) throws IOException {
        // train the model
        NaiveBayesModel model = NaiveBayesModel.load(modelSavedLocation);

        // Select example rows to display.
        Dataset<Row> predictions = model.transform(testset);
        return predictions;
    }


}
