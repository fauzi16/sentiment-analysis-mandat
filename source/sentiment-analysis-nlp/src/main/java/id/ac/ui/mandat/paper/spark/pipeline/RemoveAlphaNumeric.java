package id.ac.ui.mandat.paper.spark.pipeline;

import java.util.List;

public class RemoveAlphaNumeric {

        
    public static final String REGEX_NON_ALPHANUMERIC = "[^A-Za-z0-9\\s]";
    
    public static void exec(List<DocumentClass> documentClass){
        for (int i = documentClass.size() - 1; i >= 0; i--) {
            DocumentClass dc = documentClass.get(i);
            String document = dc.getDocument().replaceAll(REGEX_NON_ALPHANUMERIC, " ");
            if(document.length() < 10) {
                documentClass.remove(dc);
                continue;
            }
            dc.setDocument(document);
        }
    }

}
