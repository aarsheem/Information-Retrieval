package network.proximity.window;

import index.Posting;
import index.PostingList;
import network.proximity.ProximityNode;
import retrieval.model.Model;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.max;

public abstract class WindowNode extends ProximityNode {
    public WindowNode(Model model, List<? extends ProximityNode> children, Integer range){
        super(model, children, range);
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
