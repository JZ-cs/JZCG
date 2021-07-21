package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;

public class TestInitialization {
    public static void main(String[] args) {
//        testSET_ZEROS(1000);
        testSET_DOUBLE_MIN(1000);
        testSET_NONE(1000);
    }
    public static void testSET_DOUBLE_MIN(int iters){
        long st = System.currentTimeMillis();
        for(int i = 0; i < iters; i++){
            int[] dim = {100,100,100};
            MultiVector mv1 = new MultiVector(dim, Calculation.SET_DOUBLE_MIN);
        }
        long ed = System.currentTimeMillis();
        System.out.println("set with double.min: " + (ed - st) + "ms");
    }
    public static void testSET_ZEROS(int iters){
        long st = System.currentTimeMillis();
        for(int i = 0; i < iters; i++){
            int[] dim = {100,100,100};
            MultiVector mv1 = new MultiVector(dim, Calculation.SET_ALL_ZEROS);
        }
        long ed = System.currentTimeMillis();
        System.out.println("set with zeros: " + (ed - st) + "ms");
    }

    public static void testSET_NONE(int iters){
        long st = System.currentTimeMillis();
        for(int i = 0; i < iters; i++){
            int[] dim = {100,100,100};
            MultiVector mv1 = new MultiVector(dim);
        }
        long ed = System.currentTimeMillis();
        System.out.println("set None: " + (ed - st) + "ms");
    }
}
