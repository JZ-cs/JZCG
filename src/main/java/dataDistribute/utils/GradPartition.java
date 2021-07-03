package dataDistribute.utils;

import utils.GradSeq;

import java.util.Arrays;

public class GradPartition {
    public GradSeq[] gradSeqs;
    public int parts;
    public int[][] pinfos;
    public GradPartition(int parts, int[][] pinfos, double[] gbuff){
        this.parts = parts;
        this.pinfos = pinfos;
        this.gradSeqs = new GradSeq[parts];
        for(int i = 0; i < parts; i++){
            GradSeq gradSeq = new GradSeq(pinfos[i][1]);
            System.arraycopy(gbuff, pinfos[i][0], gradSeq.grads, 0, pinfos[i][1]);
            gradSeqs[i] = gradSeq;
        }
    }

    public void replaceWith(int partId, GradSeq gradSeq) throws Exception {
        this.gradSeqs[partId].replaceWith(gradSeq);
    }
    public void addWith(int partId, GradSeq gradSeq) throws Exception {
        this.gradSeqs[partId].addWith(gradSeq);
    }

    @Override
    public String toString() {
        return "GradPartition{" +
                "gradSeqs=" + Arrays.toString(gradSeqs) +
                ", parts=" + parts +
                ", pinfos=" + Arrays.toString(pinfos) +
                '}';
    }
}
