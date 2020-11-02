package cluster;

import index.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MeanCluster extends Cluster {
    private Map<String, Double> mean;
    private double norm;
    public MeanCluster(Integer id, Document doc) {
        super(id, doc);
        mean = new HashMap<> (doc.getVector()); //this is the change
        norm = doc.getNorm();
    }

    @Override
    public Double score(Document doc) {
        return this.cosine(mean, norm, doc.getVector(), doc.getNorm());
    }

    @Override
    public void add(Document doc) {
        super.add(doc);
        Map<String, Double> vector = doc.getVector();
        Set<String> words = new HashSet<>(mean.keySet());
        words.addAll(vector.keySet());
        Double sumSquare = 0.0;
        Integer size = this.docs.size();
        for(String word : words){
            Double first = mean.getOrDefault(word, 0.0);
            Double second = vector.getOrDefault(word, 0.0);
            Double score = (first * (size-1) + second)/size;
            mean.put(word, score);
            sumSquare += score * score;
        }
        norm = Math.sqrt(sumSquare);
    }
}
