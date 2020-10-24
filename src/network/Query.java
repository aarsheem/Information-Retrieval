package network;

import utility.DocOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Query {
    public static List<DocOrder> documentAtATime(QueryNode node){
        return documentAtATime( node, -1);
    }

    public static List<DocOrder> documentAtATime(QueryNode node, Integer k){
        PriorityQueue<DocOrder> docs = new PriorityQueue<>();
        while(node.hasMore()){
            Integer docId = node.nextCandidate();
            node.skipTo(docId);
            Double score = node.score(docId);
            if(score != null) docs.add(new DocOrder(score, docId));
            node.skipTo(docId+1);
        }
        List<DocOrder> docIds = new ArrayList<>();
        Integer count = 0;
        while(!docs.isEmpty() && !count.equals(k)){
            docIds.add(docs.poll());
            count++;
        }
        return docIds;
    }
}
