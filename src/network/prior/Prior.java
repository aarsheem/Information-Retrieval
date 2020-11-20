package network.prior;

import network.QueryNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Prior implements QueryNode {
    RandomAccessFile file;
    String name;

    public Prior(String filename) throws FileNotFoundException {
        this.file = new RandomAccessFile(filename, "r");
        String[] filePath = filename.split("\\/");
        this.name = filePath[filePath.length-1].split("\\.")[0];
    }

    @Override
    public void skipTo(Integer docId){}

    @Override
    public Double score(Integer docId) {
        Double ans = -1.0;
        try {
            this.file.seek(docId * 8);
            ans = this.file.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer nextCandidate(){
        return null;
    }

    @Override
    public Boolean hasMore(){
        return Boolean.FALSE;
    }
}
