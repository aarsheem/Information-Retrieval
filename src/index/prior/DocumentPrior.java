package index.prior;

import index.Document;

import java.util.List;

public interface DocumentPrior {
    public List<Double> score(List<Document> docs);
}
