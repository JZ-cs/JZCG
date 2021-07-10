package dataDistribute.utils;

import CG.ComputationalGraph;
import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class GenCG {
    public static ComputationalGraph genSmallCG(int pBatchSize){
        /* z = (xw1 + b1)w2*/
        ComputationalGraph cg = new ComputationalGraph();
        int[] dimx = {12, 16};
        dimx[0] = pBatchSize;
        Node x = cg.CGnode(new MultiVector(dimx), false);
        x.setName("X");
        cg.input = x;
        int[] dimw1 = {16, 8};
        Node w1 = cg.CGnode(new MultiVector(dimw1, Calculation.SET_ALL_ZEROS), true);
//        w1._tensor.mul(0.0001);
        w1.setName("w1");
        Node h1 = cg.matmul(x, w1);
        h1.setName("h1");

        int[] dimb1 = {8};
        Node b1 = cg.CGnode(new MultiVector(dimb1, Calculation.SET_ALL_ZEROS), true);
        b1.setName("b1");
        Node z1 = cg.add(h1, b1);
        z1.setName("z1");
        Node l1 = cg.sigmoid(z1);
        l1.setName("l1");
        int[] dimw2 = {8, 1};
        Node w2 = cg.CGnode(new MultiVector(dimw2, Calculation.SET_ALL_ONES), true);
        Node z = cg.matmul(l1, w2);
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
