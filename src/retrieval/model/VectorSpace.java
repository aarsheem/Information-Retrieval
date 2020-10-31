package retrieval.model;

import index.Index;
import index.PostingList;
import retrieval.model.Model;

import static java.lang.Math.log;

public class VectorSpace extends Model {
    private final Index index;
    public VectorSpace(Index index){
        this.index = index;
    }

    @Override
    public String getName() {
        return "vector";
    }

    @Override
    public Double score(PostingList word, Integer q) {
        Integer n = word.docFreq();
        Integer N = index.getDocumentsCount();
        Integer D = index.getDocumentSize(word.getDoc());
        Integer f = word.getDocCount();
        Double idf = log((double)N/n);
        Double tfd = 1 + log(f);
        Double tfq = 1 + log(q);
        return (tfd * tfq * idf * idf)/D;
    }
}
