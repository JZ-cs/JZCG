package operation;

import java.util.HashMap;
import java.util.Map;

public class MaxNode extends Node{
    public boolean retain_shape;
    public int[] axes;
    HashMap<Integer, Integer> markers;//marks the chosen max value index in _data.
    public MaxNode(Node ch1, boolean retain_shape, int...axes){
        super(ch1);
        this.Name = "MaxNode-" + this.id;
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        System.arraycopy(axes, 0, this.axes, 0, axes.length);
        this.markers = new HashMap<>();
        this._tensor = MultiVector.sum(ch1._tensor, retain_shape, axes);
        this._grad = MultiVector.MultiVector_like(this._tensor);
    }
    public MaxNode(Node ch1){
        super(ch1);
        this.Name = "MaxNode-" + this.id;
    }
    public void realInit(boolean retain_shape, int...axes){
        this.retain_shape = retain_shape;
        this.axes = new int[axes.length];
        System.arraycopy(axes, 0, this.axes, 0, axes.length);
        this.markers = new HashMap<>();
        this._tensor = MultiVector.sum(this.pred[0]._tensor, retain_shape, axes);
        this._grad = MultiVector.MultiVector_like(this._tensor);
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
        markers.clear();
        this._tensor = MultiVector.max(this.pred[0]._tensor, markers, this.retain_shape, this.axes);
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector tgrad0 = MultiVector.MultiVector_like(this.pred[0]._tensor);
        for(Map.Entry<Integer, Integer> entry : markers.entrySet()){
            //(1.0 * ) can be removed, but to indicate how it operates, this remains!
            tgrad0._data[entry.getValue()] = 1.0 * this._grad._data[entry.getKey()];
        }
        markers.clear();
        this.pred[0]._grad.add(tgrad0);
        this.pred[0].outd--;
    }
}
