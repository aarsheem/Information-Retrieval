package hw;

import cluster.*;
import index.Document;
import index.Index;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static hw.HW1.buildIndex;

public class HW5 {
    private static Double minThreshold = 0.05, maxThreshold = 0.99, deltaThreshold = 0.05;

    private static void printResults(cluster.Query query, Class<? extends Cluster> clusterClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, FileNotFoundException, UnsupportedEncodingException {
        for(Double threshold = minThreshold; threshold <= maxThreshold; threshold += deltaThreshold){
            String thresholdStr = String.format("%3.2f",threshold);
            String filename = "data/HW5/cluster-" + thresholdStr + ".out";
            PrintWriter fileWriter = new PrintWriter(filename, "UTF-8");
            List<Cluster> clusters = query.documentAtATime(clusterClass, threshold);
            List<String> results = getClusterOut(clusters, thresholdStr, Boolean.FALSE);
            results.forEach(fileWriter::println);
            fileWriter.close();
        }
    }

    private static List<String> getClusterOut(List<Cluster> clusters, String thresholdStr, Boolean isLatex){
        List<String> results = new ArrayList<>();
        Map<Integer, Integer> sizes = new TreeMap<>();
        for(Cluster c : clusters){
            String clusterId = String.format("%-5s",c.getId());
            List<Document> docs = c.getDocuments();
            sizes.putIfAbsent(docs.size(), 0); sizes.put(docs.size(), sizes.get(docs.size()) + 1);
            for(Document doc : docs) results.add(clusterId + String.format("%-40s", doc.getSceneId()));
        }
        if(isLatex) {
            System.out.println("\\begin{tabular}[t]{c|" + "c".repeat(sizes.size()) + "}");
            System.out.print("{\\bf size}");
            for (Integer size : sizes.keySet()) System.out.print(" & " + size);
            System.out.println("\\\\");
            System.out.print("{\\bf frequency}");
            for (Integer size : sizes.keySet()) System.out.print(" & " + sizes.get(size));
            System.out.println();
            System.out.println("\\end{tabular}");
            System.out.println("\\caption{Threshold: " + thresholdStr + ", Total Clusters: " + clusters.size() + "}");
        }
        else{
            System.out.println("Threshold: " + thresholdStr + ", Total Clusters: " + clusters.size());
            System.out.println(sizes.toString());
            //System.out.print(clusters.size() + ", ");
        }
        return  results;
    }

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, FileNotFoundException, UnsupportedEncodingException {
        boolean isCompress = Boolean.parseBoolean(args[1]);
        String filename = "data/" + args[2];
        //In case you want to print only one
        if(args.length > 3) {
            minThreshold = Double.parseDouble(args[3]); maxThreshold = minThreshold;
        }
        Index index = new Index();
        Class clusterClass = null;
        switch(args[0]) {
            case "build":
                buildIndex(filename, isCompress);
                return;
            case "mean":
                clusterClass = MeanCluster.class;
                break;
            case "min":
                clusterClass = MinCluster.class;
                break;
            case "max":
                clusterClass = MaxCluster.class;
                break;
            case "avg":
                clusterClass = AvgCluster.class;
                break;
        }
        index.load(isCompress);
        cluster.Query query = new cluster.Query(index);
        printResults(query, clusterClass);
    }
}
