package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;
import operation.functionNodes.SigmoidNode;

public class testSigmoidBackward {
    public static void main(String[] args) {
        test1();
    }
    public static void test1(){
        int[] dim1 = {2,1,4};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_INCREASE);
        Node n1 = new Node(mv1);
        SigmoidNode sigmoidNode = new SigmoidNode(n1);
        sigmoidNode.transForward();
        sigmoidNode._tensor.print();
        sigmoidNode._grad.set_ones();
        sigmoidNode.transBack();
        n1._grad.print();
    }
}
