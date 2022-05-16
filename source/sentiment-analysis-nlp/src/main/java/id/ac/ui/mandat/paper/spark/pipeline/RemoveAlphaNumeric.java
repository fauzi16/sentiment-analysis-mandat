package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.List;

public class RemoveAlphaNumeric {

        
    public static final String REGEX_NON_ALPHANUMERIC = "^[^a-zA-Z0-9]+$";
    
    public static void exec(List<DocumentClass> documentClass){
        for (DocumentClass dc : documentClass) {
            String document = dc.getDocument().replaceAll(REGEX_NON_ALPHANUMERIC, "");
            dc.setDocument(document);
        }
    }

}
