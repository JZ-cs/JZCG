package dataDistribute.utils;

import java.io.Serializable;
import java.util.ArrayList;

/*simple info discribes a server*/
public class ServerInfo implements Serializable {
    public String ip;
    public int gradListenPort;
    public int acqInfoListenPort;
    public int syncAckListenPort;
    public ServerInfo(String ip, int acqInfoListenPort, int syncAckListenPort, int gradListenPort){
        this.ip = ip;
        this.acqInfoListenPort = acqInfoListenPort;
        this.syncAckListenPort = syncAckListenPort;
        this.gradListenPort = gradListenPort;
    }
}
