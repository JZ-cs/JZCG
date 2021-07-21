package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;
import operation.OpNode;

public class TestOpBackward {
    public static void main(String[] args) {
        testDivBackWard();
    }

    public static void testAddBackWard(){
        int[] dim1 = {4,1,2};
        int[] dim2 = {2,1,3,2};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_INCREASE);
        MultiVector mv2 = new MultiVector(dim2, Calculation.SET_INCREASE);
        Node n1 = new Node(mv1);
        Node n2 = new Node(mv2);
        OpNode addNode = new OpNode(n1, n2, Calculation.ADD);
        addNode.transForward();
        addNode._grad.set_ones();
        addNode.transBack();
        n1._grad.print();
    }

    public static void testDivBackWard(){
        int[] dim1 = {4,1,2};
        int[] dim2 = {2,1,3,2};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_INCREASE);
        MultiVector mv2 = new MultiVector(dim2, Calculation.SET_INCREASE);
        Node n1 = new Node(mv1);
        Node n2 = new Node(mv2);
        OpNode divNode = new OpNode(n1, n2, Calculation.DIV);
        divNode.transForward();
        divNode._grad.set_ones();
        divNode.transBack();
        n2._grad.print();
    }
}
