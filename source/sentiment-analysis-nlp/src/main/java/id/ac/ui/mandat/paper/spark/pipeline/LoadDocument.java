package id.ac.ui.mandat.paper.spark.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class LoadDocument {

    /**
     * 
     * @return
     * @throws IOException
     */
    public static LoadDocumentResultHolder loadDocument() throws IOException {
        List<DocumentClass> documents = new ArrayList<>();
        String fileLocation = "document/text-classification/apache-spark/sample/naive-bayes-text-sample.txt";
        List<String> allLines = Files.readAllLines(Paths.get(fileLocation));
        Map<String, Double> classPointMap = new LinkedHashMap<String, Double>();
        classPointMap.put("Computer", 0.0);
        classPointMap.put("Football", 1.0);
        for (String line : allLines) {
            if(line.isEmpty()) continue;
            int separatorIndex = line.indexOf(",");
            String label = line.substring(0, separatorIndex);
            Double labelNo = classPointMap.get(label);
            String document = line.substring(separatorIndex, line.length());

            DocumentClass documentClass = new DocumentClass();
            documentClass.setClassification(label);
            documentClass.setDocument(document);
            documentClass.setClassification_no(labelNo);
            documents.add(documentClass);
        }

        Map<Double, String> classMap = new LinkedHashMap<>();
        for (Entry<String, Double> classEntry : classPointMap.entrySet()) {
            classMap.put(classEntry.getValue(), classEntry.getKey());
        }
        LoadDocumentResultHolder resultHolder = new LoadDocumentResultHolder(documents, classMap);
        return resultHolder;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public static LoadDocumentResultHolder loadDocument2() throws IOException {
        List<DocumentClass> documentClasses = new ArrayList<>();
        String[] fileLocations = new String[1];
        fileLocations[0] = "document/text-classification/data-preprocessing/manual-labeling/json/1.json";
        for (String fileLocation : fileLocations) {
            String jsonFile = FileUtils.readFileToString(new File(fileLocation), StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(jsonFile);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String sentiment = object.optString("sentiment", null);
                if(sentiment == null) continue;
                if(!sentiment.contains("N") && !sentiment.contains("P")) continue;

                JSONArray sentenceAndClassDimensions = object.getJSONArray("sentenceAndClassDimensions");
                for (int j = 0; j < sentenceAndClassDimensions.length(); j++) {
                    JSONObject object2 = sentenceAndClassDimensions.getJSONObject(j);
                    String sentence = object2.optString("sentence", null);
                    String dimension = object2.optString("dimension", null);
                    if(dimension == null || sentence == null) continue;
                    DocumentClass documentClass = new DocumentClass();
                    documentClass.setClassification(dimension);
                    String newLineRemoved = sentence.replace("\\n", " ");
                    documentClass.setDocument(newLineRemoved);
                    documentClass.setSentiment(sentiment);
                    documentClasses.add(documentClass);
                }
            }
        }

        Map<String, Double> classPointMap = new LinkedHashMap<>();
        double lastIndex = -1.0;
        for (DocumentClass documentClass : documentClasses) {
            String classString = documentClass.getClassification();
            double classPoint = lastIndex;
            if(classPointMap.containsKey(classString)) {
                classPoint = classPointMap.get(classString);
            } else {
                classPoint = ++lastIndex;
                classPointMap.put(classString, classPoint);
            }
            documentClass.setClassification_no(classPoint);
            if(documentClass.getSentiment().contains("N")) {
                documentClass.setSentiment_no(0.0);
            } else {
                documentClass.setSentiment_no(1.0);
            }
            
        }

        Map<Double, String> classMap = new LinkedHashMap<>();
        for (Entry<String, Double> classEntry : classPointMap.entrySet()) {
            classMap.put(classEntry.getValue(), classEntry.getKey());
        }
        LoadDocumentResultHolder resultHolder = new LoadDocumentResultHolder(documentClasses, classMap);
        return resultHolder;
    }

}
