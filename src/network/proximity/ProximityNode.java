package network.proximity;

import index.PostingList;
import network.QueryNode;
import retrieval.model.Model;

import java.util.ArrayList;
import java.util.List;


public abstract class ProximityNode implements QueryNode {
    protected Model model;
    protected PostingList postingList;

    public ProximityNode(Model model, List<? extends ProximityNode> children, Integer range){
        this.model = model;
        List<PostingList> words = new ArrayList<>();
        for(ProximityNode child : children) words.add(child.getPostingList());
        generatePostingList(words, range);
    }

    @Override
    public Integer nextCandidate() {
        return postingList.getDoc();
    }

    @Override
    public Boolean hasMore() {
        return !postingList.isEnd();
    }

    @Override
    public void skipTo(Integer docId) {
        postingList.skipToDoc(docId);
    }

    @Override
    public Double score(Integer docId) {
        if(postingList.getDoc().equals(docId)){
            return model.score(postingList, 1);
        }
        return model.scoreNotFound(postingList, 1, docId);
    }

    public PostingList getPostingList(){
        return this.postingList;
    }

    public abstract void generatePostingList(List<PostingList> postingLists, Integer range);
}
