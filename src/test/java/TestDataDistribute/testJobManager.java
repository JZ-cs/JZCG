package TestDataDistribute;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.JobManager;
import dataDistribute.utils.GenCG;
import dataDistribute.utils.ServerInfo;
import utils.DataGenerator;
import utils.TrainingInfo;

public class testJobManager {
    public static void main(String[] args) throws InterruptedException {
        int batches = 10;
        int batchSize = 36;
        int numServers = 2;
        int epoches = 1;
        double lr = 0.05;
        int[] xShape = {32};
        ComputationalGraph CG = GenCG.genSmallCG(batchSize / numServers);
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(false);
        MultiVector[] X = data.first;
        MultiVector[] Y = data.second;
        ServerInfo[] serverInfos = new ServerInfo[]{new ServerInfo("localhost", 19997, 20997, 21997),
        new ServerInfo("localhost", 22997, 23997, 24997)};

        TrainingInfo trainingInfo = new TrainingInfo(serverInfos, CG, X, Y, batches, batchSize, epoches, lr);
        JobManager jobManager = new JobManager(trainingInfo);
        jobManager.runJob();
    }
}
