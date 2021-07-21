package modelDistribute;

import CG.ComputationalGraph;
import operation.Node;
import operation.Pair;
import utils.TrainingInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MDJobManager {
    public TrainingInfo trainingInfo;
    public MDServer[] mdServers;
    public MDJobManager(TrainingInfo trainingInfo, MDServer[] mdServers){
        this.trainingInfo = trainingInfo;
        this.mdServers = mdServers;
    }


    /*based on md servers to make assignments*/
    public TaskBuildInfo[] taskAssign(int splits){
        long totalCosts = 0;
        int n = splits;
        ComputationalGraph cg = this.trainingInfo.CG;
        for(Node node : cg.DAG.nList){
            totalCosts += node.getCost();
        }

        /*1.calculate level for each node*/
        ArrayList<ArrayList<Node>> levelNodes = new ArrayList<>();
        ArrayList<Long> levelCosts = new ArrayList<>();
        HashMap<Node, Integer> nodeLevelMap = new HashMap<>();
        LinkedList<Node> lq = new LinkedList<>();
        for(Node node : cg.DAG.nList){
            if(node.ind == 0){
                lq.addLast(node);
            }
        }
        int levels = 0;
        while(!lq.isEmpty()){
            int lqsize = lq.size();
            ArrayList<Node> arr = new ArrayList<>();
            long lc = 0;
            while(lqsize > 0){
                Node front = lq.poll();
                if(front == null) break;
                nodeLevelMap.put(front, levels);
                arr.add(front);
                lc += front.getCost();
                front.ind = front.inDegree;
                lqsize--;
                for(Node succ : front.succList){
                    succ.ind--;
                    if(succ.ind == 0){
                        lq.addLast(succ);
                    }
                }
            }
            levelCosts.add(lc);
            levelNodes.add(arr);
            levels++;
        }
        Long[] curLevelCosts = new Long[levels];

        /*2. rearrange the position for each node in same level */
        for(int l = 0; l < levels; l++){
            ArrayList<Node> nodes = levelNodes.get(l);
            HashSet<Node> fixed;
        }




        /*2.1 decide which Task the node belongs to*/
        HashMap<Node, Integer> assignMap = new HashMap<>();

        /*3. use the assign map to build task info*/
        TaskBuildInfo[] taskBuildInfos = new TaskBuildInfo[n];
        MDTaskRunnerID[] mdTaskRunnerIDS = new MDTaskRunnerID[n];
        for(int i = 0; i < n; i++){
            mdTaskRunnerIDS[i] = new MDTaskRunnerID(mdServers[i]);
        }
        /*3-1. accquire preds and succs that are not in the same task*/
        HashMap<Node, HashSet<Node>> globalPredNotIn = new HashMap<>();
        HashMap<Node, HashSet<Node>> globalSuccNotIn = new HashMap<>();
        for(Node node : assignMap.keySet()){
            int belongs = assignMap.get(node);
            for(Node succ : node.succList){
                if(!assignMap.get(succ).equals(belongs)){
                    if(!globalSuccNotIn.containsKey(node)){
                        globalPredNotIn.put(node, new HashSet<>());
                    }
                    globalSuccNotIn.get(node).add(succ);
                }
            }
            if(node.Preds == null){
                if(node.pred[0] != null){
                    if(!assignMap.get(node.pred[0]).equals(belongs)){
                        if(!globalPredNotIn.containsKey(node)){
                            globalPredNotIn.put(node, new HashSet<>());
                        }
                        globalPredNotIn.get(node).add(node.pred[0]);
                    }
                }
                if(node.pred[1] != null){
                    if(!assignMap.get(node.pred[1]).equals(belongs)){
                        if(!globalPredNotIn.containsKey(node)){
                            globalPredNotIn.put(node, new HashSet<>());
                        }
                        globalPredNotIn.get(node).add(node.pred[1]);
                    }
                }
            }
            else {
                for(Node input : node.Preds){
                    if(!assignMap.get(input).equals(belongs)){
                        if(!globalPredNotIn.containsKey(node)){
                            globalPredNotIn.put(node, new HashSet<>());
                        }
                        globalPredNotIn.get(node).add(input);
                    }
                }
            }
        }
        HashMap<Node, HashSet<RemoteProxy>> globalNode2Proxies = new HashMap<>();
        HashMap<RemoteProxy, Node> globalProxy2Node = new HashMap<>();
        /*3-2
        * build mdTaskRunnerID, mdServers, dnodes(in the same task), fakePredNodes,
        * nodeSuccCuts for each task and prepare info for the build of remoteFwdSendMp.
        * */
        for(int i = 0; i < n; i++){
            TaskBuildInfo taskBuildInfo = new TaskBuildInfo();
            taskBuildInfo.mdTaskRunnerID = mdTaskRunnerIDS[i];
            taskBuildInfo.mdServers = this.mdServers;
            ArrayList<Node> nodes = new ArrayList<>();
            HashSet<RemoteProxy> fakePredNodes = new HashSet<>();
            HashMap<String, Integer> nodeSuccCuts = new HashMap<>();
            HashSet<Node> remotePreds = new HashSet<>();
            for(Node node : assignMap.keySet()){
                if(assignMap.get(node).equals(i)){
                    nodes.add(node);
                    remotePreds.addAll(globalPredNotIn.get(node));
                }

                if(globalSuccNotIn.containsKey(node)){
                    nodeSuccCuts.put(node.getName(), globalPredNotIn.get(node).size());
                }
                else nodeSuccCuts.put(node.getName(), 0);
            }
            for(Node rPred : remotePreds){
                int rBelongs = assignMap.get(rPred);
                RemoteProxy rproxy = new RemoteProxy(rPred, mdTaskRunnerIDS[rBelongs], mdTaskRunnerIDS[i]);
                fakePredNodes.add(rproxy);
                globalProxy2Node.put(rproxy, rPred);
                if(!globalNode2Proxies.containsKey(rPred)){
                    globalNode2Proxies.put(rPred, new HashSet<>());
                }
                globalNode2Proxies.get(rPred).add(rproxy);
            }

            taskBuildInfo.nodes = nodes;
            taskBuildInfo.fakePredNodes = fakePredNodes;
            taskBuildInfo.nodeSuccCuts = nodeSuccCuts;
            taskBuildInfos[i] = taskBuildInfo;
        }
        /*3-3
         * build remoteFwdSendMp for each task.
         * */
        for(int i = 0; i < n; i++){
            HashMap<String, HashSet<Pair<MDTaskRunnerID, String>>> remoteFwdSendMp = new HashMap<>();
            for(Node node : taskBuildInfos[i].nodes){
                if(globalNode2Proxies.containsKey(node)){
                    String key = node.getName();
                    HashSet<Pair<MDTaskRunnerID, String>> fwdSend = new HashSet<>();
                    for(RemoteProxy rp : globalNode2Proxies.get(node)){
                        fwdSend.add(Pair.make_pair(rp.locatedMDTaskRunnerID, rp.getName()));
                    }
                    remoteFwdSendMp.put(key, fwdSend);
                }
            }
            taskBuildInfos[i].remoteFwdSendMp = remoteFwdSendMp;
        }

        /*4. rewiring connection in the same task
        * (1).set preds in remote to remote proxy;
        * (2).delete succs not in the same task.
        * */
        for(int i = 0; i < n; i++){
            for(Node node : taskBuildInfos[i].nodes){
                ArrayList<Node> newSuccList = new ArrayList<>();
                for(Node succ : node.succList){
                    if(assignMap.get(succ).equals(i)){
                        newSuccList.add(succ);
                    }
                }
                if(node.Preds == null){
                    if(node.pred[0] != null){
                        for(RemoteProxy remoteProxy : taskBuildInfos[i].fakePredNodes){
                            if(remoteProxy.remoteName.equals(node.pred[0].getName())){
                                node.pred[0] = remoteProxy;
                                break;
                            }
                        }
                    }
                    if(node.pred[1] != null){
                        for(RemoteProxy remoteProxy : taskBuildInfos[i].fakePredNodes){
                            if(remoteProxy.remoteName.equals(node.pred[1].getName())){
                                node.pred[1] = remoteProxy;
                                break;
                            }
                        }
                    }
                }
                else{
                    
                }
            }
        }
        return taskBuildInfos;
    }
    public class TaskBuildInfo{
        public MDTaskRunnerID mdTaskRunnerID;
        public MDServer[] mdServers;
        public ArrayList<Node> nodes;
        public HashSet<RemoteProxy> fakePredNodes;
        public HashMap<String, Integer> nodeSuccCuts;
        public HashMap<String, HashSet<Pair<MDTaskRunnerID, String>>> remoteFwdSendMp;
    }
}
