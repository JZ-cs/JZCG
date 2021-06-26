package TestCGandMultiVector;

import Foundation.*;

public class TestSumBackward {
    public static void main(String[] args) {
        test1();
    }
    public static void test1(){
        int[] dim1 = {2,3,4};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_INCREASE);
        Node n1 = new Node(mv1);
        SumNode sumNode = new SumNode(n1, true, 0);

        int[] dim2 = {5,3,4};
        MultiVector mv2 = new MultiVector(dim2, Calculation.SET_INCREASE);
        Node n2 = new Node(mv2);
        OpNode mulNode = new OpNode(sumNode, n2, Calculation.MUL);


        sumNode.transForward();
        mulNode.transForward();
        mulNode._grad.set_ones();
        mulNode.transBack();
        sumNode.transBack();
//        sumNode._grad.print();
        n1._grad.print();
    }
}
