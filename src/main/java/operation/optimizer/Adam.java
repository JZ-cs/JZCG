package operation.optimizer;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;
import operation.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiFunction;

public class Adam implements Optimizer{
    public double beta1, beta2, alpha;
    public HashMap<Node, NodeTrainingInfo> nodeMVmap;
    public double epsilon;
    public BiFunction<Double, Long, Double> weight_decay;
    public Adam(double lr, double beta1, double beta2, double epsilon, BiFunction<Double, Long, Double> w_decay){
        this.alpha = lr;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.weight_decay = w_decay;
        this.nodeMVmap = new HashMap<>();
    }
    public Adam(double lr){
        this(lr, 0.9, 0.999, 1e-8, (learning_rate, iters)-> learning_rate);
    }

    public void updateWithGrads(Node nd){
        MultiVector g = nd._grad;
        if(!this.nodeMVmap.containsKey(nd)){
            //beginning of the grad update, initialize all with zeros.
            this.nodeMVmap.put(nd, new NodeTrainingInfo(nd));
        }
        NodeTrainingInfo nodeTrainingInfo = this.nodeMVmap.get(nd);

        MultiVector m = nodeTrainingInfo.m;
        MultiVector v = nodeTrainingInfo.v;
        nodeTrainingInfo.t++;

        m.mul(this.beta1);
        g.mul(1.0 - this.beta1);
        m.add(g);
        g.div(1.0 - this.beta1);

        g.mul(g);
        v.mul(this.beta2);
        g.mul(1.0 - this.beta2);
        v.add(g);


        MultiVector mFold = MultiVector.div(m, 1.0 - Math.pow(this.beta1, nodeTrainingInfo.t));
        MultiVector vFold = MultiVector.div(v, 1.0 - Math.pow(this.beta2, nodeTrainingInfo.t));
        //update grad;
        vFold.sqrt_inplace();
        vFold.add(this.epsilon);
        double lr;
        if(this.weight_decay != null) lr = this.weight_decay.apply(this.alpha, nodeTrainingInfo.t);
        else lr = this.alpha;
        mFold.mul(lr);
        mFold.div(vFold);
        nd._tensor.sub(mFold);
    }

    public static class Builder{
        public double beta1, beta2, lr;
        public double epsilon;
        public BiFunction<Double, Long, Double> weight_decay;

        public Builder setBeta1(double beta1) {
            this.beta1 = beta1;
            return this;
        }
        public Builder setBeta2(double beta2) {
            this.beta2 = beta2;
            return this;
        }

        public Builder setLr(double lr) {
            this.lr = lr;
            return this;
        }

        public Builder setEpsilon(double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public Builder setWeight_decay(BiFunction<Double, Long, Double> weight_decay) {
            this.weight_decay = weight_decay;
            return this;
        }
        public Adam build(){
            if(this.beta1 == 0.0) this.beta1 = 0.9;
            if(this.beta2 == 0.0) this.beta2 = 0.999;
            if(this.epsilon == 0.0) this.epsilon = 1e-8;
            if(this.lr == 0.0) this.lr = 0.001;
            if(this.weight_decay == null){
                this.weight_decay = (learning_rate, iters)->learning_rate;
            }
            return new Adam(
                    this.lr,
                    this.beta1,
                    this.beta2,
                    this.epsilon,
                    this.weight_decay);
        }
    }

    public class NodeTrainingInfo implements Serializable {
        public MultiVector m;
        public MultiVector v;
        public long t;
        public NodeTrainingInfo(Node nd){
            m = MultiVector.MultiVector_like(nd._grad, Calculation.SET_ALL_ZEROS);
            v = MultiVector.MultiVector_like(nd._grad, Calculation.SET_ALL_ZEROS);
            t = 0;
        }
    }
}
