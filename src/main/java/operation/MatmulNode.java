package operation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MatmulNode extends Node
{
    {
        this.Name = "MatmulNode" + "-" + this.id;
    }
    public MatmulNode(Node ch1, Node ch2) //mm tensor and tensor
    {
        super(ch1, ch2);
        this._tensor = MultiVector.MultiVector_like(getMatmulResultShape(ch1._tensor, ch2._tensor), Calculation.SET_EMPTY_DATA);
        this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_EMPTY_DATA);
    }

    @Override
    public void transForward() {
        super.transForward();
        if(this._tensor._data == null){
            this._tensor = MultiVector.matmul(this.pred[0]._tensor, this.pred[1]._tensor);
            this._grad = MultiVector.MultiVector_like(this._tensor, Calculation.SET_ALL_ZEROS);
        }
        else MultiVector.matmul(this.pred[0]._tensor, this.pred[1]._tensor, this._tensor);
    }

    @Override
    public void transBack() {
        super.transBack();

        //transpose the last two axes!
        int[] transpose_axes_0 = new int[this.pred[0]._tensor._dims];
        for(int i = 0; i < this.pred[0]._tensor._dims - 2; i++) transpose_axes_0[i] = i;
        transpose_axes_0[this.pred[0]._tensor._dims - 2] = this.pred[0]._tensor._dims - 1;
        transpose_axes_0[this.pred[0]._tensor._dims - 1] = this.pred[0]._tensor._dims - 2;

        int[] transpose_axes_1 = new int[this.pred[1]._tensor._dims];
        for(int i = 0; i < this.pred[1]._tensor._dims - 2; i++) transpose_axes_1[i] = i;
        transpose_axes_1[this.pred[1]._tensor._dims - 2] = this.pred[1]._tensor._dims - 1;
        transpose_axes_1[this.pred[1]._tensor._dims - 1] = this.pred[1]._tensor._dims - 2;

        //Calculation the grad of pred[0], the first one!
        MultiVector tgrad0 = MultiVector.matmul(this._grad, MultiVector.transpose(this.pred[1]._tensor, transpose_axes_1));
        ArrayList<Integer> list_sum_axes_0 = new ArrayList<>();
        ArrayList<Integer> list_squeeze_axes_0 = new ArrayList<>();
        ArrayList<Integer> broadcastInfo_0 = MultiVector.getMatmulBroadcastInfo(pred[0]._tensor, this._tensor);
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

        //Calculation the grad of pred[1], the second one!

        MultiVector tgrad1 = MultiVector.matmul(MultiVector.transpose(this.pred[0]._tensor, transpose_axes_0), this._grad);
        ArrayList<Integer> list_sum_axes_1 = new ArrayList<>();
        ArrayList<Integer> list_squeeze_axes_1 = new ArrayList<>();
        ArrayList<Integer> broadcastInfo_1 = MultiVector.getMatmulBroadcastInfo(pred[1]._tensor, this._tensor);
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

    public ArrayList<Integer> getMatmulResultShape(MultiVector mv1, MultiVector mv2){
        ArrayList<Integer> res_shape = null;
        try {
            res_shape = MultiVector._getMatmulResultShape(mv1, mv2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res_shape;
    }
}
