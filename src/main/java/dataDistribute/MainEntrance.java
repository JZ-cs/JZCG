package dataDistribute;

import CG.ComputationalGraph;
import operation.MultiVector;
import operation.Pair;
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
        DDTaskManager taskManager;
        ServerInfo[] serverInfos = new ServerInfo[]{
                new ServerInfo("1.117.99.222", 28107, 28307, 18507),
                new ServerInfo("118.31.46.60", 39107, 39307, 39507),
        new ServerInfo("47.242.192.193", 17107, 17307, 17507)
        };
        if(i == masterId){
            int batches = 10;
            int batchSize = 36;
            int numServers = serverInfos.length;
            int epoches = 3;
            double lr = 0.001;
            int[] xShape = {16};
            ComputationalGraph CG = GenCG.genSmallCG(batchSize / numServers);
            DataGenerator dataGenerator = new DataGenerator(batches, batchSize, xShape);
            Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(true);
            MultiVector[] X = data.first;
            MultiVector[] Y = data.second;


            TrainingInfo trainingInfo = new TrainingInfo(serverInfos, CG, X, Y, batches, batchSize, epoches, lr);
            DDJobManager jobManager = new DDJobManager(trainingInfo);
            TrainingInfo[] trainingInfos = jobManager.getPartitionTrainingInfos();
            taskManager = new DDTaskManager(serverInfos, i, masterId, trainingInfos);
        }
        else{
            taskManager = new DDTaskManager(serverInfos, i, masterId);
        }
        Thread t = new Thread(new runThread(taskManager));
        t.start();
        t.join();
    }
}
