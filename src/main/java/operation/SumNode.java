package operation;

public class SumNode extends Node{
    public boolean retain_shape;
    public int[] axes;
    public SumNode(Node ch1, boolean retain_shape, int...axes){
        super(ch1);
        this.Name = "SumNode-" + this.id;
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        System.arraycopy(axes, 0, this.axes, 0, axes.length);
//        this._tensor = MultiVector.sum(ch1._tensor, retain_shape, axes);
//        this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
    }

    public SumNode(Node ch1){
        super(ch1);
        this.Name = "SumNode-" + this.id;
    }
    public void realInit(boolean retain_shape, int...axes){
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        System.arraycopy(axes, 0, this.axes, 0, axes.length);
//        this._tensor = MultiVector.sum(this.pred[0]._tensor, retain_shape, axes);
//        this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
    }

    public void setRetain_shape(boolean retain_shape_flag){
        this.retain_shape = retain_shape_flag;
    }
    public boolean getRetain_shape(){
        return this.retain_shape;
    }
    public void setAxes(int[] _axes){
        System.arraycopy(_axes, 0, this.axes, 0, axes.length);
    }
    public int[] getAxes(){
        return this.axes;
    }

    @Override
    public void transForward() {
        super.transForward();
        if(this._tensor == null){
            this._tensor = MultiVector.sum(pred[0]._tensor, this.retain_shape, this.axes);
            this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
        }
        else MultiVector.sum(this._tensor, pred[0]._tensor, this.retain_shape, this.axes);
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
