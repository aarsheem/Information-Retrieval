package retrieval;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class VectorSpace extends Query{
    private String name = "vector";
    public VectorSpace(Index index){
        super(index);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Double score(PostingList word, Integer q, Integer notFoundDocId) {
        if(notFoundDocId != -1) return 0.0;
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
