package CG;

import Foundation.*;
import Foundation.lossFunctions.Loss;
import Foundation.lossFunctions.MSELoss;

import java.awt.*;
import java.io.Serializable;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashMap;

public class ComputationalGraph implements Serializable {
    public Moudle DAG;

    public Node input;
    public Node label;
    public Loss loss;
    public HashMap<String, MultiVector> gradNameMap;
    public HashMap<String, Node> nodeNameMap;
    public HashMap<String, Node> leafNameMap;
    public HashMap<String, int[]> gradName2posInfo;
    public ComputationalGraph(){
        this.DAG = new Moudle();
    }
    public long totalGradNums = 0;
    public Node getInput() {
        return input;
    }

    public void setInput(Node input) {
        this.input = input;
    }

    public Node getLabel() {
        return label;
    }

    public void setLabel(Node label) {
        this.label = label;
    }

    public Node CGnode(MultiVector _t, boolean _trainable)
    {
        Node nd = new Node(_t);
        if(_trainable) nd.Trainable = true;
        DAG.addLeaf(nd);
        return nd;
    }

    public Node CGnode(double _v, boolean _trainable)
    {
        Node nd = new Node(new MultiVector(_v));
        if(_trainable) nd.Trainable = true;
        DAG.addLeaf(nd);
        return nd;
    }

    public Node add(Node ch1, Node ch2)
    {
        return DAG.addFrom(ch1, ch2, Calculation.ADD);
    }

    public Node add(Node ch1, double c)
    {
        Node ch2 = new Node(new MultiVector(c));
        DAG.addLeaf(ch2);
        return DAG.addFrom(ch1, ch2, Calculation.ADD);
    }

    public Node sub(Node ch1, Node ch2)
    {
        return DAG.addFrom(ch1, ch2, Calculation.SUB);
    }

    public Node sub(Node ch1, double c)
    {
        Node ch2 = new Node(new MultiVector(c));
        DAG.addLeaf(ch2);
        return DAG.addFrom(ch1, ch2, Calculation.SUB);
    }

    public Node mul(Node ch1, Node ch2)
    {
        return DAG.addFrom(ch1, ch2, Calculation.MUL);
    }

    public Node mul(Node ch1, double c)
    {
        Node ch2 = new Node(new MultiVector(c));
        DAG.addLeaf(ch2);
        return DAG.addFrom(ch1, ch2, Calculation.MUL);
    }

    public Node div(Node ch1, Node ch2)
    {
        return DAG.addFrom(ch1, ch2, Calculation.DIV);
    }

    public Node div(Node ch1, double c)
    {
        Node ch2 = new Node(new MultiVector(c));
        DAG.addLeaf(ch2);
        return DAG.addFrom(ch1, ch2, Calculation.DIV);
    }

    public Node matmul(Node ch1, Node ch2)
    {
        return DAG.addFrom(ch1, ch2, Calculation.MATMUL);
    }

    public Node sum(Node ch1, boolean retain_shape, int...axes)
    {
        SumNode sumNode = (SumNode) DAG.addFrom(ch1, Calculation.SUM);
        sumNode.realInit(retain_shape, axes);
        return sumNode;
    }


    public Node max(Node ch1, boolean retain_shape, int...axes)
    {
        MaxNode maxNode = (MaxNode) DAG.addFrom(ch1, Calculation.MAX);
        maxNode.realInit(retain_shape, axes);
        return maxNode;
    }

    public Node exp(Node ch1)
    {
        return DAG.addFrom(ch1, Calculation.EXP);
    }

    public Node sigmoid(Node ch1)
    {
        return DAG.addFrom(ch1, Calculation.SIGMOID);
    }


    public Node MSELoss(Node _p, Node _y)
    {
        MSELoss loss = (MSELoss) DAG.addFrom(_p, _y, Calculation.MSE_LOSS);
        this.loss = loss;
        return (Node)loss;
    }

    public void gatherInfo(){
        gatherNodeInfo();
        gatherGradsInfo();
    }

    private void gatherNodeInfo(){
        if(this.nodeNameMap == null){
            this.nodeNameMap = new HashMap<>();
        }
        if(this.leafNameMap == null){
            this.leafNameMap = new HashMap<>();
        }
        for(Node nd : this.DAG.nList){
            if(nd.isLeaf){
                if(!this.leafNameMap.containsKey(nd.Name)){
                    this.leafNameMap.put(nd.Name, nd);
                }
            }
            if(!this.nodeNameMap.containsKey(nd.Name)){
                this.nodeNameMap.put(nd.Name, nd);
            }
        }
    }

    private void gatherGradsInfo(){
        if(this.gradNameMap == null){
            this.gradNameMap = new HashMap<>();
        }
        if(this.gradName2posInfo == null){
            this.gradName2posInfo = new HashMap<>();
        }
        int pos = 0;
        for(Node leafNode: this.DAG.Leaves){
            if(leafNode.Trainable && !gradNameMap.containsKey(leafNode.Name)){
                this.gradNameMap.put(leafNode.Name, leafNode._grad);
                int[] posInfo = new int[2];
                posInfo[0] = pos;
                posInfo[1] = leafNode._grad.total_eNum;
                gradName2posInfo.put(leafNode.Name, posInfo);
                pos += leafNode._grad.total_eNum;
                this.totalGradNums += leafNode._grad.total_eNum;
            }
        }
    }
    public void setData(MultiVector X, MultiVector Y) throws Exception{
        if(this.input == null){
            throw new Exception("Must designate the input of the model!");
        }
        if(this.label == null){
            throw new Exception("Must designate the label of the model!");
        }
        this.input._tensor.set_with(X);
        this.label._tensor.set_with(Y);
    }
    public void transForward(){
        this.DAG.transForward();
    }
    public void transBack(){
        this.DAG._grad.set_ones();
        this.DAG.transBack();
    }
    public void updateParameters(double lr){
        int batchSize = this.input._tensor._shape.get(0);
        this.DAG._updateWith_Grad(lr / batchSize);
    }
}
