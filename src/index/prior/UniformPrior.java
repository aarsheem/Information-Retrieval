package index.prior;

import index.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniformPrior implements DocumentPrior{
    @Override
    public List<Double> score(List<Document> docs) {
        return new ArrayList<>(Collections.nCopies(docs.size(), 1.0/docs.size()));
    }
}
