package TestNIO;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class testThreadPool {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        });
        CompletableFuture<String> cf2 = cf.thenCompose((param)->{
            try {
                Thread.sleep(param * 100);
            } catch (InterruptedException e) {
            }
            return CompletableFuture.supplyAsync(()->{
                return "xxxxxxx";
            });
        });
        System.out.println(cf2.get());
    }
}
