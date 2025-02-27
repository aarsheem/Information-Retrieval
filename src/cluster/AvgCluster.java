package cluster;

import index.Document;

public class AvgCluster extends Cluster{
    public AvgCluster(Integer id, Document doc){
        super(id, doc);
    }

    @Override
    public Double score(Document doc) {
        Double score = 0.0;
        for(Document d : this.docs){
            score += cosine(d, doc);
        }
        return score/this.docs.size();
    }
}

