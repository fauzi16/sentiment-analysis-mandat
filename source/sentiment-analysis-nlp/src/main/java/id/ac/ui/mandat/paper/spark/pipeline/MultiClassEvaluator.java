package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class MultiClassEvaluator {

    public static void exec(Dataset<Row> predictions) {
        // compute accuracy on the test set
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("classification_no")
                .setPredictionCol("prediction");
        MulticlassMetrics metrics = evaluator.getMetrics(predictions);
        double[][] values = new double[2][4];
        int index = 0;
        for (double label : metrics.labels()) {
            double precision = metrics.precision(label);
            double recall = metrics.recall(label);
            double fmeasure = metrics.fMeasure(label);
            values[index][0] = label;
            values[index][1] = precision;
            values[index][2] = recall;
            values[index][3] = fmeasure;
            index++;
        }
        System.out.println("-------------------------------------");
        System.out.println("| label, precision, recall, f-measure|");
        System.out.println("-------------------------------------");
        index = 0;
        for (double[] value : values) {
            double label = value[0];
            double precision = value[1];
            double recall = value[2];
            double fmeasure = value[3];
            String line = String.format("| %,.1f, %,.4f, %,.4f, %,.4f ", label, precision, recall, fmeasure);
            System.out.println(line);
        }
        System.out.println("-------------------------------------");
    }

}
