package network.netty;

import utils.GradSeq;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class GradPackage implements Serializable {

    public GradSeq[] gradSeqs;
    public int partitionId;
    public GradSeq[] getGradSeqs() {
        return gradSeqs;
    }

    public void setGradSeqs(GradSeq[] gradSeqs) {
        this.gradSeqs = gradSeqs;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public GradPackage(GradSeq[] gradSeqs, int partitionId){
        this.gradSeqs = gradSeqs;
        this.partitionId = partitionId;
    }

    public static GradPackage generateSmallGradPackage(){
        Random rand = new Random();
        int partitionId = rand.nextInt(2);
        GradSeq[] gradSeqs = new GradSeq[2];
        for(int i = 0; i < 2; i++){
            gradSeqs[i] = new GradSeq(5);
            for(int j = 0; j < 5; j++){
                gradSeqs[i].grads[j] = rand.nextInt(65536);
            }
            gradSeqs[i].totalEle = 5;
        }
        return new GradPackage(gradSeqs,partitionId);
    }

    public static GradPackage generateGradPackage(int pNum, int pid, int lenEachServer, boolean randomFlag){
        Random rand = new Random();
        GradSeq[] gradSeqs = new GradSeq[pNum];
        for(int p = 0; p < pNum; p++){
            gradSeqs[p] = new GradSeq(lenEachServer);
            double[] d = new double[lenEachServer];
            if(randomFlag){
                for(int j = 0; j < lenEachServer; j++){
                    gradSeqs[p].grads[j] = rand.nextInt(65536);
                }
            }
            else{
                for(int j = 0; j < lenEachServer; j++){
                    gradSeqs[p].grads[j] = 1;
                }
            }
        }
        return new GradPackage(gradSeqs, pid);
    }

    @Override
    public String toString() {
        return "GradPackage{" +
                "gradSeqs=" + Arrays.toString(gradSeqs) +
                ", partitionId=" + partitionId +
                '}';
    }
}
