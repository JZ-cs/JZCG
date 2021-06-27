package dataDistribute;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.GenCG;
import dataDistribute.utils.ServerInfo;
import utils.DataGenerator;
import utils.TrainingInfo;
import org.apache.commons.cli.*;

public class MainEntrance {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        options.addOption("m","master",true,"master id");
        options.addOption("i","id",true,"current machine id");

        CommandLine commandLine = parser.parse(options,args);

        int masterId = -1;
        int i = -1;
        if (commandLine.hasOption("m")){
            masterId = Integer.parseInt(commandLine.getOptionValue('m'));
        }
        else{
            throw new RuntimeException("must designate the id of master!");
        }

        if (commandLine.hasOption("i")){
            i = Integer.parseInt(commandLine.getOptionValue('i'));
        }
        else {
            throw new RuntimeException("must designate the id of current machine!");
        }
        TaskManager taskManager;
        ServerInfo[] serverInfos = new ServerInfo[]{
                new ServerInfo("129.211.184.219", 17107, 17307, 17507),
                new ServerInfo("1.117.99.222", 28107, 28307, 18507),
                new ServerInfo("118.31.46.60", 39107, 39307, 39507)
        };
        if(i == masterId){
            int batches = 10;
            int batchSize = 36;
            int numServers = serverInfos.length;
            int epoches = 1;
            double lr = 0.05;
            int[] xShape = {32};
            ComputationalGraph CG = GenCG.genSmallCG(batchSize / numServers);
            DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
            Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(false);
            MultiVector[] X = data.first;
            MultiVector[] Y = data.second;
//            String ip = "10.67.45.162";//localhost
//            ServerInfo[] serverInfos = new ServerInfo[]{new ServerInfo(ip, 19997, 20997, 21997),
//                    new ServerInfo(ip, 22997, 23997, 24997)};

            TrainingInfo trainingInfo = new TrainingInfo(serverInfos, CG, X, Y, batches, batchSize, epoches, lr);
            JobManager jobManager = new JobManager(trainingInfo);
            TrainingInfo[] trainingInfos = jobManager.getPartitionTrainingInfos();
            taskManager = new TaskManager(serverInfos, i, masterId, trainingInfos);
        }
        else{
            taskManager = new TaskManager(serverInfos, i, masterId);
        }
        Thread t = new Thread(new runThread(taskManager));
        t.start();
        t.join();
    }
}
