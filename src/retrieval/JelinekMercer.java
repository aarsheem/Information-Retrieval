package retrieval;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class JelinekMercer extends Query {
    private Double lambda;
    private String name;
    public JelinekMercer(Index index, Double lam){
        super(index);
        lambda = lam;
        name = "mercer-" + lam;
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
        return q * log((1-lambda)*f/D + lambda*c/C);
    }
}
