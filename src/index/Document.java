package index;


public class Document implements Comparable<Document>{
    private String sceneId;
    private String playId;
    private Integer size;
    public Document(){};
    public Document(String scene, String play, Integer sz){
        sceneId = scene;
        playId = play;
        size = sz;
    }

    @Override
    public int compareTo(Document other) {
        return size.compareTo(other.size);
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

}
