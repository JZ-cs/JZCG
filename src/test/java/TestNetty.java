import dataDistribute.utils.GenPartitionInfo;
import dataDistribute.utils.GradPartitionMatrix;
import network.netty.client.GradTransferClient;
import network.netty.server.GradTransferServer;
import network.netty.GradPackage;

public class TestNetty {
    public static void main(String[] args) throws Exception {
        int port1 = 19999;
        int port2 = 29999;
        double[] gbuff1 = new double[10000000];
        for(int i = 0; i < gbuff1.length; i++) gbuff1[i] = 2;
        double[] gbuff2 = new double[10000000];
        for(int i = 0; i < gbuff2.length; i++) gbuff2[i] = 2;
        int parts = 2;
        int[][] pinfos = GenPartitionInfo.genSeqPartitionInfo(2, gbuff1.length);
        GradPartitionMatrix gpm1 = new GradPartitionMatrix(parts, pinfos, gbuff1, 0);
        GradTransferServer gradTransferServer1 = new GradTransferServer("10.10.10.0", port1, gpm1);
        Thread listenThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gradTransferServer1.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        GradPartitionMatrix gpm2 = new GradPartitionMatrix(parts, pinfos, gbuff2, 1);
        GradTransferServer gradTransferServer2 = new GradTransferServer("10.10.10.9", port2, gpm2);
        Thread listenThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gradTransferServer2.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        GradPackage gradPackage1 = GradPackage.generateGradPackage(2, 1, pinfos[1][1], false);
        gradPackage1.partitionId = 1;
        String host = "localhost";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new GradTransferClient("10.10.10.0", host, port2, gradPackage1).run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        GradPackage gradPackage2 = GradPackage.generateGradPackage(2, 0, pinfos[0][1], false);
        gradPackage2.partitionId = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new GradTransferClient("10.10.10.9", host, port1, gradPackage1).run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(3000);
        listenThread1.start();
        listenThread2.start();
        listenThread1.join();
        listenThread2.join();
        System.out.println(gpm1.getAllPartsSum()[0]);
        System.out.println(gpm2.getAllPartsSum()[0]);
    }
}
