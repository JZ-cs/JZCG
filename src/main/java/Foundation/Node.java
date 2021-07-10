package Foundation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;


public class Node implements Serializable {
    public static int exCount = 0;
    public int id;
    public String Name;
    public MultiVector _tensor;
    public MultiVector _grad;
    public int calsym = -1;
    public int outDegree = 0;
    public int outd = 0;
    public int inDegree = 0;
    public int ind = 0;
    public boolean isMoudle;
    public boolean isLeaf;
    public boolean Trainable;
    public boolean updateAND_clearGrad;
    public Node[] pred = new Node [2];
    HashSet<Node> Preds = null;
    MultiVector[] databuffer = new MultiVector[2];
    ArrayList<Node> succList = new ArrayList<>();

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
        which currently we use pred[] to store, so the "outd" field change(-1) happens in
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
//            System.out.println(this.Name);
//            System.out.println("grad:");
//            this._grad.print();
//            System.out.println("Before Tensor:");
//            this._tensor.print();
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
//            System.out.println("After Tensor:");
//            this._tensor.print();
//            System.out.println("----------------------------------------------------------------");
        }
        if(this.updateAND_clearGrad) this._grad.set_zeros();
    }

    public void showName()
    {
        System.out.println(this.Name);
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
}



