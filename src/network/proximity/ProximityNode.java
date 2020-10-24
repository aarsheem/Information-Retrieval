package network.proximity;

import index.PostingList;
import network.QueryNode;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.max;

public abstract class ProximityNode implements QueryNode {
    protected retrieval.Query model;
    protected PostingList postingList;

    public ProximityNode(retrieval.Query model, List<? extends ProximityNode> children, Integer range){
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
            return model.score(postingList, 1, -1);
        }
        return model.score(postingList, 1, docId);
    }

    public PostingList getPostingList(){
        return this.postingList;
    }

    protected List<Integer> andDocuments(List<PostingList> postingLists){
        Integer currDocId = -1;
        boolean isEnd = false;
        List<Integer> commonDocs = new ArrayList<>();
        do{
            for(PostingList word : postingLists){
                currDocId = max(currDocId, word.getDoc());
                isEnd = isEnd || word.isEnd();
            }
            for(PostingList word : postingLists) {
                word.skipToDoc(currDocId);
                if(word.getDoc().equals(currDocId)){
                    word.nextDoc();
                }
                else{
                    currDocId = -1;
                    break;
                }
            }
            if(!currDocId.equals(-1)){
                commonDocs.add(currDocId);
            }
        } while(!isEnd);
        return commonDocs;
    }

    public abstract void generatePostingList(List<PostingList> postingLists, Integer range);
}
