package network.proximity.window;

import index.Posting;
import network.proximity.ProximityNode;
import retrieval.model.Model;

import java.util.ArrayList;
import java.util.List;

public class OrderNode extends WindowNode {

    public OrderNode(Model model, List<? extends ProximityNode> children, Integer range){
        super(model, children, range);
    }

    protected List<Integer> window(List<Posting> postings, Integer range){
        List<Integer> result = new ArrayList<>();
        boolean isEnd = false;
        boolean isFound;
        Posting maxPosting, minPosting;
        Integer order = 0;
        do{
            //find max posting, note its order 'o'
            //skip all postings based on their order i : maxPos + (i - o) * range
            //check all postings
            //if found then increase all to next position
            //else increase just the minimum posting
            isFound = true;
            maxPosting = new Posting(-1, Integer.MIN_VALUE);
            minPosting = new Posting(-1, Integer.MAX_VALUE);
            for(int i = 0; i < postings.size(); i++) if(maxPosting.getPosition() < postings.get(i).getPosition()) {maxPosting = postings.get(i); order = i;}
            for(int i = 0; i < postings.size(); i++){
                postings.get(i).skipToPosition(maxPosting.getPosition() + (i - order) * range);
                if(postings.get(i).isEnd()){
                    isEnd = true; isFound = false; break;
                }
                if(i != 0 && (postings.get(i).getPosition() - postings.get(i-1).getPosition() > range
                    || postings.get(i).getPosition() - postings.get(i-1).getPosition() < 0)) isFound = false;
                if(minPosting.getPosition() > postings.get(i).getPosition()) minPosting = postings.get(i);
            }
            if(isFound){
                result.add(postings.get(0).getPosition());
                for(Posting posting : postings) posting.nextPosition();
            }
            else minPosting.nextPosition();
        } while(!isEnd);
        return result;
    }

    @Override
    public String getName() {
        return "od1";
    }
}
