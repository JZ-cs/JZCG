package utils;

import java.io.Serializable;
import java.util.Arrays;

public class GradSeq implements Serializable {
    public double[] grads;
    public int totalEle = 0;
    public GradSeq(int glen){
        this.grads = new double[glen];
        this.totalEle = glen;
    }
    @Override
    public String toString() {
        return "GradSeq{" +
                "grads=" + Arrays.toString(grads) +
                ", totalEle=" + totalEle +
                '}';
    }
    public double[] getGrads() {
        return grads;
    }

    public void setGrads(double[] grads) {
        this.grads = grads;
    }

    public int getTotalEle() {
        return totalEle;
    }

    public void setTotalEle(int totalEle) {
        this.totalEle = totalEle;
    }

    public double getGradsSum(){
        double sums = 0;
        for(int i = 0; i < grads.length; i++){
            sums += grads[i];
        }
        return sums;
    }
}
