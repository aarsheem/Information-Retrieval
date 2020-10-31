package retrieval.model;

import index.PostingList;

public class Count extends Model {

    @Override
    public Double score(PostingList word, Integer q){
        return (double)word.getDocCount() * q;
    }

    @Override
    public String getName(){
        return "count";
    }
}
