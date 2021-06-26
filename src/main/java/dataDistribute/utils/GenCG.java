package dataDistribute.utils;

import CG.ComputationalGraph;
import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class GenCG {
    public static ComputationalGraph genSmallCG(int pBatchSize){
        ComputationalGraph cg = new ComputationalGraph();
        int[] dimx = {12, 32};
        dimx[0] = pBatchSize;
        Node x = cg.CGnode(new MultiVector(dimx), false);
        int[] dimw1 = {32, 16};
        Node w1 = cg.CGnode(new MultiVector(dimw1, Calculation.SET_ALL_ONES), true);
        Node h1 = cg.matmul(x, w1);

        int[] dimb1 = {16};
        Node b1 = cg.CGnode(new MultiVector(dimb1, Calculation.SET_ALL_ZEROS), true);
        Node z1 = cg.add(h1, b1);
        Node l1 = cg.sigmoid(z1);

        int[] dimw2 = {16, 8};
        Node w2 = cg.CGnode(new MultiVector(dimw2, Calculation.SET_INCREASE), true);
        Node h2 = cg.matmul(l1, w2);
        Node z = cg.sum(h2, true, 1);
        int[] dimy = {12, 1};
        dimy[0] = pBatchSize;
        Node y = cg.CGnode(new MultiVector(dimy), false);

        Node mseLoss = cg.MSELoss(z, y);
        return cg;
    }
}
