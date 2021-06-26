package TestDataDistribute;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.GenCG;
import dataDistribute.utils.ServerInfo;
import utils.DataGenerator;
import utils.TrainingInfo;

public class testDDJob {
    public static void main(String[] args) {
        int batches = 20;
        int batchSize = 36;
        double lr = 0.001;
        int[] xshape = {32};
        int epoches = 5;
        ServerInfo[] serverInfos = new ServerInfo[]{};
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xshape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(false);
        MultiVector[] x = data.first;
        MultiVector[] y = data.second;
        ComputationalGraph CG = GenCG.genSmallCG(batchSize / serverInfos.length);
        TrainingInfo trainingInfo = new TrainingInfo(serverInfos, CG, x, y, batches, batchSize, epoches, lr);

        System.out.println(x[0]._shape);
    }
}
