package hw;

import index.Index;
import retrieval.*;
import retrieval.model.*;
import utility.DocOrder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HW2 {
    private static final Double k1 = 1.5, k2 = 500., b = 0.75, lambda = 0.2, mu = 1500.;
    private static String[] texts = {
        "the king queen royalty",
        "servant guard soldier",
        "hope dream sleep",
        "ghost spirit",
        "fool jester player",
        "to be or not to be",
        "alas",
        "alas poor",
        "alas poor yorick",
        "antony strumpet"
    };

    private static void printResults(Model model, Query query, Index index) throws FileNotFoundException, UnsupportedEncodingException {
        String modelName = model.getName();
        String outFile = "data/HW2/" + modelName.split("-", -1)[0] + ".trecrun";
        PrintWriter fileWriter = new PrintWriter(outFile, "UTF-8");
        for(int i = 0; i < texts.length; i++){
            String text = texts[i];
            List<DocOrder> topDocIds = query.documentAtATime(model, text.split("\\s+"));
            List<String> results = getTrecrun(topDocIds, index, i+1, model.getName());
            results.forEach(fileWriter::println);
        }
        fileWriter.close();
    }

    public static List<String> getTrecrun(List<DocOrder> topDocIds, Index index, Integer id, String modelName){
        List<String> results = new ArrayList<>();
        String myID = "aarsheemishr-";
        for(int j = 0; j < topDocIds.size(); j++){
            String queryId = String.format("%-3s", "Q" + id);
            Integer docId = topDocIds.get(j).getDocId();
            String sceneId = String.format("%-40s", index.getSceneId(docId));
            String rank = String.format("%-5d",j+1);
            Double score = topDocIds.get(j).getScore();
            String scoreStr = String.format("%-10.5f",score);
            results.add(queryId + " skip " + sceneId + rank + scoreStr + myID + modelName);
        }
        return results;
    }

    public static void main(String[] args) {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        Query query = new Query(index);
        Model model = null;
        try {
            switch (args[0]) {
                case "build" -> {
                    HW1.buildIndex(filename, isCompress);
                    return;
                }
                case "vector" -> {
                    index.load(isCompress);
                    model = new VectorSpace(index);
                }
                case "bm25" -> {
                    index.load(isCompress);
                    model = new BM25(index, k1, k2, b);
                }
                case "mercer" -> {
                    index.load(isCompress);
                    model = new JelinekMercer(index, lambda);
                }
                case "dirichlet" -> {
                    index.load(isCompress);
                    model = new Dirichlet(index, mu);
                }
            }
            printResults(model, query, index);
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
        }
    }
}