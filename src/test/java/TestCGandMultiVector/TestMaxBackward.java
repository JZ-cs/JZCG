package TestCGandMultiVector;

import operation.Calculation;
import operation.MaxNode;
import operation.MultiVector;
import operation.Node;

public class TestMaxBackward {
    public static void main(String[] args) {
        test1();
    }

    public static void test1(){
        int[] dim1 = {2,3,2};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_RANDOM_UINT16);
        mv1.print();
        Node n1 = new Node(mv1);
        MaxNode maxNode = new MaxNode(n1, false, 1);
        maxNode.transForward();
        maxNode._grad.set_ones();
        maxNode.transBack();
        n1._grad.print();
    }
}
