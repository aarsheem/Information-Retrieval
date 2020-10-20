package retrieval;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class Dirichlet extends Query {
    private Double mu;
    private String name;
    public Dirichlet(Index index, Double m){
        super(index);
        mu = m;
        name = "dirchlet-" + mu;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Double score(PostingList word, Integer q, Integer notFoundDocId) {
        Integer f, D;
        if(notFoundDocId != -1){
            D = index.getDocumentSize(notFoundDocId);
            f = 0;
        }
        else{
            D = index.getDocumentSize(word.getDoc());
            f = word.getDocCount();
        }
        Integer C = index.getTotalWords();
        Integer c = word.termFreq();
        Double num = f + mu * c / C;
        Double den = D + mu;
        return q * log(num/den);
    }
}
