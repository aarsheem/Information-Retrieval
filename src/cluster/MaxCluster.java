package cluster;

import index.Document;

public class MaxCluster extends Cluster{
    public MaxCluster(Integer id, Document doc){
        super(id, doc);
    }

    @Override
    public Double score(Document doc) {
        Double score = Double.NEGATIVE_INFINITY;
        for(Document d : this.docs){
            score = Math.max(score, cosine(d, doc));
        }
        return score;
    }
}
