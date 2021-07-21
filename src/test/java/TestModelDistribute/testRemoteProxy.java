package TestModelDistribute;

import operation.MultiVector;
import operation.Node;
import operation.Pair;
import modelDistribute.MDServer;
import modelDistribute.MDTaskRunnerID;
import modelDistribute.RemoteProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class testRemoteProxy {
    public static void main(String[] args) {
        int[] dims1 = {1,2,3};
        int[] dims2 = {3,2,3};
        Node x = new Node(new MultiVector(dims1));
        Node y = new Node(new MultiVector(dims2));
        MDTaskRunnerID[] mdTaskRunnerIDS = new MDTaskRunnerID[2];
        mdTaskRunnerIDS[0] = new MDTaskRunnerID(new MDServer("11111", new HashMap<>()));
        mdTaskRunnerIDS[1] = new MDTaskRunnerID(new MDServer("11111", new HashMap<>()));
        System.out.println(mdTaskRunnerIDS[0].uuid);
        System.out.println(mdTaskRunnerIDS[1].uuid);
        HashSet<RemoteProxy> remoteProxies = new HashSet<>();
        RemoteProxy r1 = new RemoteProxy(x, mdTaskRunnerIDS[0], mdTaskRunnerIDS[1]);
        RemoteProxy r2 = new RemoteProxy(x, mdTaskRunnerIDS[0], mdTaskRunnerIDS[1]);
        RemoteProxy r3 = new RemoteProxy(x, mdTaskRunnerIDS[1], mdTaskRunnerIDS[0]);
        remoteProxies.add(r1);
        remoteProxies.add(r2);
        remoteProxies.add(r3);
        System.out.println(remoteProxies.size());

        Pair<MDTaskRunnerID, String> p1 = Pair.make_pair(mdTaskRunnerIDS[0],"xxxxx");
        Pair<MDTaskRunnerID, String> p2 = Pair.make_pair(mdTaskRunnerIDS[0],"xxxxx");
        System.out.println(p1.hashCode() + " -- " + p2.hashCode());
        System.out.println(p1.equals(p2));

        ArrayList<Node> arrayList = new ArrayList<>();
        arrayList.add(x);
        arrayList.add(y);
        arrayList.remove(x);
        System.out.println(arrayList.size());
    }
}
