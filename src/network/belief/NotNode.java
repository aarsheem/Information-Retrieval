package network.belief;

import network.Query;
import network.QueryNode;
import network.proximity.TermNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NotNode<T extends QueryNode> extends BeliefNode { //todo: Not sure if this is the best way to do this
    public NotNode(T c){
        super(Arrays.asList(c));
    }

    @Override
    public Double score(Integer docId) {
        Double score = children.get(0).score(docId);
        return Math.log(1 - Math.exp(score));
    }

    @Override
    public String getName() {
        return "not";
    }
}