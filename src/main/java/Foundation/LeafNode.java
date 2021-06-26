package Foundation;

public class LeafNode extends Node{
    public LeafNode(MultiVector mv1){
        super(mv1);
        this.isLeaf = true;
        this.Trainable = true;
        this.Name = "Leaf-" + this.id;
    }
}
