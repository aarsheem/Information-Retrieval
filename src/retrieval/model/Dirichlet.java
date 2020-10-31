package retrieval.model;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class Dirichlet extends Model {
    private final Double mu;
    private final Index index;
    public Dirichlet(Index index, Double m){
        this.index = index;
        this.mu = m;
    }

    @Override
    public String getName() {
        return "dirichlet-" + mu;
    }

    @Override
    public Double score(PostingList word, Integer q) {
        Integer D = index.getDocumentSize(word.getDoc());
        Integer f = word.getDocCount();
        Integer C = index.getTotalWords();
        Integer c = word.termFreq();
        Double num = f + mu * c / C;
        Double den = D + mu;
        return q * log(num/den);
    }

    @Override
    public Double scoreNotFound(PostingList word, Integer q, Integer docId) {
        Integer D = index.getDocumentSize(docId);
        Integer C = index.getTotalWords();
        Integer c = word.termFreq();
        Double num = mu * c / C;
        Double den = D + mu;
        return q * log(num/den);
    }
}
