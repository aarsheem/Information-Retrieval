package network.belief;

import network.QueryNode;

import java.util.List;

public class MaxNode extends BeliefNode {
    public MaxNode(List<? extends QueryNode> c){
        super(c);
    }

    @Override
    public Double score(Integer docId) {
        Double score = Double.NEGATIVE_INFINITY;
        for(QueryNode child : children) score = Math.max(child.score(docId), score);
        return score;
    }

    @Override
    public String getName() {
        return "max";
    }
}