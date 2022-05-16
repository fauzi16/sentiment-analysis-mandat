package id.ac.ui.mandat.paper.spark.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

public class StemmingSample {

    public static void main(String[] args) throws IOException {
        Set<String> dictionary = new HashSet<String>();

        // Memuat file kata dasar dari distribusi JSastrawi
        // Jika perlu, anda dapat mengganti file ini dengan kamus anda sendiri
        InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = br.readLine()) != null) {
            dictionary.add(line);
        }

        Lemmatizer lemmatizer = new DefaultLemmatizer(dictionary);
        // Selesai setup JSastrawi
        // lemmatizer bisa digunakan berkali-kali

        System.out.println(lemmatizer.lemmatize("memakan"));
        System.out.println(lemmatizer.lemmatize("meminum"));
        System.out.println(lemmatizer.lemmatize("bernyanyi"));
    }

}
