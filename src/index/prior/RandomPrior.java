package index.prior;

import index.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPrior implements DocumentPrior{

    @Override
    public List<Double> score(List<Document> docs) {
        Random rand = new Random();
        List<Double> p = new ArrayList<>();
        Double sum = 0.0;
        for(int i = 0; i < docs.size(); i++){
            Double r = rand.nextDouble();
            p.add(r); sum += r;
        }
        for(int i = 0; i < p.size(); i++) p.set(i, p.get(i)/sum);
        return p;
    }
}
