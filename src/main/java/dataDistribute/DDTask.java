package dataDistribute;

import CG.ComputationalGraph;
import dataDistribute.utils.GenPartitionInfo;
import dataDistribute.utils.GradPartition;
import dataDistribute.utils.GradPartitionMatrix;
import dataDistribute.utils.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GradSeq;
import utils.TrainingInfo;

import java.util.Map;

public class DDTask implements Runnable{
    public TrainingInfo trainingInfo;
    public ServerInfo[] serverInfoList;
    public int[][] PartitionInfos;
    public static final Logger log = LoggerFactory.getLogger(DDTask.class);
    public int numServers;
    public int me;

    public DDTask(TrainingInfo trainingInfo, ServerInfo[] serverInfoList, int me){
        this.trainingInfo = trainingInfo;
        this.serverInfoList = serverInfoList;
        this.me = me;
        this.numServers = serverInfoList.length;
        try{
            this.PartitionInfos = GenPartitionInfo.genSeqPartitionInfo(this.numServers, (int)trainingInfo.CG.totalGradNums);
        }catch (Exception e){
            log.error("Error happens when generate partition infos!");
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        /*initialize all the servers*/

        ComputationalGraph CG = this.trainingInfo.CG;
        int batchSize = this.trainingInfo.batchSize;
        int batches = this.trainingInfo.batches;
        int epoches = this.trainingInfo.epoches;
        double lr = this.trainingInfo.lr;
        CG.gatherInfo();
        System.out.println("Now run DDTask-" + this.me);
        for(int ep = 0; ep < epoches; ep++){
            for(int b = 0; b < batches; b++){
                /*1.local transforward and transback, calculate the grads for current server*/
//                CG.input._tensor.set_with(trainingInfo.X[b]);
//                CG.label._tensor.set_with(trainingInfo.Y[b]);
//                CG.DAG.transForward();
//                CG.DAG._grad.set_ones();
//                CG.DAG.transBack();
                try {
                    CG.setData(trainingInfo.X[b], trainingInfo.Y[b]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CG.transForward();
                CG.transBack();
                System.out.printf("DDTask-%d epoch:%d, batch:%d local calculate finish! %n", this.me, ep, b);
                /*2.grad exchange, fill with other servers' grads*/
                int parts = this.numServers;
                int[][] pinfos = this.PartitionInfos;
                //(1).prepare for grad exchange
                double[] gbuff = new double[(int)CG.totalGradNums];
                //(2).copy grads to gbuff
                for(Map.Entry<String, int[]> entry : CG.gradName2posInfo.entrySet()){
                    int p = entry.getValue()[0];
                    int len = entry.getValue()[1];
                    String gname = entry.getKey();
                    System.arraycopy(CG.gradNameMap.get(gname)._data, 0, gbuff, p, len);
                }
                //(3).setup gPartitions for server grads exchange
                GradPartition gp = new GradPartition(parts, pinfos, gbuff);
                /*3.exchange grads with others now!*/
                Thread gradExcger = new Thread(new GradExchanger(this.me, gp, this.serverInfoList));
                gradExcger.start();
                try {
                    gradExcger.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error(String.format("Error happens when exchanging grads in epoch: %d, batch: %d", ep, b));
                }
                /*4.set gradSeq back to gbuff*/
                for(int p = 0; p < parts; p++){
                    System.arraycopy(gp.gradSeqs[p].grads, 0, gbuff, pinfos[p][0], pinfos[p][1]);
                }

                /*5.copy gbuff back to grads in CG*/
                for(Map.Entry<String, int[]> entry : CG.gradName2posInfo.entrySet()){
                    int p = entry.getValue()[0];
                    int len = entry.getValue()[1];
                    String gname = entry.getKey();
                    System.arraycopy(gbuff, p, CG.gradNameMap.get(gname)._data, 0, len);
                }
                /*6.update weights with their grads*/
//                CG.DAG._updateWith_Grad(lr * (1.0 / batchSize));
                CG.updateParameters(lr);
                System.out.printf("DDTask-%d epoch:%d, batch:%d Done! %n", this.me, ep, b);
            }
        }
        log.info("Trainning over!");
    }
}
