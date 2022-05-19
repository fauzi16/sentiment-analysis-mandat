package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class MultiClassEvaluator {

    public static void exec(Dataset<Row> predictions, Map<Double, String> classPointMap) {
        // compute accuracy on the test set
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("classification_no")
                .setPredictionCol("prediction");
        MulticlassMetrics metrics = evaluator.getMetrics(predictions);
        List<double[]> values = new ArrayList<double[]>();
        for (double label : metrics.labels()) {
            double precision = metrics.precision(label);
            double recall = metrics.recall(label);
            double fmeasure = metrics.fMeasure(label);
            double[] value = new double[4];
            value[0] = label;
            value[1] = precision;
            value[2] = recall;
            value[3] = fmeasure;
            values.add(value);
        }
        System.out.println("-----------------------------------------------");
        System.out.println("| label, label_no, precision, recall, f-measure|");
        System.out.println("-----------------------------------------------");
        for (double[] value : values) {
            double labelNo = value[0];
            double precision = value[1];
            double recall = value[2];
            double fmeasure = value[3];
            String label = classPointMap.get(labelNo);
            String line = String.format("| %,.1f, %,.4f, %,.4f, %,.4f, %s ", labelNo, precision, recall, fmeasure, label);
            System.out.println(line);
        }
        System.out.println("-------------------------------------");
    }

}
