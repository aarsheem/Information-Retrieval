package index;


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
    private List<Document> docs;
    private Compression compress;

    public IndexBuilder(){
        docs = new ArrayList<>();
        invertedIndex = new HashMap<>();
    }

    public void buildIndex(String filename, boolean isCompress) throws IOException {
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
        parseFile(filename);
        saveInvertedIndex("data/" + indexName,"data/" + lookupName);
        saveDocuments("data/documents.txt");
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
            for(int j = 0; j < words.length; j++){
                String word = words[j];
                invertedIndex.putIfAbsent(word, new PostingList());
                invertedIndex.get(word).add(docId, j);
            }
            docs.add(new Document(sceneString, playString, words.length));
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
        for (Map.Entry<String, PostingList> entry : invertedIndex.entrySet()){
            String word = entry.getKey();
            long lastOffset = offset;
            PostingList postings = entry.getValue();
            ByteBuffer buffer = compress.encode(postings.toArray(compress.isCompress()));
            Integer wordCount = buffer.position() + buffer.remaining();
            indexWriter.write(buffer.array());
            offset = indexWriter.getFilePointer();
            lookupWriter.println(word + " " + lastOffset + " " + wordCount);
        }
        indexWriter.close();
        lookupWriter.close();
    }

    private void saveDocuments(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter fileWriter = new PrintWriter(filename, "UTF-8");
        for (Document d : docs){
            fileWriter.println(d.getSceneId() + " " + d.getPlayId() + " " + d.getSize());
        }
        fileWriter.close();
    }


}
