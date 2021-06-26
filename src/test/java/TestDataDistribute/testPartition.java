package TestDataDistribute;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.GenPartitionInfo;
import dataDistribute.utils.GradPartitionMatrix;
import utils.DataGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class testPartition {

    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean ab = new AtomicBoolean();
        ab.set(false);
        myThread[] myThreads = new myThread[10];
        for(int i = 0; i < 10; i++){
            myThreads[i] = new myThread(i, ab);
            new Thread(myThreads[i]).start();
        }
        Thread.sleep(5000);
        ab.set(true);
    }

}

class myThread implements Runnable{
    public AtomicBoolean closed;
    int id;
    myThread(int id, AtomicBoolean ab){
        this.id = id;
        this.closed = ab;
    }

    @Override
    public void run() {
        for(;;){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread-id-" + id + " is running!");
            if(this.closed.get()){
                System.out.println("Now closed!!!");
                break;
            }
        }
    }
}
