package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.Serializable;
import java.util.Set;

import jsastrawi.morphology.DefaultLemmatizer;

public class SerializedLemmatizer extends DefaultLemmatizer implements Serializable {

    public SerializedLemmatizer(Set<String> dictionary) {
        super(dictionary);
    }
    
}
