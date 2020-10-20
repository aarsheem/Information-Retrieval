package retrieval;

import index.Index;
import index.Posting;
import index.PostingList;

import static java.lang.Integer.max;

public class Dice {
    private Index index;
    public Dice(Index index){
        this.index = index;
    }

    private Integer order(Integer first, Integer second){
        if(first - second == -1) return 0;
        else if(first - second < -1) return 1;
        else return 2;
    }

    public Double computeDice(String firstWord, String secondWord){
        PostingList first = index.getPostingList(firstWord);
        PostingList second = index.getPostingList(secondWord);
        Integer nFirst = first.termFreq();
        Integer nSecond = second.termFreq();
        Integer nTogether = 0;
        boolean isEnd = false;
        while(!isEnd){
            while(!first.getDoc().equals(second.getDoc())){
                if(first.getDoc() < second.getDoc()) first.skipToDoc(second.getDoc());
                else second.skipToDoc(first.getDoc());
                if(first.isEnd() || second.isEnd()){
                    isEnd = true;
                    break;
                }
            }
            if(isEnd) break;
            Posting firstPost = first.getPosting();
            Posting secondPost = second.getPosting();
            while(!(firstPost.isEnd() || secondPost.isEnd())){
                Integer o = order(firstPost.getPosition(), secondPost.getPosition());
                if(o == 0){
                    firstPost.nextPosition();
                    secondPost.nextPosition();
                    nTogether += 1;
                }
                else if(o == 1) firstPost.skipToPosition(secondPost.getPosition()-1);
                else secondPost.skipToPosition(firstPost.getPosition()+1);
            }
            first.nextDoc();
            second.nextDoc();
            if(first.isEnd() || second.isEnd()){
                isEnd = true;
            }
        }
        return nTogether/ (double) (nFirst + nSecond);
    }

    public String maxDice(String query){
        String[] vocab = index.getVocab();
        Double maxDice = -1.0;
        String second = "";
        for(String word: vocab){
            Double dice = computeDice(query, word);
            if(dice > maxDice){
                maxDice = dice;
                second = word;
            }
        }
        return second;
    }

}
