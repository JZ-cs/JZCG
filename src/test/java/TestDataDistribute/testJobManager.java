package TestDataDistribute;

import CG.ComputationalGraph;
import operation.MultiVector;
import operation.Pair;
import dataDistribute.DDJobManager;
import dataDistribute.utils.GenCG;
import dataDistribute.utils.ServerInfo;
import utils.DataGenerator;
import utils.TrainingInfo;

import java.net.InetAddress;

public class testJobManager {
    public static void main(String[] args) throws Exception {
        int batches = 5;
        int batchSize = 36;
        int epoches = 1;
        double lr = 0.001;
        int[] xShape = {16};
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();//localhost
        ServerInfo[] serverInfos = new ServerInfo[]{
                new ServerInfo(ip, 19997, 20997, 21997),
                new ServerInfo(ip, 22997, 23997, 24997),
                new ServerInfo(ip, 25997, 26997, 27997)
        };
        int numServers = serverInfos.length;
        ComputationalGraph CG = GenCG.genSmallCG(batchSize / numServers);
        DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(true);
        MultiVector[] X = data.first;
        MultiVector[] Y = data.second;

        TrainingInfo trainingInfo = new TrainingInfo(serverInfos, CG, X, Y, batches, batchSize, epoches, lr);
        DDJobManager jobManager = new DDJobManager(trainingInfo);
        jobManager.runJob();
    }
}
