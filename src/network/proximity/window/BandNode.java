package network.proximity.window;

import network.proximity.ProximityNode;
import retrieval.model.Model;

import java.util.List;

public class BandNode extends UnorderNode{
    public BandNode(Model model, List<? extends ProximityNode> children){
        super(model, children, Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return "band";
    }

}
