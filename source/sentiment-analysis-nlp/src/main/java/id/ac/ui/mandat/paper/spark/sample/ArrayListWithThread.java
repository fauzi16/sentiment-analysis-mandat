package id.ac.ui.mandat.paper.spark.sample;

import java.util.ArrayList;
import java.util.List;

public class ArrayListWithThread {

    public static void main(String[] args) throws InterruptedException {
        final List<String> list = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                list.add("Fauzi test");
            }
        }).start();

        Thread.sleep(5000l);

        System.out.println(list.get(0));
    }
    
}
