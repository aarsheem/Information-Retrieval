package network.belief;

import network.QueryNode;

import java.util.List;

public class SumNode extends BeliefNode {
    public SumNode(List<? extends QueryNode> c){
        super(c);
    }

    @Override
    public Double score(Integer docId) {
        Double score = 0.0;
        for(QueryNode child : children) score += Math.exp(child.score(docId));
        return Math.log(score/children.size());
    }

    @Override
    public String getName() {
        return "sum";
    }
}