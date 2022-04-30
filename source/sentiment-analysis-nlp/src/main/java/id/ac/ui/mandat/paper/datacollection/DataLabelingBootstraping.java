package id.ac.ui.mandat.paper.datacollection;

import java.io.File;
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
        String fileLocation = "../document/text-classification/lengthGT100/json/4.json";
        String jsonFile = FileUtils.readFileToString(new File(fileLocation), StandardCharsets.UTF_8);
        JSONArray array = new JSONArray(jsonFile);

        int sejarahRusiaIndonesiaCount = 0;
        int standarGandaNegaraBaratCount = 0;
        int nonBlockCOunt = 0;
        int kemanusiaanCount = 0;
        int amanahKonstitusiCount = 0;
        int perdamaianDuniaCount = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Object objComment = object.get("comment");
            String comment = (String) objComment;
            String lowerCasedComment = comment.toLowerCase();

            if(lowerCasedComment.contains("lupa") || lowerCasedComment.contains("sejarah")) {
                sejarahRusiaIndonesiaCount++;
                System.out.println(comment);
                System.out.println();
            }
            if(lowerCasedComment.contains("palestina") || lowerCasedComment.contains("israel") || lowerCasedComment.contains("irak")
                || lowerCasedComment.contains("iraq")) {
                standarGandaNegaraBaratCount++;
            }
            if(lowerCasedComment.contains("non-blo") || lowerCasedComment.contains("nonblo") || lowerCasedComment.contains("non blo")) {
                nonBlockCOunt++;
            }
            if(lowerCasedComment.contains("kemanusiaan")) {
                kemanusiaanCount++;
            }
            if(lowerCasedComment.contains("uud")) {
                amanahKonstitusiCount++;
            }
            if(lowerCasedComment.contains("perdamaian dunia")) {
                perdamaianDuniaCount++;
            }
        }

        System.out.println("Count of Class 'Sejarah Rusia-Indonesia': " + sejarahRusiaIndonesiaCount);
        System.out.println("Count of Class 'Standar Ganda Negara Barat': " + standarGandaNegaraBaratCount);
        System.out.println("Count of Class 'Non-Blok': " + nonBlockCOunt);
        System.out.println("Count of Class 'Kemanusiaan': " + kemanusiaanCount);
        System.out.println("Count of Class 'UUD 1945': " + amanahKonstitusiCount);
        System.out.println("Count of Class 'Perdamaian Dunia': " + perdamaianDuniaCount);
    }

}