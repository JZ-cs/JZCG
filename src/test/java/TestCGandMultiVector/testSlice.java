package TestCGandMultiVector;

import operation.MultiVector;
import operation.Pair;
import utils.DataGenerator;

public class testSlice {
    public static void main(String[] args) {
        int[] shape = {2, 2};
        DataGenerator dataGenerator = new DataGenerator(1, 6, shape);
        Pair<MultiVector[], MultiVector[]> data = dataGenerator.genData(false);
        MultiVector[] X = data.first;
        int pBatchSize = 6 / 3;
        for(int i = 0; i < 3; i++){
            Pair<Integer, Integer> pRange = Pair.make_pair(i * pBatchSize, (i + 1) * pBatchSize);
            MultiVector p = MultiVector.slice(X[0], true, pRange);
            p.print();
        }
    }
}
