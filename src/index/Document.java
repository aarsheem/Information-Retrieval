package index;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.log;

public class Document implements Comparable<Document>{
    // for reading and writing
    private Long offset;
    private Integer count;

    private String sceneId;
    private String playId;
    private Integer size;
    private Map<String, Integer> freq;
    private Map<String, Double> vector = null;

    public Document(String scene, String play){
        this.sceneId = scene;
        this.playId = play;
        freq = new HashMap<>();
        size = 0;
    }

    public Document(String scene, String play, Long offset, Integer count){
        this.sceneId = scene;
        this.playId = play;
        this.offset = offset;
        this.count = count;
        freq = new HashMap<>();
        size = 0;
    }

    public void add(String word){
        freq.put(word, freq.containsKey(word) ? freq.get(word)+1 : 1);
        size += 1;
    }

    public void add(String word, Integer count){
        freq.put(word, count);
        size += count;
    }

    //call before calling getVector
    public void vectorize(Index index){
        if(vector != null) return;
        vector = new HashMap<>();
        Double norm = 0.0;
        for(Map.Entry<String, Integer> entry : freq.entrySet()){
            String word = entry.getKey();
            Integer f = entry.getValue();
            // Integer D = size;
            Integer n  = index.getDocFreq(word);
            Integer N = index.getDocumentsCount();
            Double idf = log((double)N/n);  //todo: check with prof.
            Double tf = 1 + log(f);
            Double score = tf * idf;
            vector.put(word, score);
            norm += score * score;
        }
        norm = Math.sqrt(norm);
        // norm = 1
        for(Map.Entry<String, Double> entry : vector.entrySet()){
            vector.put(entry.getKey(), entry.getValue()/norm);
        }
    }

    @Override
    public int compareTo(Document other) {
        return size.compareTo(other.size);
    }

    public Map<String, Integer> getFreq() {
        return freq;
    }

    public Map<String, Double> getVector(){
        return vector;
    }

    public Integer getSize() {
        return size;
    }

    public String getPlayId() {
        return playId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public Long getOffset(){
        return offset;
    }

    public Integer getCount(){
        return count;
    }


}
