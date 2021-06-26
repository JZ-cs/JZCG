package utils;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public int batches;
    public int batchSize;
    public int[] pershape;
    public DataGenerator(int batches, int batchSize, int[] pershape){
        this.batches = batches;
        this.batchSize = batchSize;
        this.pershape = pershape;
    }

    public MultiVector genMinibatchData(boolean randomFlag){
        int[] dims = new int[pershape.length + 1];
        dims[0] = this.batchSize;
        System.arraycopy(pershape, 0, dims, 1, pershape.length);
        if(randomFlag) return new MultiVector(dims, Calculation.SET_RANDOM_UINT16);
        else return new MultiVector(dims, Calculation.SET_INCREASE);
    }

    public MultiVector genMinibatchLabel(boolean randomFlag){
        int[] dims = {this.batchSize, 1};
        MultiVector res;
        if(randomFlag){
            res = new MultiVector(dims, Calculation.SET_RANDOM_UINT16);
        }
        else{
            res =  new MultiVector(dims, Calculation.SET_INCREASE);
        }

        return res;
    }

    public Pair<MultiVector[], MultiVector[]> genData(boolean randomFlag){
        MultiVector[] x = new MultiVector[this.batches];
        MultiVector[] y = new MultiVector[this.batches];
        for(int i = 0; i < this.batches; i++){
            x[i] = genMinibatchData(randomFlag);
            y[i] = genMinibatchLabel(randomFlag);
        }
        return Pair.make_pair(x, y);
    }

}
