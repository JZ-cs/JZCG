package network.netty;

import utils.GradSeq;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class GradPackage implements Serializable {
    public static int ADD = 0;
    public static int SET = 1;
    public GradSeq getGradSeq() {
        return gradSeq;
    }

    public void setGradSeq(GradSeq gradSeq) {
        this.gradSeq = gradSeq;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public GradSeq gradSeq;
    public int partitionId;
    public int op;

    public GradPackage(GradSeq gradSeq, int partitionId, int op){
        this.gradSeq = gradSeq;
        this.partitionId = partitionId;
        this.op = op;
    }

    @Override
    public String toString() {
        return "GradPackage{" +
                "gradSeq=" + gradSeq +
                ", partitionId=" + partitionId +
                '}';
    }
}
