package dataDistribute;

import Foundation.MultiVector;
import Foundation.Pair;
import dataDistribute.utils.ServerInfo;
import utils.TrainingInfo;

import java.util.ArrayList;

public class JobManager{
    public TrainingInfo metaTrainingInfo;
    public ServerInfo[] serverInfoList;
    public JobManager(TrainingInfo trainingInfo){
        this.metaTrainingInfo = trainingInfo;
        this.serverInfoList = trainingInfo.serverInfoList;
    }
    public void runJob() throws InterruptedException {
        TrainingInfo[] trainingInfos = getPartitionTrainingInfos();
        int numServers = serverInfoList.length;
        int masterID = 0;
        TaskManager[] taskManagers = new TaskManager[numServers];
        for(int i = 0; i < numServers; i++){
            if(i == masterID){
                taskManagers[i] = new TaskManager(serverInfoList, i, masterID, trainingInfos);
            }
            else{
                taskManagers[i] = new TaskManager(serverInfoList, i, masterID);
            }
        }
        Thread[] runThreads = new Thread[numServers];
        for(int i = 0; i < numServers; i++){
            runThreads[i] = new Thread(new runThread(taskManagers[i]));
        }
        for(int i = 0; i < numServers; i++){
            runThreads[i].start();
        }
        for(int i = 0; i < numServers; i++){
            runThreads[i].join();
        }
        System.out.println("Job Done!");

    }
    public TrainingInfo[] getPartitionTrainingInfos(){
        int numServers = serverInfoList.length;
        TrainingInfo[] trainingInfos = new TrainingInfo[numServers];
        int dLen = metaTrainingInfo.X.length;
        int lLen = metaTrainingInfo.Y.length;
        for(int i = 0; i < numServers; i++){
            MultiVector[] pX = new MultiVector[dLen];
            MultiVector[] pY = new MultiVector[lLen];
            int pBatchSize = metaTrainingInfo.batchSize / numServers;
            for(int j = 0; j < dLen; j++){
                Pair<Integer, Integer> batchRange = Pair.make_pair(i * pBatchSize, (i + 1) * pBatchSize);
                pX[j] = MultiVector.slice(metaTrainingInfo.X[j], true, batchRange);
                pY[j] = MultiVector.slice(metaTrainingInfo.Y[j], true, batchRange);
            }
            trainingInfos[i] = new TrainingInfo(serverInfoList, metaTrainingInfo.CG, pX, pY, metaTrainingInfo.batches, pBatchSize, metaTrainingInfo.epoches, metaTrainingInfo.lr);
        }
        return trainingInfos;
    }

}
class runThread implements Runnable{
    TaskManager taskManager;
    public runThread(TaskManager taskManager){
        this.taskManager = taskManager;
    }
    @Override
    public void run() {
        try {
            taskManager.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
