package id.ac.ui.mandat.paper.spark.sample;

import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class NaiveBayesSample {

    public static void main(String[] args) {
        // Load training data
        SparkSession spark = SparkSession.builder().appName("Naive Bayes Sample")
            .config("spark.master", "local").getOrCreate();
        Dataset<Row> dataFrame = spark.read().format("libsvm").load("document/text-classification/apache-spark/sample/sample_libsvm_data.txt");
        // Split the data into train and test
        Dataset<Row>[] splits = dataFrame.randomSplit(new double[] { 0.6, 0.4 }, 1234L);
        Dataset<Row> train = splits[0];
        Dataset<Row> test = splits[1];

        // create the trainer and set its parameters
        NaiveBayes nb = new NaiveBayes();
        //nb.set

        // train the model
        NaiveBayesModel model = nb.fit(train);

        // Select example rows to display.
        Dataset<Row> predictions = model.transform(test);
        predictions.show(false);

        // compute accuracy on the test set
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");
        double accuracy = evaluator.evaluate(predictions);
        System.out.println("Test set accuracy = " + accuracy);
    }

}
