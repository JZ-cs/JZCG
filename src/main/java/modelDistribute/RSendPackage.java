package modelDistribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RSendPackage implements Serializable {
    ConcurrentHashMap<String, double[]> dataMap;
    HashMap<String, Integer> degreeDec;
    public AtomicInteger seted;
    public MDTaskRunnerID mdTaskRunnerID;
    public final int SIZE;
    int remotePort;
    public RSendPackage(MDTaskRunnerID mdTaskRunnerID, int remotePort, int size, HashMap<String, Integer> degreeDec){
        this.seted = new AtomicInteger();
        this.seted.set(0);
        this.SIZE = size;
        this.degreeDec = degreeDec;
        this.dataMap = new ConcurrentHashMap<>(size);
    }
    public void setData(String nodeName, double[] data){
        if(!dataMap.contains(nodeName)){
            dataMap.put(nodeName, data);
            this.seted.addAndGet(1);
        }
    }

    public boolean isComplete(){
        return seted.get() == this.SIZE;
    }
}
