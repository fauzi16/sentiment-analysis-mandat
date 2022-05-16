package id.ac.ui.mandat.paper.spark.pipeline;

import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import id.ac.ui.mandat.paper.spark.sample.StopWordFactoryBahasa;

public class StopWordRemoval {

    public static Dataset<Row> exec(Dataset<Row> data) {
        StopWordsRemover remover = new StopWordsRemover()
                .setInputCol("lemmatized")
                .setOutputCol("stopword_removal");
        remover.setStopWords(StopWordFactoryBahasa.STOP_WORD);

        return remover.transform(data);
    }

}
