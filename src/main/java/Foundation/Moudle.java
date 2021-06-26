package Foundation;

import Foundation.functionNodes.ExpNode;
import Foundation.functionNodes.SigmoidNode;
import Foundation.lossFunctions.MSELoss;

import java.util.*;

public class Moudle extends Node{
    public int _nodesNum = 0;
    public int _edgesNum = 0;
    public int _totalweights = 0;
    public boolean insideConnected = false;
    private boolean initialized = false;
    /*output is the gate connecting this DAG to others and its inside parts,
    * typically, output is one of the Node in DAG, when adding a node into
    * DAG, the output is always updated by that node, to keep tracking of the
    * last node.
    *
    */
    public Node _output;
    public ArrayList<Node> nList = new ArrayList<Node>();
    public ArrayList<Node> Leaves = new ArrayList<Node>();
    public HashMap<Node, Integer> nMap = new HashMap<Node, Integer>(); //mapping Node address to id in nList;

    //preds(AKA the inputs) should not be added into the DAG!!!
    public Moudle(){
        this.Name = "Moudle-" + this.id;
        this.isMoudle = true;
        this.Preds = new HashSet<>();
        this.isLeaf = false;
        this.Trainable = true;
        this._tensor = null;
        this._grad = null;
    }
    public Moudle(Node... _inputs)
    {
        this();
        for(Node _inp : _inputs)
        {
            this.Preds.add(_inp);
            this.inDegree++;
            this.ind++;
            _inp.outDegree++;
            _inp.outd++;
            _inp.succList.add(this);
        }
    }
    public int addInputs(Node... _inputs){
        for(Node _inp : _inputs){
            if(!this.Preds.contains(_inp)){
                this.Preds.add(_inp);
                this.inDegree++;
                this.ind++;
                _inp.outDegree++;
                _inp.outd++;
                _inp.succList.add(this);
            }
        }
        return 1;
    }
    public int addLeaf(Node nd)
    {
        if(nMap.get(nd) != null) return -1;
        else
        {
            nList.add(nd);
            nMap.put(nd, _nodesNum++);
            Leaves.add(nd);
            this._output = nd;
            return 1;
        }
    }

