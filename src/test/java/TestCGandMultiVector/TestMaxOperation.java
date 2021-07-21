package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;

import java.util.HashMap;
import java.util.Map;

public class TestMaxOperation {
    public static void main(String[] args) {
        System.out.println(ff(1,2,3));
    }
    public static int ff(int...axes){
        int res = 0;
        for(int x : axes){
            res += x;
        }
        return res;
    }
    public static void test1(){
        int[] dim1 = {2,3,2};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_RANDOM_UINT16);
        mv1.print();
        HashMap<Integer, Integer> markers = new HashMap<>();
        MultiVector maxMv = MultiVector.max(mv1, markers, true, 1);
        maxMv.print();
        for(Map.Entry<Integer, Integer> e : markers.entrySet()){
            System.out.println(e);
        }
    }

    public static void test2(){
        int[] dim1 = {2,2,3,2};
        MultiVector mv1 = new MultiVector(dim1, Calculation.SET_RANDOM_UINT16);
        mv1.print();
        HashMap<Integer, Integer> markers = new HashMap<>();
        MultiVector maxMv = MultiVector.max(mv1, markers, false, 3);
        maxMv.print();
        for(Map.Entry<Integer, Integer> e : markers.entrySet()){
            System.out.println(e);
        }
    }
}
