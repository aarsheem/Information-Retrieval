package retrieval;

import index.Index;
import index.PostingList;
import retrieval.model.Model;
import utility.DocOrder;


import java.util.*;

public class Query {

    protected Index index;

    public Query(Index index){
        this.index = index;
    }

    //returns map of query terms posting lists and their counts
    private Map<PostingList, Integer> getWords(String[] query){
        Map<PostingList, Integer> words = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for(String q : query){
            counts.merge(q, 1, Integer::sum);
        }
        for(Map.Entry<String, Integer> e : counts.entrySet()){
            String s = e.getKey(); Integer c = e.getValue();
            PostingList postings = index.getPostingList(s);
            if(postings != null) words.put(index.getPostingList(s), c);
        }
        return words;
    }

    public List<DocOrder> documentAtATime(Model model, String[] query){
        return documentAtATime(model, query, -1);
    }

    public List<DocOrder> documentAtATimeConj(Model model, String[] query){
        return documentAtATimeConj(model, query, -1);
    }

    public List<DocOrder> documentAtATime(Model model, String[] query, Integer k){
        Map<PostingList, Integer> words = getWords(query);
        PriorityQueue<DocOrder> docs = new PriorityQueue<>();
        for(int docId = 0; docId < index.getDocumentsCount(); docId++){
            Double score = 0.0;
            Boolean found = Boolean.FALSE;
            for(Map.Entry<PostingList, Integer> e : words.entrySet()){
                PostingList word = e.getKey(); Integer count = e.getValue();
                if(word.getDoc() != docId)
                    score += model.scoreNotFound(word, count, docId);
                else{
                    found = Boolean.TRUE;
                    score += model.score(word, count);
                    word.nextDoc();
                }
            }
            if(found) docs.add(new DocOrder(score, docId));
        }
        List<DocOrder> docIds = new ArrayList<>();
        Integer count = 0;
        while(!docs.isEmpty() && !count.equals(k)){
            docIds.add(docs.poll());
            count++;
        }
        return docIds;
    }

    public List<DocOrder> documentAtATimeConj(Model model, String[] query, Integer k){
        Map<PostingList, Integer> words = getWords(query);
        PriorityQueue<DocOrder> docs = new PriorityQueue<>();
        Integer currDocId = -1;
        boolean isEnd = false;
        do{
            Double score = 0.0;
            for(Map.Entry<PostingList, Integer> e : words.entrySet()){
                PostingList word = e.getKey();
                int doc = word.getDoc();
                currDocId = Math.max(currDocId, doc);
                isEnd = isEnd || word.isEnd();
            }
            for(Map.Entry<PostingList, Integer> e : words.entrySet()){
                PostingList word = e.getKey(); Integer count = e.getValue();
                word.skipToDoc(currDocId);
                if(word.getDoc().equals(currDocId)){
                    score += model.score(word, count);
                    word.nextDoc();
                }
                else {
                    currDocId = -1;
                    break;
                }
            }
            if(currDocId != -1) {
                docs.add(new DocOrder(score, currDocId));
            }
        } while(!isEnd);
        List<DocOrder> docIds = new ArrayList<>();
        Integer count = 0;
        while(!docs.isEmpty() && !count.equals(k)){
            docIds.add(docs.poll());
            count++;
        }
        return docIds;
    }
}
