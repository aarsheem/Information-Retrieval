package network.belief;

import network.QueryNode;

import java.util.ArrayList;
import java.util.List;

public abstract class BeliefNode implements QueryNode {
    protected List<? extends QueryNode> children;
    public BeliefNode(List<? extends QueryNode> c){
        children = c;
    }

    @Override
    public Integer nextCandidate() {
        Integer docId = Integer.MAX_VALUE;
        for(QueryNode child: children){
            if(child.hasMore()) docId = Math.min(docId, child.nextCandidate());
        }
        if(docId.equals(Integer.MAX_VALUE)) return -1;
        return docId;
    }

    @Override
    public Boolean hasMore() {
        if(nextCandidate().equals(-1)) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    @Override
    public void skipTo(Integer docId) {
        for(QueryNode child: children) child.skipTo(docId);
    }
}
