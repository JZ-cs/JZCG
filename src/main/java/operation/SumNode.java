package operation;

public class SumNode extends Node{
    public boolean retain_shape;
    public int[] axes;
    public SumNode(Node ch1, boolean retain_shape, int...axes){
        super(ch1);
        this.Name = "SumNode-" + this.id;
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        for(int i = 0; i < axes.length; i++){
            this.axes[i] = axes[i];
        }
        this._tensor = MultiVector.sum(ch1._tensor, retain_shape, axes);
        this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
    }

    public SumNode(Node ch1){
        super(ch1);
        this.Name = "SumNode-" + this.id;
    }
    public void realInit(boolean retain_shape, int...axes){
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        for(int i = 0; i < axes.length; i++){
            this.axes[i] = axes[i];
        }
        this._tensor = MultiVector.sum(this.pred[0]._tensor, retain_shape, axes);
        this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
    }

    public void setRetain_shape(boolean retain_shape_flag){
        this.retain_shape = retain_shape_flag;
    }
    public boolean getRetain_shape(){
        return this.retain_shape;
    }
    public void setAxes(int[] _axes){
        for(int i = 0; i < axes.length; i++){
            this.axes[i] = _axes[i];
        }
    }
    public int[] getAxes(){
        return this.axes;
    }

    @Override
    public void transForward() {
        super.transForward();
        this._tensor = MultiVector.sum(pred[0]._tensor, this.retain_shape, this.axes);
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector tgrad0_ones = MultiVector.MultiVector_like(this.pred[0]._tensor, Calculation.SET_ALL_ONES);
        MultiVector tgrad0 = MultiVector.mul(this._grad, tgrad0_ones);
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
    }
}
