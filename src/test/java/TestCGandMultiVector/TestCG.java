package TestCGandMultiVector;

import CG.ComputationalGraph;
import Foundation.*;
import Foundation.lossFunctions.MSELoss;
import dataDistribute.utils.GenCG;
import utils.DataGenerator;

import java.util.Map;

public class TestCG {
    public static void main(String[] args) {
        test1();
    }
    public static void test1(){
        /*
        * h1 = xw1,   z1 = h1 + b1,   l1 = sigmoid(z1),   h2 = l1w2
        * z = sum(h2)
        * z = sum(Sigmoid(xw1 + b1) * w2)
        */
        int batches = 10;
        int batchSize = 12;
        int[] xShape = {32};
        ComputationalGraph cg = GenCG.genSmallCG(12);
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(true);
        MultiVector[] X = data.first;
        MultiVector[] Y = data.second;
        for(int ep = 0; ep < 10; ep++){
            for(int i = 0; i < X.length; i++){
                cg.input._tensor.set_with(X[i]);
                cg.label._tensor.set_with(Y[i]);
                cg.DAG.transForward();
                cg.DAG._grad.set_ones();
                cg.DAG.transBack();
                cg.nodeNameMap.get("w1")._grad.print();
                cg.DAG._updateWith_Grad(0.05);
            }
        }
    }
}
