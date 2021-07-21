package dataDistribute;

import dataDistribute.utils.ServerInfo;
import network.netty.initializer.iclient.InitializerClient;
import network.netty.initializer.iserver.InitializerServer;
import utils.TrainingInfo;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class DDTaskManager {
    public TrainingInfo trainingInfo;
    public TrainingInfo[] sendTrainingInfos;
    public ServerInfo[] serverInfoList;
    public int me;
    public int masterID;
    public DDTaskManager(ServerInfo[] serverInfoList, int me, int masterID){
        this.trainingInfo = new TrainingInfo();
        this.serverInfoList = serverInfoList;
        this.me = me;
        this.masterID = masterID;
        this.sendTrainingInfos = null;
    }

    public DDTaskManager(ServerInfo[] serverInfoList, int me, int masterID, TrainingInfo[] trainingInfos){
        this.serverInfoList = serverInfoList;
        this.me = me;
        this.masterID = masterID;
        this.trainingInfo = trainingInfos[me];
        this.sendTrainingInfos = new TrainingInfo[serverInfoList.length];
        for(int i = 0; i < serverInfoList.length; i++){
            if(i != me) sendTrainingInfos[i] = trainingInfos[i];
        }
    }

    public void run() throws InterruptedException {
        if(this.masterID == this.me){
            /* TaskManager assigned as master, here master only means that this
             * TaskManager works in the same machine where the job started;
             * (due to limited resources)
             * trainingInfo is got locally;
             * so listen for synchronization ack from other TaskManager;
             * currently other TaskManager send msg:synchronized was dealt in
             * JobManager, so if this TaskManager was created by the JobManager,
             * */
            System.out.println("TaskManager-" + this.me + "(master) Run!");
            int numServers = serverInfoList.length;
            ServerInfo myinfo = serverInfoList[me];
            ArrayList<Thread> threadArrayList = new ArrayList<>();
            for(int i = 0; i < numServers; i++){
                if(i != me){
                    /*send Training info to peers*/
                    if(sendTrainingInfos[i] == null){
                        throw new NullPointerException();
                    }
                    int remoteId = i;
                    Thread sendTinfoThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new InitializerClient(myinfo.ip, serverInfoList[remoteId].ip, serverInfoList[remoteId].acqInfoListenPort, sendTrainingInfos[remoteId]).run();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    threadArrayList.add(sendTinfoThread);
                }
            }
            for(Thread st : threadArrayList){
                st.start();
            }
            for(Thread st : threadArrayList){
                st.join();
            }
        }
        else{
            /*TaskManager on different machine, thus needs to acquire training info.*/
            System.out.println("TaskManager-" + this.me + " Run!");
            assert (this.trainingInfo == null);
            Thread accTinfoThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerInfo myinfo = serverInfoList[me];
                    try {
                        new InitializerServer(myinfo.ip, myinfo.acqInfoListenPort, trainingInfo).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            accTinfoThread.start();
            accTinfoThread.join();
        }
        System.out.println("TaskManager-" + this.me + " Initialized!");
        new DDTask(this.trainingInfo, serverInfoList, me).run();
        System.out.println("TaskManager-" + this.me + " finished training!");
    }
}
