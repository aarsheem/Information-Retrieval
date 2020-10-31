package network.proximity;

import index.PostingList;
import retrieval.model.Model;

import java.util.ArrayList;
import java.util.List;

public class TermNode extends ProximityNode {
    public TermNode(Model model, PostingList postingList){
        super(model, new ArrayList<>(), 0);
        this.postingList = postingList;
    }

    @Override
    public void generatePostingList(List<PostingList> postingLists, Integer range) {
    }

    @Override
    public String getName() {
        return "term";
    }
}
