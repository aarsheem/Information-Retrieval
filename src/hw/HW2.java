package hw;

import index.Document;
import index.Index;
import index.PostingList;
import retrieval.*;
import utility.DocOrder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HW2 {
    private static final Double k1 = 1.5, k2 = 500., b = 0.75, lambda = 0.2, mu = 1200.;
    private static final String[] queries = {
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

    private static void printResults(Query model, Index index) throws FileNotFoundException, UnsupportedEncodingException {
        String modelName = model.getName().split("-", -1)[0];
        String filename = "data/HW2/" + modelName + ".trecrun";
        PrintWriter fileWriter = new PrintWriter(filename, "UTF-8");
        for (int i = 0; i < queries.length; i++) {
            List <String> results = getTrecrun(model, index, queries[i], i+1);
            results.forEach(fileWriter::println);
        }
        fileWriter.close();
    }

    private static List<String> getTrecrun(Query model, Index index, String query, Integer id){
        List<String> results = new ArrayList<>();
        String myID = "aarsheemishr-";
        List<DocOrder> topDocIds = model.documentAtATime(query.split("\\s+"));
        for(int j = 0; j < topDocIds.size(); j++){
            Integer docId = topDocIds.get(j).getDocId();
            Double score = topDocIds.get(j).getScore();
            String queryId = String.format("%-3s", "Q" + id);
            String sceneId = String.format("%-40s", index.getSceneId(docId));
            String rank = String.format("%-5d",j+1);
            String scoreStr = String.format("%-10.5f",score);
            results.add(queryId + " skip " + sceneId + rank + scoreStr + myID + model.getName());
        }
        return results;
    }

    public static void main(String[] args) {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        Query model;
        try {
            switch (args[0]) {
                case "build" -> HW1.buildIndex(filename, isCompress);
                case "vector" -> {
                    index.load(isCompress);
                    model = new VectorSpace(index);
                    printResults(model, index);
                }
                case "bm25" -> {
                    index.load(isCompress);
                    model = new BM25(index, k1, k2, b);
                    printResults(model, index);
                }
                case "mercer" -> {
                    index.load(isCompress);
                    model = new JelinekMercer(index, lambda);
                    printResults(model, index);
                }
                case "dirichlet" -> {
                    index.load(isCompress);
                    model = new Dirichlet(index, mu);
                    printResults(model, index);
                }
            }
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
        }
    }
}