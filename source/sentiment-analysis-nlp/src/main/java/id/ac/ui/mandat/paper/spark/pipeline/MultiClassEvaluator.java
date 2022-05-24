package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class MultiClassEvaluator {

    public static void exec(Dataset<Row> predictions, Map<Double, String> classPointMap, String evaluatedColumn) {
        // compute accuracy on the test set
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol(evaluatedColumn)
                .setPredictionCol(ColumnName.PREDICTION);
        MulticlassMetrics metrics = evaluator.getMetrics(predictions);
        
        List<double[]> values = new ArrayList<double[]>();
        for (double label : metrics.labels()) {
            double precision = metrics.precision(label);
            double recall = metrics.recall(label);
            double fmeasure = metrics.fMeasure(label);
            double truePositiveRate = metrics.truePositiveRate(label);
            double falsePositoveRate = metrics.falsePositiveRate(label);
            double falseNegativeRate = 1 - truePositiveRate;
            double trueNegativeRate = 1 - falsePositoveRate;
            double[] value = new double[8];
            value[0] = label;
            value[1] = precision;
            value[2] = recall;
            value[3] = fmeasure;
            value[4] = truePositiveRate;
            value[5] = falsePositoveRate;
            value[6] = falseNegativeRate;
            value[7] = trueNegativeRate;
            values.add(value);
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.println("| label_no, true_positive, false_positive, false_negative, true_negative, precision, recall, f-measure, label|");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        for (double[] value : values) {
            double labelNo = value[0];
            double precision = value[1];
            double recall = value[2];
            double fmeasure = value[3];
            double truePositiveRate = value[4];
            double falsePositoveRate = value[5];
            double falseNegativeRate = value[6];
            double trueNegativeRate = value[7];
            String label = classPointMap.get(labelNo);
            String line = String.format("| %,.1f, %,.2f, %,.2f, %,.2f, %,.2f, %,.4f, %,.4f, %,.4f, %s ", labelNo, truePositiveRate, falsePositoveRate, 
                falseNegativeRate, trueNegativeRate, precision, recall, fmeasure, label);
            System.out.println(line);
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        forCsv(classPointMap, values);
    }

    private static void forCsv(Map<Double, String> classPointMap, List<double[]> values) {
        System.out.println("label_no,true_positive,false_positive,false_negative,true_negative,precision,recall,f-measure,label");
        for (double[] value : values) {
            double labelNo = value[0];
            double precision = value[1];
            double recall = value[2];
            double fmeasure = value[3];
            double truePositiveRate = value[4];
            double falsePositoveRate = value[5];
            double falseNegativeRate = value[6];
            double trueNegativeRate = value[7];
            String label = classPointMap.get(labelNo);
            String line = String.format("%,.1f,%,.2f,%,.2f,%,.2f,%,.2f,%,.4f,%,.4f,%,.4f,%s", labelNo, truePositiveRate, falsePositoveRate, 
                falseNegativeRate, trueNegativeRate, precision, recall, fmeasure, label);
            System.out.println(line);
        }
    }

}
