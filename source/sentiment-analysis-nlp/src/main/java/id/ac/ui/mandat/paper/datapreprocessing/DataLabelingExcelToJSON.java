package id.ac.ui.mandat.paper.datapreprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;

import id.ac.ui.mandat.paper.datapreprocessing.model.Comment;
import id.ac.ui.mandat.paper.datapreprocessing.model.SentenceAndClassDimension;

/**
 * DataLabelingExcelToJSON
 */
public class DataLabelingExcelToJSON {

    private static int COLOMN_INDEX_COMMENT = 2;
    private static int COLOMN_INDEX_USERNAME = 3;
    private static int COLOMN_INDEX_SENTIMENT = 4;
    private static int COLOMN_INDEX_CLASSDIMENION1 = 5;
    private static int COLOMN_INDEX_CLASSDIMENION2 = 6;
    private static int COLOMN_INDEX_CLASSDIMENION3 = 7;
    private static int COLOMN_INDEX_CLASSDIMENION4 = 8;

    public static void main(String[] args) throws EncryptedDocumentException, IOException {
        String fileLocation = "../document/text-classification/data-preprocessing/manual-labeling/excel";
        String filename = "1.json";
        File excelFile = new File(fileLocation, filename + ".xlsx");
        if (!excelFile.exists()) {
            System.out.println("File tidak ditemukan");
        }
        FileInputStream is = new FileInputStream(excelFile);

        Workbook wb = WorkbookFactory.create(is);

        Sheet sheet = wb.getSheet("Sheet1");

        List<Comment> comments = new ArrayList<>();

        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex == 0) {
                rowIndex++;
                continue;
            }

            Cell commentCell = row.getCell(COLOMN_INDEX_COMMENT);
            String comment = commentCell != null ? commentCell.getStringCellValue() : null;

            Cell usernameCell = row.getCell(COLOMN_INDEX_USERNAME);
            String username = usernameCell != null ? usernameCell.getStringCellValue() : null;

            Cell sentimentCell = row.getCell(COLOMN_INDEX_SENTIMENT);
            String sentiment = sentimentCell != null ? sentimentCell.getStringCellValue() : null;

            Cell classDim1Cell1 = row.getCell(COLOMN_INDEX_CLASSDIMENION1);
            String classDim1 = classDim1Cell1 != null ? classDim1Cell1.getStringCellValue() : null;

            Cell classDim1Cell2 = row.getCell(COLOMN_INDEX_CLASSDIMENION2);
            String classDim2 = classDim1Cell2 != null ? classDim1Cell2.getStringCellValue() : null;

            Cell classDim1Cell3 = row.getCell(COLOMN_INDEX_CLASSDIMENION3);
            String classDim3 = classDim1Cell3 != null ? classDim1Cell3.getStringCellValue() : null;

            Cell classDim1Cell4 = row.getCell(COLOMN_INDEX_CLASSDIMENION4);
            String classDim4 = classDim1Cell4 != null ? classDim1Cell4.getStringCellValue() : null;

            Comment commentObj = new Comment();
            commentObj.setUsername(username);
            commentObj.setSentiment(sentiment);
            commentObj.setComment(comment);
            List<SentenceAndClassDimension> sentences = new ArrayList<SentenceAndClassDimension>();
            commentObj.setSentenceAndClassDimensions(sentences);
            commentObj.setRowIndex(rowIndex);

            processSentenceSegmentation(commentObj, classDim1, classDim2, classDim3, classDim4);
            comments.add(commentObj);

            rowIndex++;
        }


        JSONArray array = new JSONArray(comments);
        String resultLocation = "../document/text-classification/data-preprocessing/manual-labeling/json";
        FileWriter fileWritter = new FileWriter(new File(resultLocation, filename));
        array.write(fileWritter);
        fileWritter.close();

    }

    /**
     * 
     * @param classDim1
     * @param classDim2
     * @param classDim3
     * @param classDim4
     */
    private static void processSentenceSegmentation(Comment comment, String classDim1, String classDim2,
            String classDim3, String classDim4) {
        try {
            String sentence = null;
            if (classDim2 != null) {
                sentence = parseSentenceSegment(comment.getComment(), "1");
                comment.getSentenceAndClassDimensions().add(new SentenceAndClassDimension(sentence, classDim1));
                sentence = parseSentenceSegment(comment.getComment(), "2");
                comment.getSentenceAndClassDimensions().add(new SentenceAndClassDimension(sentence, classDim2));
            } else {
                comment.getSentenceAndClassDimensions()
                        .add(new SentenceAndClassDimension(comment.getComment(), classDim1));
            }

            if (classDim3 != null) {
                sentence = parseSentenceSegment(comment.getComment(), "3");
                comment.getSentenceAndClassDimensions().add(new SentenceAndClassDimension(sentence, classDim3));
            }

            if (classDim4 != null) {
                sentence = parseSentenceSegment(comment.getComment(), "4");
                comment.getSentenceAndClassDimensions().add(new SentenceAndClassDimension(sentence, classDim4));
            }
        } catch (Exception e) {
            System.out.println("with row index " + comment.getRowIndex());
            throw e;
        }
    }

    /**
     * 
     * @param comment
     */
    private static String parseSentenceSegment(String comment, String part) {
        try {
            String identifier = "#" + part + "#";
            if (comment.contains(part)) {
                int startIndex = comment.indexOf(identifier) + 3;
                int lastIndex = comment.lastIndexOf(identifier);

                String sentenceSegment = comment.substring(startIndex, lastIndex);
                return sentenceSegment;
            }
        } catch (Exception e) {
            throw new RuntimeException("error with part " + part + ",comment: " + comment);
        }

        return null;
    }

}