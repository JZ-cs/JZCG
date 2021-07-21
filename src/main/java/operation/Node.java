package operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;


public class Node implements Serializable {
    public static int exCount = 0;
    public int id;
    public String Name;
    public MultiVector _tensor;
    public MultiVector _grad;
    public int outDegree = 0;
    public int outd = 0;
    public int inDegree = 0;
    public int ind = 0;
    public boolean isMoudle;
    public boolean isLeaf;
    public boolean Trainable;
    public boolean updateAND_clearGrad;
    public Node[] pred = new Node [2];
    public HashSet<Node> Preds = null;
    public ArrayList<Node> succList = new ArrayList<>();

    {
        this.id = exCount++;
        this.isMoudle = false;
        this.isLeaf = true;
        this.Name = "Leaf-" + this.id;
        this.Trainable = false;
        this.updateAND_clearGrad = true;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        Node tn = (Node)obj;
        if(tn instanceof Node)
        {
            if(this == tn) return true;
            else if(tn.id == this.id) return true;
            else return false;
        }
        return false;
    }

    public Node()
    {

    }

    public Node(MultiVector _t)
    {
        this._tensor = _t;
        this._grad = MultiVector.MultiVector_like(_t, 0);
    }

    public Node(Node ch1, Node ch2)
    {
        pred[0] = ch1;
        pred[1] = ch2;
        ch1.outDegree++;
        ch1.outd++;
        ch2.outDegree++;
        ch2.outd++;
        ch1.succList.add(this);
        ch2.succList.add(this);
        this.inDegree += 2;
        this.ind += 2;
        this.isLeaf = false;
    }

    public Node(Node ch1)
    {
        pred[0] = ch1;
        ch1.outDegree++;
        ch1.outd++;
        this.inDegree += 1;
        this.ind += 1;
        ch1.succList.add(this);
        this.isLeaf = false;
    }


    public void transForward()
    {
        // Forward to successors
        for(Node succ : this.succList)
        {
            succ.ind--;
        }
        //set ind with inDegree
        this.ind = this.inDegree;
    }

    /*A Node can have multiple successors, but the number of predcessors is in {0,1,2},
        which currently use pred[] to store, so the "outd" field change(-1) happens in
        the specific child Node. This may change in the future, when multiple predcessors
        is supported.*/
    public void transBack()
    {
        this.outd = this.outDegree;
    }

    public void _updateWith_Grad(double lr)
    {
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
        }
        if(this.updateAND_clearGrad) this._grad.set_zeros();
    }

    public String getName()
    {
        return this.Name;
    }
    public void setName(String name)
    {
        this.Name = name;
    }

    public void printTensor(){
        System.out.println(this.Name + ": ");
        this._tensor.print();
    }
    public void printGrad(){
        System.out.println(this.Name + ": ");
        this._grad.print();
    }

    public long getCost(){
        return this._tensor.total_eNum;
    }

    public String getConnectionInfo(){
        ArrayList<String> preList = new ArrayList<>();
        if(this.Preds == null){
            if(this.pred[0] != null){
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                sb.append(pred[0].Name + ", " + pred[0].id);
                sb.append(")");
                preList.add(sb.toString());
            }
            if(this.pred[1] != null){
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                sb.append(pred[1].Name + ", " + pred[1].id);
                sb.append(")");
                preList.add(sb.toString());
            }

        }
        else{
            for(Node pred : this.Preds){
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                sb.append(pred.Name + ", " + pred.id);
                sb.append(")");
                preList.add(sb.toString());
            }
        }
        ArrayList<String> succList = new ArrayList<>();
        for(Node succ : this.succList){
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(succ.Name + ", " + succ.id);
            sb.append(")");
            succList.add(sb.toString());
        }
        StringBuilder connectionInfo = new StringBuilder();
        connectionInfo.append("{");
        for(int i = 0; i < preList.size(); i++){
            connectionInfo.append(preList.get(i));
            if(i != preList.size() - 1){
                connectionInfo.append("  ");
            }
            else connectionInfo.append("} ");
        }
        if(preList.isEmpty()) connectionInfo.append("} ");
        connectionInfo.append(" --(p)--> [");
        connectionInfo.append(this.Name + ", " + this.id);
        connectionInfo.append("] --(s)-->  {");
        for(int i = 0; i < succList.size(); i++){
            connectionInfo.append(succList.get(i));
            if(i != succList.size() - 1){
                connectionInfo.append("  ");
            }
            else connectionInfo.append("}");
        }
        if(succList.isEmpty()) connectionInfo.append("} ");
        return connectionInfo.toString();
    }
}



