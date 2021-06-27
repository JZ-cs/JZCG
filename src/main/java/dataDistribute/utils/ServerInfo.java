package dataDistribute.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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

    public static ServerInfo[] generateRandomServerInfosForIps(String[] ips){
        int numServers = ips.length;
        ServerInfo[] serverInfos = new ServerInfo[numServers];
        int totalPorts = numServers * 3;
        HashSet<Integer> portsSet = new HashSet<>();
        Random rand = new Random();
        while(portsSet.size() < totalPorts){
            int randomPort = rand.nextInt(40000) + 15000;
            portsSet.add(randomPort);
        }
        List<Integer> ports = new ArrayList<>(portsSet);
        for(int i = 0; i < numServers; i++){
            serverInfos[i] = new ServerInfo(ips[i], ports.get(i * 3), ports.get(i * 3 + 1),
                    ports.get(i * 3 + 2));
        }
        return serverInfos;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "ip='" + ip + '\'' +
                ", gradListenPort=" + gradListenPort +
                ", acqInfoListenPort=" + acqInfoListenPort +
                ", syncAckListenPort=" + syncAckListenPort +
                '}';
    }
}
