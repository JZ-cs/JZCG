package utils;

import operation.Moudle;
import operation.Node;

import java.util.Collection;
import java.util.HashSet;

public class GraphTravels {
    public static void travelForLeaves(Collection<Node> nodes, HashSet<Node> leaves){
        for(Node node : nodes){
            subTravel(node, leaves);
        }
    }
    public static void subTravel(Node node, HashSet<Node> leaves){
        if(node.isLeaf){
            leaves.add(node);
        }
        else if(node instanceof Moudle){
            Moudle md = (Moudle) node;
            for(Node subNode : md.nList){
                subTravel(subNode, leaves);
            }
        }
    }
}
