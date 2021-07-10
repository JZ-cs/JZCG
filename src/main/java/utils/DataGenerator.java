package utils;

import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Pair;
import jdk.jfr.Unsigned;

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
        /* fixed pattern
        *  return y = sum(<x,x> + 2x + 3)
        * */
        MultiVector square = MultiVector.mul(x, x);
        MultiVector xm2 = MultiVector.mul(x, 2);
        MultiVector addmv = MultiVector.add(square, xm2);
        addmv.add(3);
        int[] axes = new int[addmv._dims - 1];
        for(int i = 0; i < axes.length; i++) axes[i] = i + 1;
        return MultiVector.sum(addmv, true, axes);
    }

    public Pair<MultiVector[], MultiVector[]> genData(boolean randomFlag){
        MultiVector[] x = new MultiVector[this.batches];
        MultiVector[] y = new MultiVector[this.batches];
        for(int i = 0; i < this.batches; i++){
            x[i] = genMinibatchData(randomFlag);
            x[i].mul(i / (1 << 16) / this.batches);
            y[i] = genMinibatchLabel(x[i]);
        }
        return Pair.make_pair(x, y);
    }

}
