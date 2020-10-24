package network.filter;

import index.PostingList;
import network.QueryNode;
import network.proximity.ProximityNode;

public abstract class FilterNode implements QueryNode {
    protected QueryNode queryNode;
    protected PostingList filter;
    public FilterNode(QueryNode queryNode, ProximityNode filterNode){
        this.queryNode = queryNode;
        this.filter = filterNode.getPostingList();
    }

    @Override
    public Boolean hasMore() {
        return queryNode.hasMore();
    }

    @Override
    public void skipTo(Integer docId) {
        queryNode.skipTo(docId);
    }

}
