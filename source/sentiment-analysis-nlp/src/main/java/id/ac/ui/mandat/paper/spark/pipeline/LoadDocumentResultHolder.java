package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.List;
import java.util.Map;

/**
 * LoadDocumentResultHolder
 */
public class LoadDocumentResultHolder {

    private List<DocumentClass> documentClasses;
    private Map<Double, String> classPointMap;

    public LoadDocumentResultHolder(List<DocumentClass> documentClasses, Map<Double, String> classPointMap) {
        this.documentClasses = documentClasses;
        this.classPointMap = classPointMap;
    }
    public List<DocumentClass> getDocumentClasses() {
        return documentClasses;
    }
    public void setDocumentClasses(List<DocumentClass> documentClasses) {
        this.documentClasses = documentClasses;
    }
    public Map<Double, String> getClassPointMap() {
        return classPointMap;
    }
    public void setClassPointMap(Map<Double, String> classPointMap) {
        this.classPointMap = classPointMap;
    }

    

}