package retrieval.model;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class JelinekMercer extends Model {
    private final Double lambda;
    private final Index index;
    public JelinekMercer(Index index, Double lam){
        this.index = index;
        this.lambda = lam;
    }

    @Override
    public String getName() {
        return "mercer-" + this.lambda;
    }

    @Override
    public Double score(PostingList word, Integer q) {
        Integer D = index.getDocumentSize(word.getDoc());
        Integer f = word.getDocCount();
        Integer C = index.getTotalWords();
        Integer c = word.termFreq();
        return q * log((1-lambda)*f/D + lambda*c/C);
    }

    @Override
    public Double scoreNotFound(PostingList word, Integer q, Integer docId) {
        Integer C = index.getTotalWords();
        Integer c = word.termFreq();
        return q * log(lambda*c/C);
    }
}
