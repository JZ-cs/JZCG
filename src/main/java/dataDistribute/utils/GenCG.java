package dataDistribute.utils;

import CG.ComputationalGraph;
import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class GenCG {
    public static ComputationalGraph genSmallCG(int pBatchSize){
        /* z = sum(Sigmoid(xw1 + b1) * w2)*/
        ComputationalGraph cg = new ComputationalGraph();
        int[] dimx = {12, 32};
        dimx[0] = pBatchSize;
        Node x = cg.CGnode(new MultiVector(dimx), false);
        x.setName("X");
        cg.input = x;
        int[] dimw1 = {32, 16};
        Node w1 = cg.CGnode(new MultiVector(dimw1, Calculation.SET_ALL_ONES), true);
        w1.setName("w1");
        Node h1 = cg.matmul(x, w1);
        h1.setName("h1");

        int[] dimb1 = {16};
        Node b1 = cg.CGnode(new MultiVector(dimb1, Calculation.SET_ALL_ZEROS), true);
        b1.setName("b1");
        Node z1 = cg.add(h1, b1);
        z1.setName("z1");
        Node l1 = cg.sigmoid(z1);
        l1.setName("l1");

        int[] dimw2 = {16, 8};
        Node w2 = cg.CGnode(new MultiVector(dimw2, Calculation.SET_INCREASE), true);
        w2.setName("w2");
        Node h2 = cg.matmul(l1, w2);
        h2.setName("h2");
        Node z = cg.sum(h2, true, 1);
        z.setName("z");
        int[] dimy = {12, 1};
        dimy[0] = pBatchSize;
        Node y = cg.CGnode(new MultiVector(dimy), false);
        y.setName("Y");
        cg.label = y;
        Node mseLoss = cg.MSELoss(z, y);
        mseLoss.setName("MseLoss");
        cg.gatherInfo();
        return cg;
    }
}
