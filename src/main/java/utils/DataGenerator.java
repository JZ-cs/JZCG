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

    public MultiVector genMinibatchLabel(MultiVector x){
        int[] dims = {this.batchSize, 1};
        MultiVector res;
        /* fixed pattern
        *  return y = sum(xw1 + b)
        * */
        int lastAx = x._shape.get(x._shape.size() - 1);
        int[] dimw1 = {8, 4};
        dimw1[0] = lastAx;
        MultiVector w1 = new MultiVector(dimw1, Calculation.SET_INCREASE);
        w1.mul(0.001);
        MultiVector addmv = MultiVector.matmul(x, w1);
        int[] dimb = new int[addmv._dims - 1];
        for(int i = 0; i < dimb.length; i++) dimb[i] = addmv._shape.get(i + 1);
        MultiVector b = new MultiVector(dimb, Calculation.SET_ALL_ONES);
        MultiVector h = MultiVector.add(addmv, b);
        int[] axes = new int[h._dims - 1];
        for(int i = 0; i < axes.length; i++){
            axes[i] = i + 1;
        }
        return MultiVector.sum(h, true, axes);
    }

    public Pair<MultiVector[], MultiVector[]> genData(boolean randomFlag){
        MultiVector[] x = new MultiVector[this.batches];
        MultiVector[] y = new MultiVector[this.batches];
        for(int i = 0; i < this.batches; i++){
            x[i] = genMinibatchData(randomFlag);
            y[i] = genMinibatchLabel(x[i]);
        }
        return Pair.make_pair(x, y);
    }

}
