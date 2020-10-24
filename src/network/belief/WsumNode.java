package network.belief;

import network.QueryNode;

import java.util.List;

public class WsumNode extends BeliefNode {
    protected List<Integer> weights;
    public WsumNode(List<QueryNode> c, List<Integer> weights){
        super(c);
        this.weights = weights;
    }

    @Override
    public Double score(Integer docId) {
        Double score = 0.0;
        Double den = 0.0;
        for(int i = 0; i < children.size(); i++){
            score += weights.get(i) * Math.exp(children.get(i).score(docId));
            den += weights.get(i);
        }
        return Math.log(score/den);
    }

    @Override
    public String getName() {
        return "wsum";
    }
}