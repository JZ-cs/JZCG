package modelDistribute;

import operation.Node;
import operation.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MDTaskRunner {
    public MDTaskRunnerID mdTaskRunnerID;
    public MDServer[] mdServers;
    public HashMap<String, Node> nameMap;
    public ArrayList<Node> nodes;
    public HashSet<RemoteProxy> fakePredNodes;
    public HashMap<String, Integer> nodeSuccCuts;
    public HashMap<String, HashSet<Pair<MDTaskRunnerID, String>>> remoteFwdSendMp;


    public HashMap<Integer, ArrayList<Node>> fwdLevelTb;
    public HashMap<Integer, ArrayList<Node>> bkwdLevelTb;
    public HashMap<Node, AtomicInteger> outDegreeMp;
    public int topBkwdLevel;
    public int topFwdLevel;
    private ExecutorService executorService;
    public MDTaskRunner(MDTaskRunnerID mdTaskRunnerID,
                        MDServer[] mdServers,
                        ArrayList<Node> nodes,
                        HashSet<RemoteProxy> fakePredNodes,
                        HashMap<String, Integer> nodeSuccCuts,
                        HashMap<String, HashSet<Pair<MDTaskRunnerID, String>>> remoteFwdSendMp
                  ){
        this.mdTaskRunnerID = mdTaskRunnerID;
        this.mdServers = mdServers;
        this.nodeSuccCuts = nodeSuccCuts;
        this.nodes = new ArrayList<>();
        this.nameMap = new HashMap<>();
        for(Node node : nodes){
            this.nodes.add(node);
            this.nameMap.put(node.getName(), node);
        }

        this.fakePredNodes = new HashSet<>();
        for(RemoteProxy remoteProxy : fakePredNodes){
            this.fakePredNodes.add(remoteProxy);
            this.nameMap.put(remoteProxy.getName(), remoteProxy);
        }
        this.remoteFwdSendMp = remoteFwdSendMp;
        accquireLevelInfo();
    }


    private void accquireLevelInfo() {
        //forward level info
        LinkedList<Node> fq = new LinkedList<Node>();
        fwdLevelTb = new HashMap<>();
        for(Node pnode : this.fakePredNodes){
            fq.addLast(pnode);
        }
        int flevel = 0;
        while(!fq.isEmpty()){
            int fqsize = fq.size();
            while(fqsize > 0){
                Node front = fq.poll();
                if(front != null){
                    front.ind = front.inDegree;// reset ind;
                    if(!fwdLevelTb.containsKey(flevel)){
                        ArrayList<Node> levelList = new ArrayList<>();
                        fwdLevelTb.put(flevel, levelList);
                    }
                    fwdLevelTb.get(flevel).add(front);
                    fqsize--;
                    for(Node succ : front.succList){
                        succ.ind--;
                        if(succ.ind == 0){
                            fq.addLast(succ);
                        }
                    }
                }
                else break;
            }
            flevel++;
        }
        this.topFwdLevel = flevel - 1;
        LinkedList<Node> bq = new LinkedList<Node>();
        bkwdLevelTb = new HashMap<>();
        for(Map.Entry<String, Integer> entry : nodeSuccCuts.entrySet()){
            this.nameMap.get(entry.getKey()).outd -= entry.getValue();
        }
        for(Node node : this.nodes){
            if(node.outd == 0){
                bq.addLast(node);
            }
        }
        int blevel = 0;
        while(!bq.isEmpty()){
            int bqsize = bq.size();
            while(bqsize > 0){
                Node front = bq.poll();
                if(front != null){
                    front.outd = front.outDegree;
                    if(!bkwdLevelTb.containsKey(blevel)){
                        ArrayList<Node> levelList = new ArrayList<>();
                        bkwdLevelTb.put(flevel, levelList);
                    }
                    bkwdLevelTb.get(flevel).add(front);
                    bqsize--;
                    if(front.Preds != null){
                        throw new RuntimeException("Right now after model split every node should NOT be a moudle, meaning Preds == null!");
                    }
                    if(front.pred[0] != null){
                        front.pred[0].outd--;
                        if(front.pred[0].outd == 0){
                            bq.addLast(front.pred[0]);
                        }
                    }
                    if(front.pred[1] != null){
                        front.pred[1].outd--;
                        if(front.pred[1].outd == 0){
                            bq.addLast(front.pred[1]);
                        }
                    }
                }

            }
            blevel++;
        }
        this.topBkwdLevel = blevel - 1;
    }

    public void runForward(int epoch, int bNum){

    }

    public void runBackward(int epoch, int bNum){

    }
    public void initialLizeExecutePools(int nThreads){
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    public class fwdTaskSubmiter implements Runnable{
        public int level;
        public long sleepMillisecs;
        public HashSet<Node> waitNodeSet;
        public fwdTaskSubmiter(int level, long sleepMillisecs){
            this.level = level;
            this.sleepMillisecs = sleepMillisecs;
            for(Node node : fwdLevelTb.get(this.level)){
                waitNodeSet.add(node);
            }
        }
        @Override
        public void run() {
            if(this.level <= topFwdLevel)
            {
                while(!this.waitNodeSet.isEmpty()){
                    for(Node node : this.waitNodeSet){
                        if(node.ind == 0){
                            executorService.submit(new fwdTask(node));
                            this.waitNodeSet.remove(node);
                        }
                    }
                    try {
                        Thread.sleep(this.sleepMillisecs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class fwdTask implements Runnable{
        public Node runNode;
        public fwdTask(Node runNode){
            this.runNode = runNode;
        }
        @Override
        public void run() {
            runNode.transForward();
        }
    }

    public class bkwdTaskSubmiter implements Runnable{
        public int level;
        public long sleepMillisecs;
        public HashSet<Node> waitNodeSet;
        public bkwdTaskSubmiter(int level, long sleepMillisecs){
            this.level = level;
            this.sleepMillisecs = sleepMillisecs;
            for(Node node : bkwdLevelTb.get(this.level)){
                waitNodeSet.add(node);
            }
        }
        @Override
        public void run() {
            if(this.level <= topBkwdLevel){
                while(!this.waitNodeSet.isEmpty()){
                    for(Node node : this.waitNodeSet){
                        if(node.outd == 0){
                            executorService.submit(new bkwdTask(node));
                            this.waitNodeSet.remove(node);
                        }
                    }
                    try {
                        Thread.sleep(this.sleepMillisecs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class bkwdTask implements Runnable{
        public Node runNode;
        public bkwdTask(Node runNode){
            this.runNode = runNode;
        }

        @Override
        public void run() {
            runNode.transBack();
        }
    }

    public enum FwdResultPusher{
        INSTANCE;
        public HashMap<MDTaskRunnerID, RSendPackage> sendPackages;
        FwdResultPusher() {

        }
        public static void build(HashMap<String, ArrayList<Pair<MDTaskRunnerID, String>>> remoteFwdSendMp){
            HashMap<MDTaskRunnerID, HashSet<String>> mapper = new HashMap<>();
            for(String nodeName : remoteFwdSendMp.keySet()){
                ArrayList<Pair<MDTaskRunnerID, String>> sendPairs = remoteFwdSendMp.get(nodeName);
                for(Pair<MDTaskRunnerID, String> sendpair : sendPairs){
                    if(!mapper.containsKey(sendpair.first)){
                        mapper.put(sendpair.first, new HashSet<>());
                    }
                    mapper.get(sendpair.first).add(sendpair.second);
                }
            }
            INSTANCE.sendPackages = new HashMap<>();
            for(Map.Entry<MDTaskRunnerID, HashSet<String>> entry : mapper.entrySet()){
                HashMap<String, Integer> degreeDec = new HashMap<>();
                for(String nodeName : entry.getValue()){
                    degreeDec.put(nodeName, 1);
                }
                int port = entry.getKey().mdServer.portMp.get(MDServer.FORWARD_RESULT_LISTEN_PORT);
                INSTANCE.sendPackages.put(entry.getKey(), new RSendPackage(entry.getKey(), port, entry.getValue().size(), degreeDec));
            }
        }

        public void addData(MDTaskRunnerID mdTaskRunnerID, String remoteNodeName, double[] data){
            RSendPackage fwdSendPackage = this.sendPackages.get(mdTaskRunnerID);
            fwdSendPackage.setData(remoteNodeName, data);
            if(fwdSendPackage.isComplete()){
                //send result package;
            }
        }

    }

    public class BkwdGradPusher{
        public HashMap<MDTaskRunnerID, RSendPackage> sendPackages;
        public BkwdGradPusher(ArrayList<RemoteProxy> fakePredNodes){
            HashMap<MDTaskRunnerID, HashSet<String>> mapper = new HashMap<>();
            HashMap<String, Integer> totalDegreeDec = new HashMap<>();
            for(RemoteProxy remoteProxy : fakePredNodes){
                MDTaskRunnerID mdTaskRunnerID = remoteProxy.remoteMDTaskRunnerID;
                String remoteName = remoteProxy.remoteName;
                if(!mapper.containsKey(mdTaskRunnerID)){
                    mapper.put(mdTaskRunnerID, new HashSet<>());
                }
                mapper.get(mdTaskRunnerID).add(remoteName);
                totalDegreeDec.put(remoteName, remoteProxy.succList.size());

            }
            for(Map.Entry<MDTaskRunnerID, HashSet<String>> entry : mapper.entrySet()){
                HashMap<String, Integer> degreeDec = new HashMap<>();
                for(String nodeName : entry.getValue()){
                    degreeDec.put(nodeName, totalDegreeDec.get(nodeName));
                }
                int port = entry.getKey().mdServer.portMp.get(MDServer.BACKWARD_GRAD_LISTEN_PORT);
                this.sendPackages.put(entry.getKey(), new RSendPackage(entry.getKey(), port, entry.getValue().size(), degreeDec));
            }
        }

        public void addData(MDTaskRunnerID mdTaskRunnerID, String remoteNodeName, double[] data){
            RSendPackage bkwdSendPackage = this.sendPackages.get(mdTaskRunnerID);
            bkwdSendPackage.setData(remoteNodeName, data);
            if(bkwdSendPackage.isComplete()){
                //send grad package;
            }
        }
    }
}
