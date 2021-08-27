package operation.layers;

import operation.Calculation;
import operation.Module;
import operation.MultiVector;
import operation.Node;

public class Linear extends Module {
    public Linear(Node x, int hidden_size, boolean bias, int ac_fn){
        this.Name = "Linear-" + this.id;
        System.out.println(x._tensor._shape.toString());
        int last_dim = x._tensor._shape.get(x._tensor._dims - 1);
        int[] dimw = {last_dim, hidden_size};
        Node w = new Node(new MultiVector(dimw, Calculation.SET_NORMALIZE));
//        this.addLeaf(w);

        Node mmNode = addFromLeftInput(x, w, Calculation.MATMUL);
        Node output = null;
        if(bias){
            int[] dimb = {hidden_size};
            Node b = new Node(new MultiVector(dimb, Calculation.SET_ALL_ZEROS));
//            this.addLeaf(b);
            Node addNode = addFrom(mmNode, b, Calculation.ADD);
            output = addFrom(addNode, Calculation.SIGMOID);
        }
        else output = addFrom(mmNode, Calculation.SIGMOID);
        assert this._output == output;
        this._tensor = MultiVector.MultiVector_like(this._output._tensor, Calculation.SET_EMPTY_DATA);
    }
}
