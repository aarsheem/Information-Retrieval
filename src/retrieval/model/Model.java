package retrieval.model;

import index.Index;
import index.PostingList;

public abstract class Model {

    public abstract Double score(PostingList word, Integer q);

    public abstract String getName();

    //In case the document doesn't have the query word
    public Double scoreNotFound(PostingList word, Integer q, Integer docId){
        return 0.0;
    }
}
