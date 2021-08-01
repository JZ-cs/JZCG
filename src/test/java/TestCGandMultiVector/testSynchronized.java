package TestCGandMultiVector;

import operation.Calculation;
import operation.MultiVector;
import org.jetbrains.annotations.TestOnly;

public class testSynchronized {
    public static void main(String[] args) throws InterruptedException {
        int[] dims = {2,3,4};
        MultiVector mv1 = new MultiVector(dims, Calculation.SET_ALL_ONES);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    mv1.addGrad(1.0);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    mv1.addGrad(1.0);
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        mv1.print();
    }
}
