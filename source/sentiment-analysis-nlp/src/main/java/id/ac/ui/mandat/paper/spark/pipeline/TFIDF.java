package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;

public class TFIDF implements BaseTransformer {

    private Double featureThreashold;
    private HashingTF hashingTF;
    private IDF idf;

    public TFIDF() {
        this.hashingTF = new HashingTF();
        this.idf = new IDF();
    }

    public Dataset<Row> exec(Dataset<Row> data, int ngram) {
        HashingTF hashingTF = new HashingTF()
                .setInputCol(ColumnName.ngram(ngram))
                .setOutputCol(ColumnName.TF);

        Dataset<Row> featurizedData = hashingTF.transform(data);
        // alternatively, CountVectorizer can also be used to get term frequency vectors
        // featurizedData.show(false);

        idf.setInputCol(ColumnName.TF).setOutputCol(ColumnName.TFIDF);
        IDFModel idfModel = idf.fit(featurizedData);

        Dataset<Row> result = idfModel.transform(featurizedData);
        this.collpsingFeatureByThreshold(result);

        return result;
    }

    /**
     * collepsing feature of TF-IDF result base on specified threshold 
     * 
     * @param result
     */
    private void collpsingFeatureByThreshold(Dataset<Row> result) {
        if (featureThreashold != null) {
            Dataset<Row> tfidfOnly = result.select(ColumnName.TFIDF);
            Encoder<Row> tfidfOnlyEncoder = RowEncoder.apply(tfidfOnly.schema());
            tfidfOnly.map((MapFunction<Row, Row>) (Row value) -> {
                SparseVector v1 = value.getAs(ColumnName.TFIDF);
                List<Integer> selectedIndices = new ArrayList<>();
                List<Double> selectedWeight = new ArrayList<>();
                int[] indices = v1.indices();
                for (int i = 0; i < indices.length; i++) {
                    int index = indices[i];
                    double weight = (double) v1.apply(index);
                    if (weight > TFIDF.this.featureThreashold) {
                        selectedIndices.add(index);
                        selectedWeight.add(weight);
                    }
                }
                int[] selectedIndicesArray = new int[selectedIndices.size()];
                for (int i = 0; i < selectedIndices.size(); i++) {
                    selectedIndicesArray[i] = selectedIndices.get(i);
                }
                double[] selectedWeightArray = new double[selectedWeight.size()];
                for (int i = 0; i < selectedWeight.size(); i++) {
                    selectedWeightArray[i] = selectedWeight.get(i);
                }
                SparseVector slicedVector = (SparseVector) Vectors.sparse(v1.size(), selectedIndicesArray,
                        selectedWeightArray);
                return RowFactory.create(slicedVector);
            }, tfidfOnlyEncoder);

            result = result.withColumn(ColumnName.TFIDF, tfidfOnly.col(ColumnName.TFIDF));
        }
    }

    public Double getFeatureThreashold() {
        return featureThreashold;
    }

    public void setFeatureThreashold(Double featureThreashold) {
        this.featureThreashold = featureThreashold;
    }

    @Deprecated
    public Dataset<Row> exec(Dataset<Row> param) {
        throw new RuntimeException();
    }

    @Deprecated
    public void setInputColumn(String inputColumn) {
        throw new RuntimeException();
    }

    @Override
    public String getInputColumn() {
        return hashingTF.getInputCol();
    }

    @Override
    public String getOutputColumn() {
        return idf.getOutputCol();
    }

}
