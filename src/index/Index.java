package index;

import utility.Compression;
import utility.EmptyCompression;
import utility.VbyteCompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.*;

public class Index {
    private Map<String, Lookup> lookup;
    private List<Document> docs;
    private Integer totalWords;
    private Compression compress;
    RandomAccessFile invertedIndex;

    private class Lookup{
        public final Long offset;
        public final Integer count;
        public Lookup(String x, String y) {
            this.offset = Long.parseLong(x);
            this.count = Integer.parseInt(y);
        }
    }

    public Index(){
        docs = new ArrayList<>();
        lookup = new HashMap<>();
        totalWords = 0;
    }

    public void load(boolean isCompress) throws FileNotFoundException {
        String indexName, lookupName;
        if(isCompress){
            compress = new VbyteCompression();
            indexName = "inverted_index_compressed.txt";
            lookupName = "lookup_compressed.txt";
        }
        else{
            compress = new EmptyCompression();
            indexName = "inverted_index.txt";
            lookupName = "lookup.txt";
        }
        File indexFile = new File("data/" + indexName);
        invertedIndex = new RandomAccessFile(indexFile, "r");
        totalWords = readDocuments("data/documents.txt");
        loadLookup("data/" + lookupName);

    }

    private void loadStringMap(String filename, Map<Integer, String> map) throws FileNotFoundException {
        File file = new File(filename);
        Scanner fileReader = new Scanner(file);
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            map.put(Integer.parseInt(words[0]), words[1]);
        }
    }

    private void loadLookup(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner fileReader = new Scanner(file);
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            lookup.put(words[0], new Lookup(words[1], words[2]));
        }
        fileReader.close();
    }

    public PostingList getPostingList(String word){
        Lookup obj = lookup.get(word);
        if(obj == null) {
            System.out.println(word + " not found");
            return null;
        }
        Integer[] arr = compress.decode(invertedIndex, obj.offset, obj.count);
        return new PostingList(arr, compress.isCompress());
    }

    private Integer readDocuments(String filename) throws FileNotFoundException {
        Integer total = 0, curr;
        File file = new File(filename);
        Scanner fileReader = new Scanner(file);
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            curr = Integer.parseInt(words[2]);
            docs.add(new Document(words[0], words[1], curr));
            total += curr;
        }
        return total;
    }

    public String[] getVocab(){
        return lookup.keySet().toArray(new String[0]);
    }

    public Integer getVocabSize(){
        return lookup.size();
    }

    public Integer getDocumentsCount(){
        return docs.size();
    }

    public Integer getDocumentSize(Integer id){
        return docs.get(id).getSize();
    }

    public String getSceneId(Integer id){
        return docs.get(id).getSceneId();
    }

    public Integer getTotalWords(){
        return totalWords;
    }
}
