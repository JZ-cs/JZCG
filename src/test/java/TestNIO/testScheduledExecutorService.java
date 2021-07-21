package TestNIO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class testScheduledExecutorService {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        ScheduledFuture<?> future = executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("start!");
                for(int i = 0; i < 10000; i++){
                    System.out.println(i);
                }
            }
        }, 800, TimeUnit.MILLISECONDS);

        executor.shutdown();
    }
}
