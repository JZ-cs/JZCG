package dataDistribute;

import dataDistribute.utils.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TrainingInfo;

/*DD stands for "data dstributed"*/
public class DDJob {
    public static final Logger log = LoggerFactory.getLogger(DDJob.class);
    public TrainingInfo metaTrainingInfo;
    public ServerInfo[] serverInfoList;
    DDJob(TrainingInfo metaTrainingInfo, ServerInfo[] serverInfoList){
        this.metaTrainingInfo = metaTrainingInfo;
        this.serverInfoList = serverInfoList;
    }

    public void runJob(){
        int numServers = serverInfoList.length;
    }
}
