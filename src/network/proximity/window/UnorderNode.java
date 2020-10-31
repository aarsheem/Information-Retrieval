package network.proximity.window;

import index.Posting;
import network.proximity.ProximityNode;
import retrieval.model.Model;

import java.util.ArrayList;
import java.util.List;

public class UnorderNode extends WindowNode {

    public UnorderNode(Model model, List<? extends ProximityNode> children, Integer range){
        super(model, children, range);
    }

    protected List<Integer> window(List<Posting> postings, Integer range){
        List<Integer> result = new ArrayList<>();
        boolean isEnd = false;
        boolean isFound;
        Posting maxPosting, minPosting;
        do{
            //find max posting
            //skip all postings to maxPos - range + 1
            //find min posting
            //all positions should be less than minPos + range
            //if found then increase all to next position
            //else increase just the min posting
            isFound = true;
            maxPosting = new Posting(-1, Integer.MIN_VALUE);
            minPosting = new Posting(-1, Integer.MAX_VALUE);
            for(Posting posting : postings) if(maxPosting.getPosition() < posting.getPosition()) maxPosting = posting;
            for(Posting posting : postings){
                posting.skipToPosition(maxPosting.getPosition() - range + 1);
                if(posting.isEnd()){
                    isEnd = true; isFound = false; break;
                }
                if(minPosting.getPosition() > posting.getPosition()) minPosting = posting;
            }
            for(Posting posting : postings) if(posting.getPosition() - minPosting.getPosition() >= range) {isFound = false; break;}
            if(isFound){
                result.add(minPosting.getPosition());
                for(Posting posting : postings) posting.nextPosition();
            }
            else minPosting.nextPosition();
        } while(!isEnd);
        return result;
    }

    @Override
    public String getName() {
        return "uw";
    }
}