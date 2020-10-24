package network.proximity.window;

import network.proximity.ProximityNode;

import java.util.List;

public class BandNode extends UnorderNode{
    public BandNode(retrieval.Query model, List<? extends ProximityNode> children){
        super(model, children, Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return "band";
    }

}
