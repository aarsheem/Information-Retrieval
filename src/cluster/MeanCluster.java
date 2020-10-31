package cluster;

import index.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MeanCluster extends Cluster {
    private Map<String, Double> mean;
    public MeanCluster(Integer id, Document doc) {
        super(id, doc);
        mean = doc.getVector();
    }

    @Override
    public Double score(Document doc) {
        return this.dot(mean, doc.getVector());
    }

    @Override
    public void add(Document doc) {
        super.add(doc);
        Map<String, Double> vector = doc.getVector();
        Set<String> words = new HashSet<>(mean.keySet());
        words.addAll(vector.keySet());
        Double norm = 0.0;
        Integer size = this.docs.size();
        for(String word : words){
            Double first = mean.getOrDefault(word, 0.0);
            Double second = vector.getOrDefault(word, 0.0);
            Double score = (first * (size-1) + second)/size;
            mean.put(word, score);
            norm += score * score;
        }
        // normalize
        norm = Math.sqrt(norm);
        for(Map.Entry<String, Double> entry : mean.entrySet()){
            mean.put(entry.getKey(), entry.getValue()/norm);
        }
    }
}
