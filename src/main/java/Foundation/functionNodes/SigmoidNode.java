package Foundation.functionNodes;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class SigmoidNode extends Node {
    public SigmoidNode(Node ch1){
        super(ch1);
        this.Name = "SigmoidNode-" + this.id;

        this._tensor = MultiVector.sigmoid(ch1._tensor);
        this._grad = MultiVector.MultiVector_like(this._tensor);
    }

    @Override
    public void transForward() {
        super.transForward();
        this._tensor = MultiVector.sigmoid(this.pred[0]._tensor);
    }

    @Override
    public void transBack() {
        super.transBack();

        //use f'(x) = f(x) * (1 - f(x))
        MultiVector grad_Equal2selfTensor = MultiVector.MultiVector_like(this._tensor);
        grad_Equal2selfTensor.set_with(this._grad);

        MultiVector secondPart = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ONES);
        secondPart.sub(grad_Equal2selfTensor);
        MultiVector tgrad0 = MultiVector.mul(grad_Equal2selfTensor, secondPart);

        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
    }
}
