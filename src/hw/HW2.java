package hw;

import index.Document;
import index.Index;
import index.PostingList;
import retrieval.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HW2 {
    private static Double k1 = 1.5, k2 = 500., b = 0.75, lambda = 0.2, mu = 1200.;
    private static String[] queries = {
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
        String myID = "aarsheemishr-";
        PrintWriter fileWriter = new PrintWriter(filename, "UTF-8");
        for (int i = 0; i < queries.length; i++){
            String query = queries[i];
            List<Query.DocOrder> topDocIds = model.documentAtATime(query.split("\\s+"));
            for(int j = 0; j < topDocIds.size(); j++){
                Integer docId = topDocIds.get(j).getDocId();
                Double score = topDocIds.get(j).getScore();
                String queryId = String.format("%-3s", "Q" + (i+1));
                String sceneId = String.format("%-40s", index.getSceneId(docId));
                String rank = String.format("%-5d",j+1);
                String scoreStr = String.format("%-10.5f",score);
                fileWriter.println(queryId + " skip " + sceneId + rank + scoreStr + myID + model.getName());
            }
        }
        fileWriter.close();
    }

    public static void main(String[] args) {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        Query model;
        try {
            switch(args[0]) {
                case "build":
                    HW1.buildIndex(filename, isCompress);
                    break;
                case "vector":
                    index.load(isCompress);
                    model = new VectorSpace(index);
                    printResults(model, index);
                    break;
                case "bm25":
                    index.load(isCompress);
                    model = new BM25(index, k1, k2, b);
                    printResults(model, index);
                    break;
                case "mercer":
                    index.load(isCompress);
                    model = new JelinekMercer(index, lambda);
                    printResults(model, index);
                    break;
                case "dirichlet":
                    index.load(isCompress);
                    model = new Dirichlet(index, mu);
                    printResults(model, index);
                    break;
            }
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
        }
    }
}