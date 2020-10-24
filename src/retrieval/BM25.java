package retrieval;

import index.Index;
import index.PostingList;

import static java.lang.Math.log;

public class BM25 extends Query{
    private Double k1, k2, b;
    private String name;
    public BM25(Index index, Double _k1, Double _k2, Double _b){
        super(index);
        k1 = _k1;
        k2 = _k2;
        b = _b;
        name = "bm25-" + k1 + "-" + k2 + "-" + b;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Double score(PostingList word, Integer q, Integer notFoundDocId) {
        if(notFoundDocId != -1) return 0.0;
        Integer n = word.docFreq();
        Integer N = index.getDocumentsCount();
        Integer f = word.getDocCount();
        Integer D = index.getDocumentSize(word.getDoc());
        Double avgD = (double)index.getTotalWords()/N;
        Double K = k1 * (1 - b + b*D/avgD);
        Double num = (k1 + 1)*f*(k2 + 1)*q;
        Double den = (K + f)*(k2 + q);
        return log((N - n + 0.5)/(n + 0.5)) * num / den;
    }
}
