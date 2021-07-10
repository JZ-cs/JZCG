package dataDistribute.utils;

import CG.ComputationalGraph;
import Foundation.Calculation;
import Foundation.MultiVector;
import Foundation.Node;

public class GenCG {
    public static ComputationalGraph genSmallCG(int pBatchSize){
        /* z = sum(xw1 + b1)*/
        ComputationalGraph cg = new ComputationalGraph();
        int[] dimx = {12, 8};
        dimx[0] = pBatchSize;
        Node x = cg.CGnode(new MultiVector(dimx), false);
        x.setName("X");
        cg.input = x;
        int[] dimw1 = {8, 4};
        Node w1 = cg.CGnode(new MultiVector(dimw1, Calculation.SET_INCREASE), true);
        w1._tensor.mul(0.01);
        w1.setName("w1");
        Node h1 = cg.matmul(x, w1);
        h1.setName("h1");

        int[] dimb1 = {4};
        Node b1 = cg.CGnode(new MultiVector(dimb1, Calculation.SET_ALL_ZEROS), true);
        b1.setName("b1");
        Node z1 = cg.add(h1, b1);
        z1.setName("z1");
        Node z = cg.sum(z1, true, 1);
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
