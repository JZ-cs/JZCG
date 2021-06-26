package Foundation;

import java.util.ArrayList;

public class OpNode extends Node{
    public boolean broadcastFlag;
    public int opSign;
    public OpNode(Node ch1, Node ch2, int opSign){
        super(ch1, ch2);
        this.broadcastFlag = needBroadcast();
        this.opSign = opSign;
        this.Name = getName();
        //initialize the _tensor here is OK, however not here is to avoid the switch thing,
        //may alter to it later.
        this._tensor = new MultiVector(getOpResultShape(ch1._tensor, ch2._tensor));
        this._grad = MultiVector.MultiVector_like(this._tensor);
    }
    public String getName(){
        String prefix = null;
        switch (this.opSign){
            case Calculation.ADD:{
                prefix = "Add-";
                break;
            }
            case Calculation.SUB:{
                prefix = "Sub-";
                break;
            }
            case Calculation.MUL:{
                prefix = "Mul-";
                break;
            }
            case Calculation.DIV:{
                prefix = "Div-";
                break;
            }
        }
        return prefix + this.id;
    }
    public boolean needBroadcast(){
        if(this.pred[0]._tensor._shape.size() != this.pred[1]._tensor._shape.size()) return true;
        int dims = this.pred[0]._tensor._dims;
        for(int i = 0; i < dims; i++){
            if(this.pred[0]._tensor._shape.get(i) != this.pred[1]._tensor._shape.get(i)) return true;
        }
        return false;
    }

    @Override
    public void transForward() {
        super.transForward();
        switch (this.opSign){
            case Calculation.ADD:{
                this._tensor = MultiVector.add(pred[0]._tensor, pred[1]._tensor);
                break;
            }
            case Calculation.SUB:{
                this._tensor = MultiVector.sub(pred[0]._tensor, pred[1]._tensor);
                break;
            }
            case Calculation.MUL:{
                this._tensor = MultiVector.mul(pred[0]._tensor, pred[1]._tensor);
                break;
            }
            case Calculation.DIV:{
                this._tensor = MultiVector.div(pred[0]._tensor, pred[1]._tensor);
                break;
            }
        }
    }

    @Override
    public void transBack() {
        super.transBack();
        MultiVector tgrad0 = null, tgrad1 = null;
        switch (this.opSign){
            case Calculation.ADD:{
                //z = a + b
                tgrad0 = MultiVector.mul(this._grad, 1.0);
                tgrad1 = MultiVector.mul(this._grad, 1.0);
                break;
            }
            case Calculation.SUB:{
                //z = a - b
                tgrad0 = MultiVector.mul(this._grad, 1.0);
                tgrad1 = MultiVector.mul(this._grad, -1.0);
                break;
            }
            case Calculation.MUL:{
                //z = a * b
                tgrad0 = MultiVector.mul(this._grad, this.pred[1]._tensor);
                tgrad1 = MultiVector.mul(this._grad, this.pred[0]._tensor);
                break;
            }
            case Calculation.DIV:{
                //z = a / b
                tgrad0 = MultiVector.mul(this._grad, MultiVector.reciprocal(this.pred[1]._tensor));

                MultiVector negSquareOfPred1 = MultiVector.mul(this.pred[1]._tensor, this.pred[1]._tensor);
                negSquareOfPred1.inv_inplace();
                tgrad1 = MultiVector.mul(this._grad, MultiVector.div(this.pred[0]._tensor, negSquareOfPred1));
                break;
            }
        }

        //reshape the first grad(grad of pred[0])
        ArrayList<Integer> list_sum_axes_0 = new ArrayList<>();
        ArrayList<Integer> list_squeeze_axes_0 = new ArrayList<>();
        ArrayList<Integer> broadcastInfo_0 = MultiVector.getOpBroadcastInfo(pred[0]._tensor, this._tensor);
        for(int i = 0; i < broadcastInfo_0.size(); i++)
        {
            if(broadcastInfo_0.get(i) == 1) list_sum_axes_0.add(i);
            else if(broadcastInfo_0.get(i) == -1)
            {
                list_sum_axes_0.add(i);
                list_squeeze_axes_0.add(i);
            }
        }
        if(list_sum_axes_0.size() > 0)
        {
            int[] sum_axes_0 = new int[list_sum_axes_0.size()];
            for(int i = 0; i < list_sum_axes_0.size(); i++) sum_axes_0[i] = list_sum_axes_0.get(i);
            tgrad0 = MultiVector.sum(tgrad0, true, sum_axes_0);
        }
        if(list_squeeze_axes_0.size() > 0)
        {
            int[] squeeze_axes_0 = new int[list_squeeze_axes_0.size()];
            for(int i = 0; i < list_squeeze_axes_0.size(); i++) squeeze_axes_0[i] = list_squeeze_axes_0.get(i);
            tgrad0 = MultiVector.squeeze(tgrad0, squeeze_axes_0);
        }
        this.pred[0]._grad.add(tgrad0);

        //reshape the second grad(grad of pred[1])
        ArrayList<Integer> list_sum_axes_1 = new ArrayList<>();
        ArrayList<Integer> list_squeeze_axes_1 = new ArrayList<>();
        ArrayList<Integer> broadcastInfo_1 = MultiVector.getOpBroadcastInfo(pred[1]._tensor, this._tensor);
        for(int i = 0; i < broadcastInfo_1.size(); i++)
        {
            if(broadcastInfo_1.get(i) == 1) list_sum_axes_1.add(i);
            else if(broadcastInfo_1.get(i) == -1)
            {
                list_sum_axes_1.add(i);
                list_squeeze_axes_1.add(i);
            }
        }
        if(list_sum_axes_1.size() > 0)
        {
            int[] sum_axes_1 = new int[list_sum_axes_1.size()];
            for(int i = 0; i < list_sum_axes_1.size(); i++) sum_axes_1[i] = list_sum_axes_1.get(i);
            tgrad1 = MultiVector.sum(tgrad1, true, sum_axes_1);
        }
        if(list_squeeze_axes_1.size() > 0)
        {
            int[] squeeze_axes_1 = new int[list_squeeze_axes_1.size()];
            for(int i = 0; i < list_squeeze_axes_1.size(); i++) squeeze_axes_1[i] = list_squeeze_axes_1.get(i);
            tgrad1 = MultiVector.squeeze(tgrad1, squeeze_axes_1);
        }
        this.pred[1]._grad.add(tgrad1);

        //sub outdegrees by 1
        this.pred[0].outd--;
        this.pred[1].outd--;
    }

    private ArrayList<Integer> getOpResultShape(MultiVector mv1, MultiVector mv2){
        ArrayList<Integer> res_shape = null;
        try{
            res_shape = MultiVector._getOpResultShape(mv1, mv2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res_shape;
    }
}
