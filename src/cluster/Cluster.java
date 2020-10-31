package cluster;

import index.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Cluster {
    private Integer id;
    protected List<Document> docs;
    public Cluster(Integer id, Document doc){
        this.id = id;
        docs = new ArrayList<>();
        docs.add(doc);
    }

    public Integer getId() {
        return this.id;
    }

    public List<Document> getDocuments() {
        return this.docs;
    }

    public void add(Document doc) {
        docs.add(doc);
    }

    protected Double dot(Map<String, Double> first, Map<String, Double> second){
        if(first.size() > second.size()) {
            Map<String, Double> temp = first;
            first = second;
            second = temp;
        }
        Double dot = 0.0;
        for(Map.Entry<String, Double> entry : first.entrySet()){
            String word = entry.getKey();
            Double secondScore = second.getOrDefault(word, 0.0);
            dot += first.get(word) * secondScore;
        }
        return dot;
    }

    public abstract Double score(Document doc);
}
