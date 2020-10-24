package network;

public interface QueryNode {
    public Integer nextCandidate();
    public Boolean hasMore();
    public void skipTo(Integer docId);
    public Double score(Integer docId);
    public String getName();
}
