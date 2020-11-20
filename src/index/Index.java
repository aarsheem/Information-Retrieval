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
    private Map<Integer, String> orderedWords;
    private Map<String, Integer> docFreq;
    private List<Document> docs;
    private Integer totalWords;
    private Compression compress;
    RandomAccessFile invertedIndex, documents;

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
        orderedWords = new HashMap<>();
        docFreq = new HashMap<>();
        totalWords = 0;
    }

    public void load(boolean isCompress) throws FileNotFoundException {
        String indexName, lookupName, docName;
        if(isCompress){
            compress = new VbyteCompression();
            indexName = "inverted_index_compressed.txt";
            lookupName = "lookup_compressed.txt";
            docName = "documents_compressed.txt";
        }
        else{
            compress = new EmptyCompression();
            indexName = "inverted_index.txt";
            lookupName = "lookup.txt";
            docName = "documents.txt";
        }
        File indexFile = new File("data/" + indexName);
        File docFile = new File("data/" + docName);
        invertedIndex = new RandomAccessFile(indexFile, "r");
        documents = new RandomAccessFile(docFile, "r");
        totalWords = readDocuments("data/document_info.txt");
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
        Integer count = 0;
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            lookup.put(words[0], new Lookup(words[1], words[2]));
            orderedWords.put(count, words[0]);
            count++;
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
        Integer total = 0, size, count;
        Double prior;
        Long offset;
        File file = new File(filename);
        Scanner fileReader = new Scanner(file);
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            size = Integer.parseInt(words[2]);
            offset = Long.parseLong(words[3]);
            count = Integer.parseInt(words[4]);
            docs.add(new Document(words[0], words[1], offset, count, size));
            total += size;
        }
        return total;
    }

    public Document getDocument(Integer docId){
        if(docs.get(docId).getSize() != 0) return docs.get(docId);
        Integer[] arr = compress.decode(documents, docs.get(docId).getOffset(), docs.get(docId).getCount());
        Integer lastKey = 0;
        for(int i = 0; i < arr.length; i+=2){
            docs.get(docId).add(orderedWords.get(arr[i] + lastKey), arr[i+1]);
            if(compress.isCompress()) lastKey += arr[i];
        }
        return docs.get(docId);
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

    public Integer getDocFreq(String word){
        if(docFreq.containsKey(word)) return docFreq.get(word);
        docFreq.put(word, getPostingList(word).docFreq());
        return docFreq.get(word);
    }

    public List<Document> getAllDocuments(){
        return this.docs;
    }
}
