package network.proximity.window;

import index.Posting;
import index.PostingList;
import network.proximity.ProximityNode;

import java.util.ArrayList;
import java.util.List;

public abstract class WindowNode extends ProximityNode {
    public WindowNode(retrieval.Query model, List<? extends ProximityNode> children, Integer range){
        super(model, children, range);
    }

    @Override
    public void generatePostingList(List<PostingList> postingLists, Integer range) {
        this.postingList = new PostingList();
        List<Integer> docs = andDocuments(postingLists);
        List<Posting> postings = new ArrayList<>();
        for(PostingList word : postingLists) word.reset();
        for(Integer docId : docs){
            for(PostingList word : postingLists){
                word.skipToDoc(docId);
                postings.add(word.getPosting());
            }
            List<Integer> positions = window(postings, range);
            for(Integer position : positions) this.postingList.add(docId, position);
            postings.clear();
        }
    }

    protected abstract List<Integer> window(List<Posting> postings, Integer range);

}