    public int addNonLeaf(Node nd)
    {
        //this is used when adding a non-leaf node or a DAG,
        //please be sure that node nd connects to the DAG.
        int __code = -1;
        try{
            __code = _addNonLeaf(nd);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return __code;
    }

    public int _addNonLeaf(Node nd) throws Exception
    {
        if(nd.inDegree == 0)
        {
            throw new Exception("Using method addNonLeaf() while this Node is a leaf!!!");
        }
        if(nMap.get(nd) != null) return -1;
        else
        {
            nList.add(nd);
            nMap.put(nd, _nodesNum++);
            this._output = nd;
            this._edgesNum += nd.inDegree;
            return 1;
        }
    }

    public Node addFrom(Node ch1, Node ch2, int opSign)
    {
        Node nd = null;
        try{
            nd = addNewNode_from(ch1, ch2, opSign);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return nd;
    }

    public Node addFrom(Node ch1, int opSign)
    {
        Node nd = null;
        try{
            nd = addNewNode_from(ch1, opSign);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return nd;
    }

    //with return Node
    public Node addNewNode_from(Node ch1, Node ch2, int opSign) throws Exception
    {
        if(this.nMap.get(ch1) == null || this.nMap.get(ch2) == null)
        {
             String ex = "Exist";
             String nex = "NON-EXIST!";
             String first_info = (this.nMap.get(ch1) == null ? nex : ex);
             String second_info = (this.nMap.get(ch2) == null ? nex : ex);
             throw new Exception(String.format("Child Node 1 %s in Graph, Child Node 2 %s in Graph.", first_info, second_info));
        }
        this._edgesNum += 2;
        switch(opSign){
            case Calculation.ADD: {//add
                OpNode nd = new OpNode(ch1, ch2, Calculation.ADD);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.SUB:{//sub
                OpNode nd = new OpNode(ch1, ch2, Calculation.SUB);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.MUL:{//mul
                OpNode nd = new OpNode(ch1, ch2, Calculation.MUL);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.DIV:{//div
                OpNode nd = new OpNode(ch1, ch2, Calculation.DIV);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.MATMUL:{//mm
                MatmulNode nd = new MatmulNode(ch1, ch2);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.MSE_LOSS:{
                MSELoss nd = new MSELoss(ch1, ch2);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            default :
                throw new Exception(String.format("Unsupported Binary Operation Sign %d !", opSign));
        }
    }


    public Node addNewNode_from(Node ch1, int opSign) throws Exception
    {
        if(this.nMap.get(ch1) == null)
        {
            String first_info = (this.nMap.get(ch1) == null ? "NON-EXIST!" : "Exist");
            throw new Exception(String.format("Child Node 1 %s in Graph.", first_info));
        }
        this._edgesNum += 1;
        switch(opSign){
            case Calculation.SUM :{//sum
                SumNode nd = new SumNode(ch1);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.MAX :{//max
                MaxNode nd = new MaxNode(ch1);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output= nd;
                return nd;
            }
            case Calculation.EXP: {//max
                ExpNode nd = new ExpNode(ch1);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            case Calculation.SIGMOID:{
                SigmoidNode nd = new SigmoidNode(ch1);
                nList.add(nd);
                nMap.put(nd, _nodesNum++);
                this._output = nd;
                return nd;
            }
            default:
                return null;
        }
    }

    public boolean testConnected() throws Exception
    {
        int totalNodes = this.Preds.size() + this.nList.size();
        int inqNum = 0;
        HashMap<Node, Boolean> inq = new HashMap<>();
        for(Node nd : this.nList)
        {
            inq.put(nd, false);
        }
        for(Node _inp : this.Preds)
        {
            inq.put(_inp, false);
        }
        LinkedList<Node> q = new LinkedList<>();
        q.add(this._output);
        inq.put(this._output, true);
        inqNum++;
        while(!q.isEmpty() && inqNum < totalNodes)
        {
            Node f = q.poll();
//            System.out.println(f.Name);
            if(this.Preds.contains(f)) continue;
            if(f.Preds != null)
            {
                for(Node pre : f.Preds)
                {
                    if(inq.get(pre) != null && !inq.get(pre))
                    {
                        q.add(pre);
                        inqNum++;
                        inq.put(pre, true);
                    }
                }
            }
            else
            {
                if(f.pred[0] != null && !inq.get(f.pred[0]))
                {
                    q.add(f.pred[0]);
                    inqNum++;
                    inq.put(f.pred[0], true);
                }
                if(f.pred[1] != null && !inq.get(f.pred[1]))
                {
                    q.add(f.pred[1]);
                    inqNum++;
                    inq.put(f.pred[1], true);
                }
            }
        }
        if(inqNum != totalNodes)
        {
            System.out.println(inqNum + " - " + totalNodes);
            throw new Exception("Inside nodes and Inputs are not connected!");
        }
        return true;
    }

    @Override
    public void transForward() {
        if(!this.initialized){
            /*meaning the tensor and grad of the moudle itself is null*/
            this._tensor = MultiVector.MultiVector_like(this._output._tensor);
            this._grad = MultiVector.MultiVector_like(this._output._grad);
            this.initialized = true;
        }
        if(!this.insideConnected)
        {
            //inside parts are not Connected!!!
            try{
                this.insideConnected = testConnected();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        // TODO Auto-generated method stub
        LinkedList<Node> q = new LinkedList<Node>();
        for(Node le : this.Leaves)
        {
            if(!Preds.contains(le)){
                q.add(le);
            }
        }
        while(!q.isEmpty())
        {
            Node front = q.poll();
            front.transForward();
            if(front == this._output)
            {
                for(Node succ : this.succList)
                {
                    succ.ind--;
                }
                this._tensor.set_with(this._output._tensor);
                this.ind = this.inDegree;
            }
            else
            {
                for(Node succ : front.succList)
                {
                    if(succ.ind == 0) q.add(succ);
                }
            }
        }
    }

    @Override
    public void transBack() {
        // TODO Auto-generated method stub
        LinkedList<Node> q = new LinkedList<Node>();
        this._output._grad.set_with(this._grad);
        q.add(this._output);
        while(!q.isEmpty())
        {
            Node front = q.poll();
            if(!this.Preds.contains(front))
            {
                front.transBack();
//                System.out.println(front.Name);
                if(front.isMoudle)
                {
                    for(Node pre : front.Preds)
                    {
                        if(pre.outd == 0) q.add(pre);
                    }
                }
                else
                {
                    if(front.pred[0] != null && front.pred[0].outd == 0) q.add(front.pred[0]);
                    if(front.pred[1] != null && front.pred[1].outd == 0) q.add(front.pred[1]);
                }
            }
        }
        for(Node pre : this.Preds)
        {
            pre.outd--;
        }
        this.outd = this.outDegree;
    }

    @Override
    public void _updateWith_Grad(double lr) {
        // TODO Auto-generated method stub
        if(this.Trainable)
        {
            if(this.updateAND_clearGrad)
            {
                this._grad.mul(lr);
                // System.out.println("now update " + this.id);
                this._tensor.sub(this._grad);
            }
            else
            {
                this._tensor.sub(MultiVector.mul(this._grad, lr));
            }
            for(Node nd : this.nList)
            {
                /*input node and output node can not be trained, even if an input
                * node is a true leaf node.
                * if this true leaf input node needs to be trained, it should be inside
                * the boxNode, DON'T make it an input of this Moudle.*/
                if(!this.Preds.contains(nd)){
//                    System.out.println(nd.Name);
                    nd._updateWith_Grad(lr);
                }
            }
        }
        if(this.updateAND_clearGrad) this._grad.set_zeros();
    }
}
