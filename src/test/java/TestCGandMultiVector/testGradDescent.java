package TestCGandMultiVector;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.GenCG;
import utils.DataGenerator;

public class testGradDescent {
    public static void main(String[] args) {
        int batches = 10;
        int batchSize = 12;
        int[] xShape = {8};
        ComputationalGraph cg = GenCG.genSmallCG(12);
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(false);
        MultiVector[] X = data.first;
        MultiVector[] Y = data.second;
        for(int ep = 0; ep < 1; ep++){
            for(int b = 0; b < X.length; b++){
                cg.input._tensor.set_with(X[b]);
                cg.label._tensor.set_with(Y[b]);
                cg.DAG.transForward();
                cg.DAG._grad.set_ones();
                cg.DAG.transBack();
//                cg.nodeNameMap.get("z")._tensor.print();
//                cg.label._tensor.print();
//                cg.nodeNameMap.get("w1")._grad.print();
                cg.DAG._updateWith_Grad(0.01);
//                System.out.println(cg.nodeNameMap.get("MseLoss"));
                System.out.printf("Epoch-%d, batch-%d, loss: %f %n", ep, b, cg.loss.getLoss());
            }
        }
    }
}
