package TestModelDistribute;

import CG.ComputationalGraph;
import dataDistribute.utils.GenCG;
import utils.ModelSplits;
import operation.Node;

public class testUnpackMoudle {
    public static void main(String[] args) {
        ComputationalGraph cg = GenCG.genComplexSmallCG(12);
        ModelSplits.unpackSubMoudles(cg.DAG);
        for(Node node : cg.DAG.nList){
            System.out.println(node.getConnectionInfo());
        }
        cg.transForward();
        cg.transBack();
    }
}
