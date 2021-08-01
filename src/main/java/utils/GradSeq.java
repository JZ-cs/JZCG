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

    public GradSeq(GradSeq gradSeq){
        this.totalEle = gradSeq.totalEle;
        this.grads = new double[this.totalEle];
        System.arraycopy(gradSeq.grads, 0, this.grads, 0, gradSeq.totalEle);
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
        for (double grad : grads) {
            sums += grad;
        }
        return sums;
    }
    public void replaceWith(GradSeq gradSeq){
        if (gradSeq.totalEle != this.totalEle) {
            throw new RuntimeException("Must use grads seq of same length to set!");
        }
        System.arraycopy(gradSeq.grads, 0, this.grads, 0, gradSeq.totalEle);
    }

    public void addWith(GradSeq gradSeq) {
        if (gradSeq.totalEle != this.totalEle) {
            throw new RuntimeException("Must use grads seq of same length to add!");
        }
        for(int i = 0; i < this.totalEle; i++){
            this.grads[i] += gradSeq.grads[i];
        }
    }
}
