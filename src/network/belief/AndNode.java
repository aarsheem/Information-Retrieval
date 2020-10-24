package network.belief;

import network.QueryNode;
import network.belief.BeliefNode;

import java.util.List;

public class AndNode extends BeliefNode {
    public AndNode(List<? extends QueryNode> c){
        super(c);
    }

    @Override
    public Double score(Integer docId) {
        Double score = 0.0;
        for(QueryNode child : children) score += child.score(docId);
        return score;
    }

    @Override
    public String getName() {
        return "and";
    }
}
