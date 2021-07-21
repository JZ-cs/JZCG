package dataDistribute.utils;

import CG.ComputationalGraph;
import operation.Calculation;
import operation.MultiVector;
import operation.Node;
import operation.layers.Linear;

public class GenCG {
    public static ComputationalGraph genSmallCG(int pBatchSize){
        /* z = sigmoid(xw1 + b1)w2*/
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
        w2.setName("w2");
        Node z = cg.matmul(l1, w2);
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

    public static ComputationalGraph genComplexSmallCG(int pBatchSize){
        ComputationalGraph cg = new ComputationalGraph();
        int[] dimx = {12, 16};
        dimx[0] = pBatchSize;
        Node x = cg.CGnode(new MultiVector(dimx), false);//0
        x.setName("X");
        cg.input = x;
        int[] dimw1 = {16, 8};//1
        Node w1 = cg.CGnode(new MultiVector(dimw1, Calculation.SET_ALL_ZEROS), true);
//        w1._tensor.mul(0.0001);
        w1.setName("w1");
        Node h1 = cg.matmul(x, w1);//2
        h1.setName("h1");

        int[] dimb1 = {8};
        Node b1 = cg.CGnode(new MultiVector(dimb1, Calculation.SET_ALL_ZEROS), true);
        b1.setName("b1");//3
        Node z1 = cg.add(h1, b1);//4
        z1.setName("z1");
        Node linear1 = cg.Linear(z1, 4, true, 0);
        linear1.setName("Linear-1");
        Node linear2 = cg.Linear(z1, 8, true, 0);
        linear2.setName("Linear-2");
        Node linear3 = cg.Linear(linear2, 4, true, 0);
        linear3.setName("Linear-3");

        Node z = cg.add(linear1, linear3);
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
