package utility;

public class DocOrder implements Comparable<DocOrder>{
    Double score;
    Integer docId;

    public DocOrder(Double s, Integer d){
        score = s;
        docId = d;
    }

    public Double getScore() {
        return score;
    }

    public Integer getDocId(){
        return docId;
    }

    @Override
    public int compareTo(DocOrder other) {
        return other.score.compareTo(score);
    }

}
