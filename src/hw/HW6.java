package hw;

import index.Index;
import network.QueryNode;
import network.belief.AndNode;
import network.prior.Prior;
import network.proximity.TermNode;
import retrieval.model.Dirichlet;
import retrieval.model.Model;
import utility.DocOrder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static hw.HW1.buildIndex;

public class HW6 {
    private static final Double mu = 1500.;

    private static void printResults(Index index, QueryNode priorNode) throws FileNotFoundException, UnsupportedEncodingException {
        String text =  "antony strumpet";//"the king queen royalty";
        Model model = new Dirichlet(index, mu);
        List<QueryNode> leafNodes = new ArrayList<>();
        String[] terms = text.split("\\s+");
        for(String term : terms) leafNodes.add(new TermNode(model, index.getPostingList(term)));
        leafNodes.add(priorNode);
        QueryNode node = new AndNode(leafNodes);
        List<DocOrder> topDocIds = network.Query.documentAtATime(node);
        List<String> results = HW2.getTrecrun(topDocIds, index, 1, node.getName() + "-" + priorNode.getName() + "-" + model.getName());
        String filename = "data/HW6/" + priorNode.getName() + ".trecrun";
        PrintWriter fileWriter = new PrintWriter(filename, "UTF-8");
        results.forEach(fileWriter::println);
        fileWriter.close();
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        Index index = new Index();
        QueryNode priorNode = null;
        switch(args[0]) {
            case "build":
                buildIndex(filename, isCompress);
                return;
            case "uniform":
                index.load(isCompress);
                priorNode = new Prior("data/HW6/uniform.prior");
                break;
            case "random":
                index.load(isCompress);
                priorNode = new Prior("data/HW6/random.prior");
                break;
        }
        printResults(index, priorNode);
    }
}
