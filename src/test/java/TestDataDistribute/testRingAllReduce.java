package TestDataDistribute;

import dataDistribute.utils.GenPartitionInfo;
import dataDistribute.utils.GradPartition;
import dataDistribute.utils.GradPartitionMatrix;
import utils.GradSeq;

public class testRingAllReduce {
    public static void main(String[] args) throws Exception {
        testNServers(3);
    }
    public static void testNServers(int n) throws Exception {
        GradPartition[] gps = new GradPartition[n];
        int seqLen = n + 2;
        int[][] pinfos = GenPartitionInfo.genSeqPartitionInfo(n, seqLen);
        for(int i = 0; i < n; i++){
            double[] gbuff = new double[seqLen];
            for(int j = 0; j < seqLen; j++) gbuff[j] = j + 1 + 100 * i;
            gps[i] = new GradPartition(n, pinfos, gbuff);
        }

        //before exchange
        System.out.println("Before exchange!");
        for(GradPartition gp : gps){
            System.out.println(gp);
        }
        System.out.println("---------------------------------------------------------");
        int x = n;
        for(int t = 0; t < (n - 1); t++){
            for(int i = 0; i < n; i++){
                int pid = (i + x) % n;
                GradSeq sendPartition = gps[i].gradSeqs[pid];
                gps[(i + 1) % n].addWith(pid, sendPartition);
            }
            System.out.printf("grad exchange completed: %.2f%%%n", 100.0 * (t + 1) / (2 * (n - 1)));
            x--;
            if(x == -1) x = n - 1;
        }

        for(int t = 0; t < (n - 1); t++){
            for(int i = 0; i < n; i++){
                int pid = (i + x) % n;
                GradSeq sendPartition = gps[i].gradSeqs[pid];
                gps[(i + 1) % n].replaceWith(pid, sendPartition);
            }
            System.out.printf("grad exchange completed: %.2f%%%n", 100.0 * (t + n) / (2 * (n - 1)));
            x--;
            if(x == -1) x = n - 1;
        }
        // after exchange
        System.out.println("After exchange!");
        for(GradPartition gp : gps){
            System.out.println(gp);
        }
    }
}
