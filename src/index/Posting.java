package index;

import java.util.ArrayList;
import java.util.List;

public class Posting {
    private Integer docId;
    private List<Integer> positions;
    private Integer iterator;

    public Posting(Integer doc, Integer pos){
        this.docId = doc;
        positions = new ArrayList<>();
        positions.add(pos);
        reset();
    }

    public void add(Integer pos){
        positions.add(pos);
    }

    public Integer getSize(){
        return positions.size();
    }

    public Integer getDocId() {
        return this.docId;
    }

    public Integer getPosition(Integer id){
        return positions.get(id);
    }

    public Integer getPosition(){
        if(!isEnd()) return positions.get(iterator);
        return -1;
    }

    public void reset(){
        iterator = 0;
    }

    public boolean isEnd(){
        if(iterator >= positions.size()) return true;
        return false;
    }

    public void skipToPosition(Integer pos){
        while(!isEnd() && positions.get(iterator) < pos) iterator++;
    }

    public void nextPosition(){
        if(!isEnd()) iterator++;
    }
}
