package TestDataDistribute;

import dataDistribute.utils.GenPartitionInfo;
import dataDistribute.utils.GradPartitionMatrix;
import utils.GradSeq;

public class testRingAllReduce {
    public static void main(String[] args) throws Exception {
        testNServers(4);
    }
    public static void testNServers(int n) throws Exception {
        GradPartitionMatrix[] gpms = new GradPartitionMatrix[n];
        int seqLen = n + 1;
        int[][] pinfos = GenPartitionInfo.genSeqPartitionInfo(n, seqLen);
        for(int i = 0; i < n; i++){
            double[] gbuff = new double[seqLen];
            for(int j = 0; j < seqLen; j++) gbuff[j] = j + 1 + 1000 * i;
            gpms[i] = new GradPartitionMatrix(n, pinfos, gbuff, i);
        }

        //before exchange
//        for(int i = 0; i < n; i++){
//            System.out.println("matrix in Server-" + i);
//            System.out.println(gpms[i].getMatrixInfoString());
//            System.out.println("----------------------------------");
//        }
        int x = n;
        for(int t = 0; t < 2 * (n - 1); t++){
            for(int i = 0; i < n; i++){
                int pid = (i + x) % n;
                GradSeq[] sendPartitions = gpms[i].gPartitions[pid];
                gpms[(i + 1) % n].setPartitions(pid, sendPartitions);
            }
            System.out.printf("grad exchange completed: %.2f%%%n", 100.0 * (t + 1) / (2 * (n - 1)));
            x--;
            if(x == -1) x = n - 1;
        }
        // after exchange
//        for(int i = 0; i < n; i++){
//            System.out.println("matrix in Server-" + i);
//            System.out.println(gpms[i].getMatrixInfoString());
//            System.out.println("----------------------------------");
//        }
        for(int p = 0; p < n; p++){
            System.out.println("matrix in Server-" + p);
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    System.out.print(gpms[p].gPartitions[i][j]);
                    System.out.print("   ");
                }
                System.out.println();
            }
        }
    }
}
