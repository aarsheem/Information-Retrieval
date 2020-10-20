package index;

import java.util.ArrayList;
import java.util.List;

public class PostingList {
    private List<Posting> postings;
    private Integer termFreq;
    private Integer iterator;

    public PostingList(){
        termFreq = 0;
        postings = new ArrayList<>();
        reset();
    }

    public PostingList(Integer[] arr, boolean isCompress){
        postings = new ArrayList<>();
        termFreq = 0;
        Integer id = 0;
        Integer docId = 0;
        while(arr.length > id){
            docId += arr[id++];
            Integer count = arr[id++];
            Integer pos = 0;
            for(int i = 0; i < count; i++) {
                if(isCompress){
                    pos += arr[id++];
                    add(docId, pos);
                } else add(docId, arr[id++]);
            }
            if(!isCompress) docId = 0;
        }
        reset();
    }

    public Integer[] toArray(boolean isCompress){
        List<Integer> arr = new ArrayList<>();
        Integer docID = 0;
        for(Posting p: postings){
            arr.add(p.getDocId() - docID);
            docID = isCompress ? p.getDocId() : 0;
            arr.add(p.getSize());
            Integer pos = 0;
            for(int i = 0; i < p.getSize(); i++){
                arr.add(p.getPosition(i) - pos);
                pos = isCompress ? p.getPosition(i) : 0;
            }
        }
        return arr.toArray(new Integer[arr.size()]);
    }

    public void add(Integer docId, Integer pos){
        if(postings.isEmpty() || !postings.get(postings.size() - 1).getDocId().equals(docId))
            postings.add(new Posting(docId, pos));
        else postings.get(postings.size()-1).add(pos);
        termFreq++;
    }

    public Integer docFreq(){
        return postings.size();
    }

    //Total number of occurrences of this word
    public Integer termFreq(){
        return termFreq;
    }

    public Posting getPosting(Integer pos){
        return postings.get(pos);
    }

    public Posting getPosting(){
        return postings.get(iterator);
    }

    public Integer getDocCount(){
        if(isEnd()) return -1;
        return postings.get(iterator).getSize();
    }

    public void reset(){
        iterator = 0;
    }

    public boolean isEnd(){
        return iterator == postings.size();
    }

    public void nextDoc(){
        if(!isEnd()) iterator++;
    }

    public Integer getDoc(){
        if(isEnd()) return -1;
        return postings.get(iterator).getDocId();
    }

    public void skipToDoc(Integer docId){
        while(!isEnd() && getDoc() < docId)
            nextDoc();
    }

    @Override
    public String toString() {
        String output = "";
        for(int i = 0; i < postings.size(); i++){
            output = output + " [" + postings.get(i).getDocId() + "]";
            for(int j = 0; j < postings.get(i).getSize(); j++){
                output = output + " " + postings.get(i).getPosition(j);
            }
        }
        return output;
    }
}
