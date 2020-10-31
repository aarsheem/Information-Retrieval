package cluster;

import index.Document;
import index.Index;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.log;


public class Query {
    private Index index;
    public Query(Index index){
        this.index = index;
    }

    public List<Cluster> documentAtATime(Class<? extends Cluster> clusterClass, Double threshold) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Cluster> clusters = new ArrayList<>();
        Double maxScore;
        Cluster nearestCluster = null;
        for(int i = 0; i < index.getDocumentsCount(); i++){
            Document doc = index.getDocument(i);
            doc.vectorize(index);
            maxScore = Double.NEGATIVE_INFINITY;
            for(Cluster c: clusters){
                Double score = c.score(doc);
                if(score > maxScore){
                    maxScore = score;
                    nearestCluster = c;
                }
            }
            if(maxScore > threshold) nearestCluster.add(doc);
            else{
                Cluster another = clusterClass.getConstructor(Integer.class, Document.class).newInstance(clusters.size(), doc);
                clusters.add(another);
            }
        }
        return clusters;
    }
}
