package modelDistribute.utils;

import operation.Moudle;
import operation.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class ModelSplits {
    public static void unpackSubMoudles(Moudle DAG){
        HashSet<Node> newMembers = new HashSet<>();
        for(Node node : DAG.nList){
            if(node.isMoudle){
                unpackMoudle((Moudle) node, newMembers);
            }
            else{
                int outdDec = 0;
                for(Node succ : node.succList){
                    if(succ.isMoudle) outdDec++;
                }
                node.succList.removeIf(succ -> succ.isMoudle);
                node.outDegree -= outdDec;
            }
        }
        DAG.nList.removeIf(node -> node.isMoudle);
        DAG.nList.addAll(newMembers);
        DAG.nMap.clear();
        DAG.Leaves.clear();
        int nodeCount = 0;
        for(Node node : DAG.nList){
            node.outd = node.outDegree;
            DAG.nMap.put(node, nodeCount++);
            if(node.isLeaf) DAG.Leaves.add(node);
        }
    }
    public static void unpackMoudle(Moudle moudle, HashSet<Node> newMembers){
        Node output = moudle._output;
        for(Node succ : moudle.succList){
            if(!succ.isMoudle){
                output.succList.add(succ);
                output.outDegree += 1;
                if(succ.pred[0] == moudle) succ.pred[0] = output;
                if(succ.pred[1] == moudle) succ.pred[1] = output;
            }
        }
        newMembers.add(output);
        for(Node node : moudle.nList){
            if(node.isMoudle){
                unpackMoudle((Moudle)node, newMembers);
            }
            else{
                int outdDec = 0;
                for(Node succ : node.succList){
                    if(succ.isMoudle) outdDec++;
                }
                node.succList.removeIf(succ -> succ.isMoudle);
                node.outDegree -= outdDec;
                newMembers.add(node);
            }
        }
    }
}
