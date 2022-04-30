package id.ac.ui.mandat.paper.datacollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class DataCollectionToJsonConverter {
    
    public static void main(String[] args) throws IOException {
        String filename = "3";
        String fileLocation = "../document/text-classification/" + filename  +".txt";
        DataCollectionParser parser = new DataCollectionParser(fileLocation);
        List<UserComment> userComments = parser.extractInformation();
        
        List<UserComment> userCommentLengthGT100 = new ArrayList<>();
        for (UserComment userComment : userComments) {
            String comment = userComment.getComment().toString();
            if(comment.length() > 100) {
                userCommentLengthGT100.add(userComment);
            }
        }
        
        JSONArray array = new JSONArray(userCommentLengthGT100);
        String fileOutputLocation = "../document/text-classification/lengthGT100/json/" + filename + ".json";
        FileWriter fileWritter = new FileWriter(new File(fileOutputLocation));
        array.write(fileWritter);
        fileWritter.close();
    }

}
