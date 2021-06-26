package Foundation.lossFunctions;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class MSELoss extends Node {
    public double loss;
    public MultiVector predictions;
    public MultiVector labels;
    public int batchNum = 1;
    public MSELoss(Node predictionNode, Node labelNode){
        super(predictionNode, labelNode);
        this._tensor = new MultiVector(0.0);
        this._grad = new MultiVector(0.0);
        this.predictions = predictionNode._tensor;
        this.labels = labelNode._tensor;
        this.Name = "MSELoss-" + this.id;
        int batchNum = this.pred[0]._tensor._shape.get(0);
    }

    @Override
    public void transForward() {
        super.transForward();
        MultiVector subres = MultiVector.sub(this.predictions, this.labels);
        MultiVector subSquire = MultiVector.mul(subres, subres);
        this._tensor = MultiVector.sum(subSquire, true);
        this._tensor.div(batchNum);
        this.loss = this._tensor._data[0];
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector loss2subSquireGrad = MultiVector.MultiVector_like(this.predictions, Calculation.SET_ALL_ONES);
        loss2subSquireGrad.div(this.batchNum);
        MultiVector subSquireGrad = MultiVector.sub(this.predictions, this.labels);
        subSquireGrad.mul(2.0);
        MultiVector tgrad0 = MultiVector.mul(loss2subSquireGrad, subSquireGrad);
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
        this.pred[1].outd--;
    }
}
