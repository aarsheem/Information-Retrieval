package hw;

import index.Index;
import network.QueryNode;
import network.belief.*;
import network.proximity.TermNode;
import network.proximity.window.BandNode;
import network.proximity.window.OrderNode;
import network.proximity.window.UnorderNode;
import retrieval.Dirichlet;
import utility.DocOrder;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HW4 {
    private static final Double mu = 1500.;
    private static final Integer uwRange = 3, od1Range = 1;
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

    private static QueryNode getQueryNode(retrieval.Query model, List<TermNode> termNodes, Class<? extends QueryNode> queryNodeClass){
        if (OrderNode.class.equals(queryNodeClass)) {
            return new OrderNode(model, termNodes, od1Range);
        }
        else if(UnorderNode.class.equals(queryNodeClass)){
            return new UnorderNode(model, termNodes, uwRange * termNodes.size());
        }
        else if(SumNode.class.equals(queryNodeClass)){
            return new SumNode(termNodes);
        }
        else if(AndNode.class.equals(queryNodeClass)){
            return new AndNode(termNodes);
        }
        else if(OrNode.class.equals(queryNodeClass)){
            return new OrNode(termNodes);
        }
        else if(MaxNode.class.equals(queryNodeClass)){
            return new MaxNode(termNodes);
        }
        return null;
    }

    private static void printResults(retrieval.Query model, Index index, Class<? extends QueryNode> queryNodeClass) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter fileWriter = null;
        List<TermNode> termNodes = new ArrayList<>();
        for (int i = 0; i < queries.length; i++) {
            String[] terms = queries[i].split("\\s+");
            for(String term : terms) termNodes.add(new TermNode(model, index.getPostingList(term)));
            QueryNode node = getQueryNode(model, termNodes, queryNodeClass);
            List<String> results = getTrecrun(model.getName(), index, node, i+1);
            if(fileWriter == null){
                String filename = "data/HW4/" + node.getName() + ".trecrun";
                fileWriter = new PrintWriter(filename, "UTF-8");
            }
            results.forEach(fileWriter::println);
            termNodes.clear();
        }
        fileWriter.close();
    }

    private static List<String> getTrecrun(String modelName, Index index, QueryNode node, Integer id){
        List<String> results = new ArrayList<>();
        String myID = "aarsheemishr-";
        String name = node.getName() + "-" + modelName;
        List<DocOrder> topDocIds = network.Query.documentAtATime(node);
        for(int j = 0; j < topDocIds.size(); j++){
            Integer docId = topDocIds.get(j).getDocId();
            Double score = topDocIds.get(j).getScore();
            String queryId = String.format("%-3s", "Q" + id);
            String sceneId = String.format("%-40s", index.getSceneId(docId));
            String rank = String.format("%-5d",j+1);
            String scoreStr = String.format("%-10.5f",score);
            results.add(queryId + " skip " + sceneId + rank + scoreStr + myID + name);
        }
        return results;
    }

    public static void main(String[] args) {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        retrieval.Query model = new Dirichlet(index, mu);
        try {
            switch (args[0]) {
                case "build" -> HW1.buildIndex(filename, isCompress);
                case "od1" -> {
                    index.load(isCompress);
                    printResults(model, index, OrderNode.class);
                }
                case "uw" -> {
                    index.load(isCompress);
                    printResults(model, index, UnorderNode.class);
                }
                case "sum" -> {
                    index.load(isCompress);
                    printResults(model, index, SumNode.class);
                }
                case "and" -> {
                    index.load(isCompress);
                    printResults(model, index, AndNode.class);
                }
                case "or" -> {
                    index.load(isCompress);
                    printResults(model, index, OrNode.class);
                }
                case "max" -> {
                    index.load(isCompress);
                    printResults(model, index, MaxNode.class);
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
