package operation.functionNodes;

import operation.MultiVector;
import operation.Node;

public class ExpNode extends Node {
    public ExpNode(Node ch1){
        super(ch1);
        this.Name = "ExpNode-" + this.id;

        this._tensor = MultiVector.exp(ch1._tensor);
        this._grad = MultiVector.MultiVector_like(this._tensor);
    }

    @Override
    public void transForward() {
        super.transForward();
        this._tensor = MultiVector.exp(this.pred[0]._tensor);
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector tgrad0 = MultiVector.mul(this._grad, this._tensor);
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
    }
}
