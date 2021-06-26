package TestCGandMultiVector;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;
import Foundation.functionNodes.SigmoidNode;

import java.util.HashMap;
import java.util.Map;

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
