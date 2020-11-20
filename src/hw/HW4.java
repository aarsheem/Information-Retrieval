package hw;

import index.Index;
import network.QueryNode;
import network.belief.*;
import network.proximity.TermNode;
import network.proximity.window.OrderNode;
import network.proximity.window.UnorderNode;
import retrieval.model.Dirichlet;
import retrieval.model.Model;
import utility.DocOrder;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HW4 {
    private static final Double mu = 1500.;
    private static final Integer uwRange = 3, od1Range = 1;
    private static final String[] texts = {
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

    private static QueryNode getQueryNode(Model model, List<TermNode> termNodes, Class<? extends QueryNode> queryNodeClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (OrderNode.class.equals(queryNodeClass)) {
            return new OrderNode(model, termNodes, od1Range);
        }
        else if(UnorderNode.class.equals(queryNodeClass)){
            return new UnorderNode(model, termNodes, uwRange * termNodes.size());
        }
        else{
            //and, or, max, sum
            QueryNode node = queryNodeClass.getConstructor(List.class).newInstance(termNodes);
            return node;
        }
    }

    private static void printResults(Model model, Index index, Class<? extends QueryNode> queryNodeClass) throws FileNotFoundException, UnsupportedEncodingException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PrintWriter fileWriter = null;
        List<TermNode> termNodes = new ArrayList<>();
        for (int i = 0; i < texts.length; i++) {
            String[] terms = texts[i].split("\\s+");
            for(String term : terms) termNodes.add(new TermNode(model, index.getPostingList(term)));
            QueryNode node = getQueryNode(model, termNodes, queryNodeClass);
            List<DocOrder> topDocIds = network.Query.documentAtATime(node);
            List<String> results = HW2.getTrecrun(topDocIds, index, i+1, node.getName() + "-" + model.getName());
            if(fileWriter == null){
                String filename = "data/HW4/" + node.getName() + ".trecrun";
                fileWriter = new PrintWriter(filename, "UTF-8");
            }
            results.forEach(fileWriter::println);
            termNodes.clear();
        }
        fileWriter.close();
    }


    public static void main(String[] args) {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        Model model = new Dirichlet(index, mu);
        try {
            switch (args[0]) {
                case "build" -> {
                    HW1.buildIndex(filename, isCompress);
                    return;
                }
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
        }
        catch (FileNotFoundException | UnsupportedEncodingException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
