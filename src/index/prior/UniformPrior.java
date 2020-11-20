package index.prior;

import index.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniformPrior implements DocumentPrior{
    @Override
    public List<Double> score(Integer totalDocs) {
        return new ArrayList<>(Collections.nCopies(totalDocs, -Math.log(totalDocs)));
    }
}
