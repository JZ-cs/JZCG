package modelDistribute;

import operation.Calculation;
import operation.MultiVector;
import operation.Node;

import java.util.Objects;

public class RemoteProxy extends Node {
    public MDTaskRunnerID remoteMDTaskRunnerID;
    public MDTaskRunnerID locatedMDTaskRunnerID;
    public String remoteName;
    public RemoteProxy(Node node, MDTaskRunnerID remoteMDTaskRunnerID, MDTaskRunnerID locatedMDTaskRunnerID){
        this.Name = "Remode-" + node.getName();
        this.remoteName = node.getName();
        this._tensor = MultiVector.MultiVector_like(node._tensor, Calculation.SET_EMPTY_DATA);
        this._grad = MultiVector.MultiVector_like(node._grad, Calculation.SET_EMPTY_DATA);
        this.isLeaf = false;
        this.Trainable = false;
        this.outDegree = this.outd = 0;
        this.inDegree = this.ind = 0;
        this.remoteMDTaskRunnerID = remoteMDTaskRunnerID;
        this.locatedMDTaskRunnerID = locatedMDTaskRunnerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteProxy)) return false;
        RemoteProxy that = (RemoteProxy) o;
        return Objects.equals(remoteMDTaskRunnerID, that.remoteMDTaskRunnerID) && Objects.equals(locatedMDTaskRunnerID, that.locatedMDTaskRunnerID) && Objects.equals(remoteName, that.remoteName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteMDTaskRunnerID, locatedMDTaskRunnerID, remoteName);
    }
}
