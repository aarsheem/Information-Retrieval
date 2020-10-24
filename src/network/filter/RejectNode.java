package network.filter;

import network.QueryNode;
import network.proximity.ProximityNode;

public class RejectNode extends FilterNode {
    public RejectNode(QueryNode query, ProximityNode filter){
        super(query, filter);
    }

    @Override
    public Integer nextCandidate() {
        return queryNode.nextCandidate();
    }

    @Override
    public Double score(Integer docId) {
        filter.skipToDoc(docId);
        if(filter.getDoc().equals(docId)) return null;
        return queryNode.score(docId);
    }

    @Override
    public String getName() {
        return "reject";
    }
}
