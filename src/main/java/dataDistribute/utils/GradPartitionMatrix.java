package dataDistribute.utils;

import utils.GradSeq;
/*gPartitions is like(use 4 servers as an example):
      s0   s1   s2   s3
* p0 [l0] [l0] [l0] [l0]
* p1 [l1] [l1] [l1] [l1]
* p2 [l2] [l2] [l2] [l2]
* p3 [l3] [l3] [l3] [l3]
* here li means the data seq length, and sum(l_{i}) = CG.totalGradNums*/
public class GradPartitionMatrix {
    public GradSeq[][] gPartitions;
    public int me;
    public int parts;
    public int[][] pinfos;
    public GradPartitionMatrix(int parts, int[][] pinfos, double[] gbuff, int me){
        this.me = me;
        this.parts = parts;
        this.pinfos = pinfos;
        this.gPartitions = new GradSeq[parts][parts];
        for(int i = 0; i < parts; i++){
            GradSeq gradSeq = new GradSeq(pinfos[i][1]);
            System.arraycopy(gbuff, pinfos[i][0], gradSeq.grads, 0, pinfos[i][1]);
            gPartitions[i][this.me] = gradSeq;
        }
    }

    public GradSeq[] getGradSeqs(int partitionId){
        return gPartitions[partitionId];
    }

    public void setPartitions(int partitionId, GradSeq[] gradSeqs){
        for(int i = 0; i < parts; i++){
            if(this.gPartitions[partitionId][i] == null && gradSeqs[i] != null){
                //set this.gPartitions[partitionId][i] = gradSeqs[i];
                this.gPartitions[partitionId][i] = new GradSeq(this.pinfos[partitionId][1]);
                System.arraycopy(gradSeqs[i].grads, 0, this.gPartitions[partitionId][i].grads, 0, this.pinfos[partitionId][1]);
            }
        }
    }

    public boolean isMatrixFull(){
        int fullParts = 0;
        for(int i = 0; i < this.parts; i++){
            for (int j = 0; j < this.parts; j++) {
                if(this.gPartitions[i][j] != null){
                    fullParts += 1;
                }
            }
        }
        return fullParts == this.parts * this.parts;
    }

    public String getMatrixInfoString(){
        // '*' means that part is not null, while '.' stands for null part.
        StringBuilder sb = new StringBuilder();
        sb.append(" s");
        for(int i = 0; i < this.parts; i++){
            sb.append(" " + i);
        }
        sb.append("\n");
        sb.append("p ");
        for(int i = 0; i < 2 * this.parts; i++){
            sb.append(" ");
        }
        sb.append("\n");
        for(int i = 0; i < this.parts; i++){
            sb.append(i + " ");
            for (int j = 0; j < this.parts; j++) {
                if(this.gPartitions[i][j] != null){
                    sb.append(" " + "*");
                }
                else {
                    sb.append(" " + ".");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public double[] getAllPartsSum(){
        double[] allSums = new double[parts];
        for(int i = 0; i < this.parts; i++){
            for(int j = 0; j < this.parts; j++){
                if(gPartitions[i][j] != null){
                    allSums[j] += gPartitions[i][j].getGradsSum();
                }
            }
        }
        return allSums;
    }
}
