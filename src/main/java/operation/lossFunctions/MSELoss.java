package operation.lossFunctions;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;

public class MSELoss extends Node implements Loss {
    public double loss;

    public int batchNum = 1;
    public MSELoss(Node predictionNode, Node labelNode){
        super(predictionNode, labelNode);
        this._tensor = new MultiVector(0.0);
        this._grad = new MultiVector(0.0);
        this.Name = "MSELoss-" + this.id;
        int batchNum = this.pred[1]._tensor._shape.get(0);
    }

    @Override
    public void transForward() {
        super.transForward();
        MultiVector subres = MultiVector.sub(this.pred[0]._tensor, this.pred[1]._tensor);
        MultiVector subSquire = MultiVector.mul(subres, subres);
        this._tensor = MultiVector.sum(subSquire, true);
        this._tensor.div(this.batchNum);
        this.loss = this._tensor._data[0];
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector loss2subSquireGrad = MultiVector.MultiVector_like(this.pred[0]._tensor, Calculation.SET_ALL_ONES);
        MultiVector subSquireGrad = MultiVector.sub(this.pred[0]._tensor, this.pred[1]._tensor);
        subSquireGrad.mul(2.0 / this.batchNum);
        MultiVector tgrad0 = MultiVector.mul(loss2subSquireGrad, subSquireGrad);
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
        this.pred[1].outd--;
    }

    @Override
    public double getLoss() {
        return this.loss;
    }
}
