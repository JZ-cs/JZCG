package operation.optimizer;


import operation.Node;
import java.io.Serializable;

public interface Optimizer extends Serializable {
    void updateWithGrads(Node nd);
}
