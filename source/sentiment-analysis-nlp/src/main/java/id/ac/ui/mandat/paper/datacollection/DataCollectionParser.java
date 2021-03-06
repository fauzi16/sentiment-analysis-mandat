package id.ac.ui.mandat.paper.datacollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DataCollectionParser {

    private static final String NUMBER_REGEX = "\\d+";

    public static void main(String[] args) throws IOException {
        String fileLocation = "./document/text-classification/data-collection/11.txt";
        DataCollectionParser parser = new DataCollectionParser(fileLocation);
        List<UserComment> extractInformation = parser.extractInformation();
        int commentThatMoreThan100Char = 0;
        
        for (UserComment userComment : extractInformation) {
            String comment = userComment.getComment().toString();
            
            if(comment.length() > 50){
                System.out.println(userComment.getUsername() + " - " + comment);
                System.out.println();
                commentThatMoreThan100Char++;
            }
        }

        System.out.println("F. Jumlah data: " + extractInformation.size());
        System.out.println("G. Jumlah data dengan comment lebih besar dari 50 karakter: " + commentThatMoreThan100Char);
    }

    private String fileLocation;
    private int emptyLineStreak = 0;
    private int sectionLine = 0;

    
    public DataCollectionParser(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    private void addEmptyLineStreak() {
        emptyLineStreak++; 
    }

    private void resetEmptyLineStreak() {
        emptyLineStreak = 0;
    }

    private boolean isNewSection() {
        return emptyLineStreak >= 2;
    }

    public List<UserComment> extractInformation() throws IOException {
        List<UserComment> results = new ArrayList<>();
        // pass the path to the file as a parameter
        File file = new File(this.fileLocation);
        List<String> allLines = Files.readAllLines(file.toPath());

        String prevLine = "";
        UserComment userComment = null;

        for (String lineString : allLines){
            sectionLine++;
            
            // detecting if this is new section comment
            if(lineString == null || lineString.isEmpty()) {
                this.addEmptyLineStreak();
                if(this.isNewSection()) userComment = null;
                continue;
            } else {
                this.resetEmptyLineStreak();
            }
            
            // processing new section of comment
            if(prevLine.equals(lineString)) {
                String username = lineString;
                prevLine = null;
                userComment = new UserComment(username, new StringBuilder());
                results.add(userComment);
                sectionLine = 2;
            } else if (sectionLine == 3) {
                // ini adalah bagian dari waktu dimana comment dibuat
                continue;
            } else if (lineString.matches(NUMBER_REGEX) || (lineString.length() <= 6 && lineString.endsWith("rb"))) {
                // ini adalah data jumlah balasan yang juga terbawa ketika proses data collection
                continue;
            } else if (sectionLine > 3){
                if (userComment != null){
                    userComment.getComment().append(lineString);
                }
            }
            prevLine = lineString;
        }


        return results;
    }

}