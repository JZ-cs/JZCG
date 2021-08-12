package operation.functionNodes;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;

public class ExpNode extends Node {
    public ExpNode(Node ch1){
        super(ch1);
        this.Name = "ExpNode-" + this.id;

        this._tensor = MultiVector.MultiVector_like(ch1._tensor, Calculation.SET_EMPTY_DATA);
        this._grad = MultiVector.MultiVector_like(this._tensor);
    }

    @Override
    public void transForward() {
        super.transForward();
        if(this._tensor._data == null){
            this._tensor = MultiVector.exp(this.pred[0]._tensor);
            this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
        }
        else MultiVector.exp(this.pred[0]._tensor, this._tensor);
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector tgrad0 = MultiVector.mul(this._grad, this._tensor);
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
    }
}
