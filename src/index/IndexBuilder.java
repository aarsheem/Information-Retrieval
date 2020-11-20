package index;


import index.prior.DocumentPrior;
import index.prior.RandomPrior;
import index.prior.UniformPrior;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.Compression;
import utility.EmptyCompression;
import utility.VbyteCompression;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;


public class IndexBuilder {
    public Map<String, PostingList> invertedIndex;
    public List<Document> docs;
    private Map<String, Integer> orderedWords;
    private Compression compress;

    public IndexBuilder(){
        docs = new ArrayList<>();
        invertedIndex = new HashMap<>();
        orderedWords = new HashMap<>();
    }

    public void buildIndex(String filename, boolean isCompress) throws IOException {
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
        parseFile(filename);
        saveInvertedIndex("data/" + indexName,"data/" + lookupName);
        saveDocuments("data/" + docName, "data/document_info.txt");
        savePriors(new RandomPrior(), "data/HW6/random.prior");
        savePriors(new UniformPrior(), "data/HW6/uniform.prior");
    }

    //void parseFile(filename: String)
    //Takes the filename and generates a map of posting lists
    private void parseFile(String filename) throws IOException {
        Integer total = 0;
        String jsonString = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("corpus");
        for(int i = 0; i < arr.length(); i++){
            JSONObject scene = arr.getJSONObject(i);
            String sceneString = scene.getString("sceneId");
            String playString = scene.getString("playId");
            int docId = i;
            String text = scene.getString("text");
            String words[] = text.split("\\s+");
            Document doc = new Document(sceneString, playString);
            for(int j = 0; j < words.length; j++){
                String word = words[j];
                invertedIndex.putIfAbsent(word, new PostingList());
                invertedIndex.get(word).add(docId, j);
                doc.add(word);
            }
            docs.add(doc);
            total += words.length;
        }
        Document minSize = Collections.min(docs);
        Document maxSize = Collections.max(docs);
        System.out.println("Average Size: " + total/docs.size());
        System.out.println("Min Scene Size: " + minSize.getSize() + ", Id: " + minSize.getSceneId());
        System.out.println("Max Scene Size: " + maxSize.getSize() + ", Id: " + maxSize.getSceneId());
    }

    private void saveInvertedIndex(String indexName, String lookupName) throws IOException {
        RandomAccessFile indexWriter = new RandomAccessFile(indexName, "rw");
        PrintWriter lookupWriter = new PrintWriter(lookupName, "UTF-8");
        long offset = 0;
        int count = 0;
        for (Map.Entry<String, PostingList> entry : invertedIndex.entrySet()){
            String word = entry.getKey();
            long lastOffset = offset;
            PostingList postings = entry.getValue();
            ByteBuffer buffer = compress.encode(postings.toArray(compress.isCompress()));
            Integer wordCount = buffer.position() + buffer.remaining();
            indexWriter.write(buffer.array());
            offset = indexWriter.getFilePointer();
            lookupWriter.println(word + " " + lastOffset + " " + wordCount);
            orderedWords.put(word, count);
            count++;
        }
        indexWriter.close();
        lookupWriter.close();
    }

    //call after saveInvertedIndex so we have ordered words
    private void saveDocuments(String docName, String docInfoName) throws IOException {
        RandomAccessFile docWriter = new RandomAccessFile(docName, "rw");
        PrintWriter docInfoWriter = new PrintWriter(docInfoName, "UTF-8");
        long offset = 0;
        Random rand = new Random();
        for (Document doc : docs){
            long lastOffset = offset;
            List<Integer> entries = docFreqToList(doc);
            ByteBuffer buffer = compress.encode(entries.toArray(new Integer[0]));
            Integer wordCount = buffer.position() + buffer.remaining();
            docWriter.write(buffer.array());
            offset = docWriter.getFilePointer();
            docInfoWriter.println(doc.getSceneId() + " " + doc.getPlayId() + " " + doc.getSize() + " " + lastOffset + " " + wordCount);
        }
        docWriter.close();
        docInfoWriter.close();
    }

    private List<Integer> docFreqToList(Document doc){
        Map<Integer, Integer> freq = new TreeMap<>(); //keeps the keys sorted
        for(Map.Entry<String, Integer> entry : doc.getFreq().entrySet()){
            freq.put(orderedWords.get(entry.getKey()), entry.getValue());
        }
        List<Integer> result = new ArrayList<>();
        Integer lastKey = 0;
        for(Map.Entry<Integer, Integer> entry : freq.entrySet()){
            result.add(entry.getKey()-lastKey);
            result.add(entry.getValue());
            if(compress.isCompress()) lastKey = entry.getKey(); //save diff from last
        }
        return result;
    }

    private void savePriors(DocumentPrior prior, String priorName) throws IOException {
        List<Double> priorScore = prior.score(docs);
        RandomAccessFile priorWriter = new RandomAccessFile(priorName, "rw");
        for(Double p : priorScore) priorWriter.writeDouble(p);
        priorWriter.close();
    }
}
