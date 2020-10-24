package network.belief;

import network.QueryNode;

import java.util.List;

public class WandNode extends BeliefNode {
    protected List<Integer> weights;
    public WandNode(List<QueryNode> c, List<Integer> weights){
        super(c);
        this.weights = weights;
    }

    @Override
    public Double score(Integer docId) {
        Double score = 0.0;
        for(int i = 0; i < children.size(); i++) score += weights.get(i) * children.get(i).score(docId);
        return score;
    }

    @Override
    public String getName() {
        return "wand";
    }
}