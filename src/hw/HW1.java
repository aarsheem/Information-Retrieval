package hw;

import index.Index;
import index.IndexBuilder;
import index.PostingList;
import retrieval.Dice;
import retrieval.Query;
import retrieval.model.Count;
import retrieval.model.Model;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HW1 {
    protected static void buildIndex(String filename, boolean isCompress){
        IndexBuilder builder = new IndexBuilder();
        try {
            builder.buildIndex(filename, isCompress);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void compareIndex(){
        Index index = new Index();
        boolean areSame = true;
        String[] vocab;
        Map<String, Integer> termFreq = new HashMap<>();
        try {
            index.load(false);
            vocab = index.getVocab();
            for(String word : vocab)
                termFreq.put(word, index.getPostingList(word).termFreq());

            index.load(true);
            assert(index.getVocabSize() == vocab.length);
            vocab = index.getVocab();
            for(String word: vocab){
                if(!index.getPostingList(word).termFreq().equals(termFreq.get(word))){
                    areSame = false;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(areSame) System.out.println("Words Match!!!");
    }

    private static void randomSeven(boolean isCompress){
        Integer iterations = 100;
        Index index = new Index();
        PrintWriter fileWriter = null;
        PrintWriter fileWriter2 = null;
        try {
            index.load(isCompress);
            fileWriter = new PrintWriter("data/HW1/seven_info.txt", "UTF-8");
            fileWriter2 = new PrintWriter("data/HW1/seven.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] vocab = index.getVocab();
        for(int i = 0; i < iterations; i++){
            for(int j = 0; j < 7; j++){
                int randomNum = ThreadLocalRandom.current().nextInt(0, index.getVocabSize());
                String word = vocab[randomNum];
                PostingList postings = index.getPostingList(word);
                Integer termFreq = postings.termFreq();
                Integer docFreq = postings.docFreq();
                fileWriter.println(word + " " + termFreq + " " + docFreq);
                fileWriter2.print(word + " ");
            }
            fileWriter2.println();
        }
        fileWriter.close();
        fileWriter2.close();
    }

    private static void diceScore(boolean isCompress){
        Index index = new Index();
        Dice dice = new Dice(index);
        File file = new File("data/HW1/seven.txt");
        Scanner fileReader = null;
        PrintWriter fileWriter = null;
        try {
            index.load(isCompress);
            fileReader = new Scanner(file);
            fileWriter = new PrintWriter("data/HW1/fourteen.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        while(fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            String[] words = line.split("\\s+");
            for(int i = 0; i < 7; i++) {
                String secondWord = dice.maxDice(words[i]);
                fileWriter.print(words[i] + " " + secondWord + " ");
            }
            fileWriter.println();
        }
        fileReader.close();
        fileWriter.close();
    }

    private static void timingExperiment(){
        File file = new File("data/HW1/fourteen.txt");
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> seven = new ArrayList<>();
        List<String[]> fourteen = new ArrayList<>();
        while(fileReader.hasNextLine()){
            String[] fourteenStrings = fileReader.nextLine().split("\\s+");
            String[] sevenStrings = Arrays.copyOf(fourteenStrings, 7);
            seven.add(sevenStrings);
            fourteen.add(fourteenStrings);
        }
        fileReader.close();

        Index index = new Index();
        Index compressIndex = new Index();
        try {
            index.load(false);
            compressIndex.load(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Query query = new Query(index);
        Model model = new Count();
        Query compressQuery = new Query(compressIndex);

        long startTime = System.currentTimeMillis();
        for(String[] words : seven){
            query.documentAtATime(model, words);
        }
        long sevenTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        for(String[] words : fourteen){
            query.documentAtATime(model, words);
        }
        long fourteenTime = System.currentTimeMillis() - startTime;
        System.out.println("Uncompressed Time(ms): ");
        System.out.println("Seven Time: " + sevenTime + ";  Fourteen Time: " + fourteenTime);

        startTime = System.currentTimeMillis();
        for(String[] words : seven){
            compressQuery.documentAtATime(model, words);
        }
        sevenTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        for(String[] words : fourteen){
            compressQuery.documentAtATime(model, words);
        }
        fourteenTime = System.currentTimeMillis() - startTime;

        System.out.println("Compressed Time(ms): ");
        System.out.println("Seven Time: " + sevenTime + ";  Fourteen Time: " + fourteenTime);
    }

    public static void main(String[] args) {
        switch(args[0]) {
            case "build":
                boolean isCompress = Boolean.parseBoolean(args[1]);
                String filename = "data/" + args[2];
                buildIndex(filename, isCompress);
                break;
            case "1":
                compareIndex();
                break;
            case "2":
                isCompress = Boolean.parseBoolean(args[1]);
                randomSeven(isCompress);
                break;
            case "3":
                isCompress = Boolean.parseBoolean(args[1]);
                diceScore(isCompress);
                break;
            case "4":
                timingExperiment();
                break;
        }
    }
}
