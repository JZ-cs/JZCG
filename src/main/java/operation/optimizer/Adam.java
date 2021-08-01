package operation.optimizer;

import CG.ComputationalGraph;
import operation.Calculation;
import operation.MultiVector;
import operation.Node;
import operation.Pair;
import utils.GraphTravels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Adam implements Optimizer{
    public long t;
    public double beta1, beta2, alpha;
    public HashSet<Node> registerLeaves;
    public HashMap<Node, Pair<MultiVector, MultiVector>> mvMap;
    public double epsilon;
    public BiFunction<Double, Long, Double> weight_decay;
    public Adam(ComputationalGraph cg, double lr, double beta1, double beta2, double epsilon, BiFunction<Double, Long, Double> w_decay){
        this.alpha = lr;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.registerLeaves = new HashSet<>();
        GraphTravels.travelForLeaves(cg.DAG.nList, this.registerLeaves);
        for(Node le : this.registerLeaves){
            mvMap.put(le, Pair.make_pair(
                    MultiVector.MultiVector_like(le._grad, Calculation.SET_EMPTY_DATA),
                    MultiVector.MultiVector_like(le._grad, Calculation.SET_EMPTY_DATA)
            ));
        }
        this.weight_decay = w_decay;
    }
    public Adam(ComputationalGraph cg, double lr){
        this(cg, lr, 0.9, 0.999, 1e-8, (learning_rate, iters)-> learning_rate);
    }

    public void updateGrads(){
        this.t++;
        if(this.t == 1){
            //beginning of the grad update, initialize all with zeros.
            for(Pair<MultiVector, MultiVector> mv: this.mvMap.values()){
                if(mv.first._data == null){
                    mv.first.initializeData(Calculation.SET_ALL_ZEROS);
                }
                if(mv.second._data == null){
                    mv.second.initializeData(Calculation.SET_ALL_ZEROS);
                }
            }
        }
        for(Map.Entry<Node, Pair<MultiVector, MultiVector>> entry : this.mvMap.entrySet()){
            Node nd = entry.getKey();
            MultiVector m = entry.getValue().first;
            MultiVector v = entry.getValue().second;
            MultiVector g = MultiVector.MultiVector_like(nd._grad);
            m.mul(this.beta1);
            g.mul(1.0 - this.beta1);
            m.add(g);
            g.div(1.0 - this.beta1);

            g.mul(g);
            v.mul(this.beta2);
            g.mul(1.0 - this.beta2);
            v.add(g);


            MultiVector mFold = MultiVector.div(m, 1.0 - Math.pow(this.beta1, t));
            MultiVector vFold = MultiVector.div(v, 1.0 - Math.pow(this.beta2, t));
            //update grad;
            vFold.sqrt_inplace();
            vFold.add(this.epsilon);
            double lr;
            if(this.weight_decay != null) lr = this.weight_decay.apply(this.alpha, this.t);
            else lr = this.alpha;
            mFold.mul(lr);
            mFold.div(vFold);
            nd._tensor.sub(mFold);
            //set grads to zeros for next step
            nd._grad.set_zeros();
        }
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
        public Adam build(ComputationalGraph cg){
            if(this.beta1 == 0.0) this.beta1 = 0.9;
            if(this.beta2 == 0.0) this.beta2 = 0.999;
            if(this.epsilon == 0.0) this.epsilon = 1e-8;
            if(this.lr == 0.0) this.lr = 0.001;
            if(this.weight_decay == null){
                this.weight_decay = (learning_rate, iters)->learning_rate;
            }
            return new Adam(
                    cg,
                    this.lr,
                    this.beta1,
                    this.beta2,
                    this.epsilon,
                    this.weight_decay);
        }
    }
}
