package TestCGandMultiVector;

import dataDistribute.utils.ServerInfo;

import java.util.HashSet;

public class testGenServerInfos {
    public static void main(String[] args) {
        String[] ips = new String[]{"111","222"};
        ServerInfo[] serverInfos = ServerInfo.generateRandomServerInfosForIps(ips);
        for(ServerInfo serverInfo : serverInfos){
            System.out.println(serverInfo);
        }
    }
}
