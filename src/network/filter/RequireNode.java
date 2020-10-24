package network.filter;

import network.QueryNode;
import network.proximity.ProximityNode;

public class RequireNode extends FilterNode {
    public RequireNode(QueryNode queryNode, ProximityNode filterNode){
        super(queryNode, filterNode);
    }

    @Override
    public Integer nextCandidate() {
        Integer docId = queryNode.nextCandidate();
        filter.skipToDoc(docId);
        return Math.max(docId, filter.getDoc());
    }

    @Override
    public Double score(Integer docId) {
        filter.skipToDoc(docId);
        if(!filter.getDoc().equals(docId)) return null;
        return queryNode.score(docId);
    }

    @Override
    public String getName() {
        return "require";
    }
}
