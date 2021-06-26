package TestDataDistribute;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class testCountDown {
    public static CountDownLatch countDownLatch;
    public static void main(String[] args) {
        countDownLatch = new CountDownLatch(2);
        Thread t1 = new Thread(new cdThread(countDownLatch));
        Thread t2 = new Thread(new cdThread(countDownLatch));
        Thread t3 = new Thread(new wThread(countDownLatch));
        t1.start();
        t2.start();
        t3.start();
    }
}

class cdThread implements Runnable{
    public CountDownLatch countDownLatch;
    public cdThread(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.countDownLatch.countDown();
    }
}

class wThread implements Runnable{
    public CountDownLatch countDownLatch;
    public wThread(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Count down over!");
    }
}