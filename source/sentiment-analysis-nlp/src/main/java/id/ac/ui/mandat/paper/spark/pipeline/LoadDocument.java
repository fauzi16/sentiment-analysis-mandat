package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoadDocument {

    /**
     * 
     * @return
     * @throws IOException
     */
    public static List<DocumentClass> loadDocument() throws IOException {
        List<DocumentClass> documents = new ArrayList<>();
        String fileLocation = "document/text-classification/apache-spark/sample/naive-bayes-text-sample.txt";
        List<String> allLines = Files.readAllLines(Paths.get(fileLocation));
        for (String line : allLines) {
            if(line.isEmpty()) continue;
            int separatorIndex = line.indexOf(",");
            String label = line.substring(0, separatorIndex);
            Double labelNo = (label.startsWith("C") ? 0.0 : 1.0);
            String document = line.substring(separatorIndex, line.length());

            DocumentClass documentClass = new DocumentClass();
            documentClass.setClassification(label);
            documentClass.setDocument(document);
            documentClass.setClassification_no(labelNo);
            documents.add(documentClass);
        }
        return documents;
    }

}
