package id.ac.ui.mandat.paper.datacollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DataLabelingBootstraping
 */
public class DataLabelingBootstraping {

    public static void main(String[] args) throws IOException {
        String inputFolder = "../document/text-classification/lengthGT100/json/";
        String outputFolder = inputFolder + "bootstrap";
        String filename = "1.json";
        String fileLocation = inputFolder + filename;
        String jsonFile = FileUtils.readFileToString(new File(fileLocation), StandardCharsets.ISO_8859_1);
        JSONArray array = new JSONArray(jsonFile);

        int sejarahRusiaIndonesiaCount = 0;
        int standarGandaNegaraBaratCount = 0;
        int nonBlockCOunt = 0;
        int kemanusiaanCount = 0;
        int amanahKonstitusiCount = 0;
        int perdamaianDuniaCount = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            object.put("bootstrapped", false);
            Object objComment = object.get("comment");
            String comment = (String) objComment;
            String lowerCasedComment = comment.toLowerCase();

            if(lowerCasedComment.contains("lupa") || lowerCasedComment.contains("sejarah")) {
                // next rule: jasa
                sejarahRusiaIndonesiaCount++;
                addClassDimensionInformation("Sejarah Rusia-Indonesia", object);
            }
            if(lowerCasedComment.contains("palestina") || lowerCasedComment.contains("israel") || lowerCasedComment.contains("irak")
                || lowerCasedComment.contains("iraq")) {
                standarGandaNegaraBaratCount++;
                addClassDimensionInformation("Standar Ganda Negara Barat", object);
            }
            if(lowerCasedComment.contains("non-blo") || lowerCasedComment.contains("nonblo") || lowerCasedComment.contains("non blo")) {
                // next rule: ikut || campur, netral, abstain
                nonBlockCOunt++;
                addClassDimensionInformation("Non-Blok", object);
            }
            if(lowerCasedComment.contains("kemanusiaan")) {
                kemanusiaanCount++;
                addClassDimensionInformation("Kemanusiaan", object);
            }
            if(lowerCasedComment.contains("uud")) {
                amanahKonstitusiCount++;
                addClassDimensionInformation("Amanah Konstitusi UUD 1945", object);
            }
            if(lowerCasedComment.contains("perdamaian dunia")) {
                perdamaianDuniaCount++;
                addClassDimensionInformation("Perdamaian Dunia", object);
            }
        }

        FileWriter fileWriter = new FileWriter(new File(outputFolder, filename));
        array.write(fileWriter);

        System.out.println("Count of Class 'Sejarah Rusia-Indonesia': " + sejarahRusiaIndonesiaCount);
        System.out.println("Count of Class 'Standar Ganda Negara Barat': " + standarGandaNegaraBaratCount);
        System.out.println("Count of Class 'Non-Blok': " + nonBlockCOunt);
        System.out.println("Count of Class 'Kemanusiaan': " + kemanusiaanCount);
        System.out.println("Count of Class 'UUD 1945': " + amanahKonstitusiCount);
        System.out.println("Count of Class 'Perdamaian Dunia': " + perdamaianDuniaCount);

        fileWriter.close();
    }


    private static void addClassDimensionInformation(String classDimension, JSONObject jsonObject) {
        jsonObject.put("bootstrapped", true);
        if(!jsonObject.has("classDimension1")) {
            jsonObject.put("classDimension1", classDimension);
        } else if (!jsonObject.has("classDimension2")) {
            jsonObject.put("classDimension2", classDimension);
        } else if (!jsonObject.has("classDimension3")) {
            jsonObject.put("classDimension3", classDimension);
        } else if (!jsonObject.has("classDimension4")) {
            jsonObject.put("classDimension4", classDimension);
        } else if (!jsonObject.has("classDimension5")) {
            jsonObject.put("classDimension5", classDimension);
        } else if (!jsonObject.has("classDimension6")) {
            jsonObject.put("classDimension6", classDimension);
        }
    }

}