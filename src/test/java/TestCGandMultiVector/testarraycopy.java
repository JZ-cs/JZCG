package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;

public class testarraycopy {
    public static void main(String[] args) {
        int[] dims = {2,3,4};
        MultiVector mv = new MultiVector(dims, Calculation.SET_ALL_ONES);
        mv.mul(2);
        mv.mul(mv);
        mv.print();
    }
}
