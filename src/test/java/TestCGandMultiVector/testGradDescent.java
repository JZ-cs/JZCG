package TestCGandMultiVector;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.GenCG;
import utils.DataGenerator;

public class testGradDescent {
    public static void main(String[] args) throws Exception {
        int batches = 5;
        int batchSize = 36;
        int[] xShape = {16};
        ComputationalGraph cg = GenCG.genSmallCG(batchSize);
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(true);
        MultiVector[] X = data.first;
        MultiVector[] Y = data.second;
        for(int ep = 0; ep < 1; ep++){
            for(int b = 0; b < X.length; b++){
                cg.setData(X[b], Y[b]);
                cg.DAG.transForward();
                cg.DAG._grad.set_ones();
                cg.DAG.transBack();
//                cg.nodeNameMap.get("z")._tensor.print();
//                cg.label._tensor.print();
//                cg.nodeNameMap.get("w1")._grad.print();
                cg.DAG._updateWith_Grad(0.001);
                System.out.printf("Epoch-%d, batch-%d, loss: %f %n", ep, b, cg.loss.getLoss());
            }
        }
    }
}
