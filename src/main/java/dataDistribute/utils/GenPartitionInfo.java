package dataDistribute.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class GenPartitionInfo {
    public static int[][] genSeqPartitionInfo(int pNum, int seqLen) throws Exception{
        int[][] res = new int[pNum][2];
        if(pNum <= 0){
            throw new Exception(String.format("Can not generate infoList for partition num = %d", pNum));
        }
        if(pNum == 1){
            res[0][0] = 0;
            res[0][1] = seqLen;
        }
        else if(pNum >= seqLen){
            for(int i = 0; i < seqLen; i++){
                res[i][0] = i;
                res[i][1] = 1;
            }
            for(int i = seqLen; i < pNum; i++){
                res[i][0] = 0;
                res[i][1] = 0;
            }
        }
        else{
            if(seqLen % pNum == 0){
                int psize = seqLen / pNum;
                for(int i = 0; i < pNum; i++){
                    res[i][0] = i * psize;
                    res[i][1] = psize;
                }
            }
            else{
                int psize = seqLen / pNum;
                int remain = seqLen % pNum;
                int preStart = 0;
                for(int i = 0; i < pNum; i++){
                    res[i][0] = preStart;
                    res[i][1] = psize;
                    preStart += psize;
                    if(remain > 0){
                        res[i][1] += 1;
                        preStart += 1;
                        remain--;
                    }
                }
            }
        }
        return res;
    }
}
