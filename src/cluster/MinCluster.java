package cluster;

import index.Document;

public class MinCluster extends Cluster{
    public MinCluster(Integer id, Document doc){
        super(id, doc);
    }

    @Override
    public Double score(Document doc) {
        Double score = Double.POSITIVE_INFINITY;
        for(Document d : this.docs){
            score = Math.min(score, cosine(d, doc));
        }
        return score;
    }
}
