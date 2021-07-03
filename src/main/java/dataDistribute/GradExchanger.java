package dataDistribute;

import dataDistribute.utils.GradPartition;
import dataDistribute.utils.GradPartitionMatrix;
import dataDistribute.utils.ServerInfo;
import network.netty.GradPackage;
import network.netty.gradTransfer.client.GradTransferClient;
import network.netty.gradTransfer.server.GradTransferServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GradSeq;

public class GradExchanger implements Runnable{
    public static final Logger log = LoggerFactory.getLogger(GradExchanger.class);
//    GradPartitionMatrix gpm;
    GradPartition gp;
    public int me;
    public ServerInfo[] serverList;
    public GradExchanger(int me, GradPartition gp, ServerInfo[] serverList){
        this.gp = gp;
        this.serverList = serverList;
        this.me = me;
    }
    @Override
    public void run() {
        int n = gp.parts;
        int x = n;
        /* first n - 1 turns for grad exchange, in each turn, each server would pass
        * its gpm.gPartitions[(i + x) % n] to the next server, here next meaning the
        * server of id (i + 1) % n, in the shape of ring.
        * after first n - 1 turns, each server has one grad partition has all the parts
        * of other servers.
        *
        * during the second n - 1 turns, at each turn each server will pass
        * its gpm.gPartitions[(i + x) % n] to next server. This time,
        * the partition has all parts from other server, thus each exchange would
        * fullfill one partition for each server.
        *
        * To simplify the procedure, every time just send the whole partition,
        * here, partition i has the i'th part of grads of all the servers,
        * some of them may be null, so when receiving the partition i
        * from previous server to set its own, only when the
        * corresponding server part is null and the same server part in partition i
        * is NOT null, then copy the data to set its own.*/

        //add phase
        for(int t = 0; t < (n - 1); t++){
            int pid = (this.me + x) % n;
            int nextServerId = (this.me + 1) % n;
            GradPackage gradPackage = new GradPackage(this.gp.gradSeqs[pid], pid, GradPackage.ADD);
            String myIp = serverList[this.me].ip;
            int myListenPort = serverList[this.me].gradListenPort;
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new GradTransferServer(myIp, myListenPort, gp).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            String nextServerIp = this.serverList[nextServerId].ip;
            int nextServerListenPort = this.serverList[nextServerId].gradListenPort;
            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new GradTransferClient(myIp, nextServerIp, nextServerListenPort, gradPackage).run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            listenThread.start();
            sendThread.start();
            try {
                listenThread.join();
                sendThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(String.format("Err in waiting the grad exchange finish in turn: %d", t));
            }
            System.out.printf("%d-grad exchange completed: %.2f%%%n", this.me, 100.0 * (t + 1) / (2 * (n - 1)));
            x--;
            if(x == -1) x = n - 1;
        }
        //set phase
        for(int t = 0; t < (n - 1); t++){
            int pid = (this.me + x) % n;
            int nextServerId = (this.me + 1) % n;
            GradPackage gradPackage = new GradPackage(this.gp.gradSeqs[pid], pid, GradPackage.SET);
            String myIp = serverList[this.me].ip;
            int myListenPort = serverList[this.me].gradListenPort;
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new GradTransferServer(myIp, myListenPort, gp).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            String nextServerIp = this.serverList[nextServerId].ip;
            int nextServerListenPort = this.serverList[nextServerId].gradListenPort;
            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new GradTransferClient(myIp, nextServerIp, nextServerListenPort, gradPackage).run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            listenThread.start();
            sendThread.start();
            try {
                listenThread.join();
                sendThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(String.format("Err in waiting the grad exchange finish in turn: %d", t));
            }
            System.out.printf("%d-grad exchange completed: %.2f%%%n", this.me, 100.0 * (t + n) / (2 * (n - 1)));
            x--;
            if(x == -1) x = n - 1;
        }
//        for(int t = 0; t < 2 * (n - 1); t++){
//            int pid = (this.me + x) % n;
//            int nextServerId = (this.me + 1) % n;
//            GradPackage gradPackage = new GradPackage(this.gpm.gPartitions[pid], pid);
//            String myIp = serverList[this.me].ip;
//            int myListenPort = serverList[this.me].gradListenPort;
//            Thread listenThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        new GradTransferServer(myIp, myListenPort, gpm).run();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            String nextServerIp = this.serverList[nextServerId].ip;
//            int nextServerListenPort = this.serverList[nextServerId].gradListenPort;
//            Thread sendThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        new GradTransferClient(myIp, nextServerIp, nextServerListenPort, gradPackage).run();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            listenThread.start();
//            sendThread.start();
//            try {
//                listenThread.join();
//                sendThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                log.error(String.format("Err in waiting the grad exchange finish in turn: %d", t));
//            }
//            System.out.printf("%d-grad exchange completed: %.2f%%%n", this.me, 100.0 * (t + 1) / (2 * (n - 1)));
//            x--;
//            if(x == -1) x = n - 1;
//        }
    }
}
