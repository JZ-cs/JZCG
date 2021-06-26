import java.util.ArrayList;
import java.util.Arrays;

public class testRMI {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            threadArrayList.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        for(Thread t : threadArrayList){
            t.start();
        }
        for(Thread t : threadArrayList){
            t.join();
        }
        System.out.println("adasdasdasd");
    }
}
class Person{
    public int[] data;
    int id;
    public Person(){
    }
    public Person(int len){
        this.data = new int[len];
        this.id = len;
    }

    public void setWith(Person p){
        this.data = p.data;
        this.id = p.id;
    }

    @Override
    public String toString() {
        return "person{" +
                "data=" + Arrays.toString(data) +
                ", id=" + id +
                '}';
    }
}
class copyThread implements  Runnable{
    public Person p;
    public copyThread(Person p){
        this.p = p;
    }
    @Override
    public void run() {
        Person pp = new Person(100);
        this.p.setWith(pp);
//        System.out.println(p);
//        System.out.println(pp);
    }
}

