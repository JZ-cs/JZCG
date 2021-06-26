package utils;

import CG.ComputationalGraph;
import Foundation.MultiVector;
import dataDistribute.utils.ServerInfo;

import java.io.Serializable;

public class TrainingInfo implements Serializable {

    public int batchSize;
    public int epoches;
    public int batches;
    public double lr;

    public volatile ComputationalGraph CG;
    public MultiVector[] X;
    public MultiVector[] Y;
    public ServerInfo[] serverInfoList;
    public TrainingInfo(){

    }
    public TrainingInfo(ServerInfo[] serverInfoList, ComputationalGraph cg, MultiVector[] X, MultiVector[] Y, int batches, int batchSize, int epoches, double lr){
        this.serverInfoList = serverInfoList;
        this.CG = cg;
        this.X = X;
        this.Y = Y;
        this.batchSize = batchSize;
        this.batches = batches;
        this.epoches = epoches;
        this.lr = lr;
    }
    public void setWith(TrainingInfo trainingInfo){
        if(trainingInfo == this) return;
        this.serverInfoList = trainingInfo.serverInfoList;
        this.CG = trainingInfo.CG;
        this.X = trainingInfo.X;
        this.Y = trainingInfo.Y;
        this.batchSize = trainingInfo.batchSize;
        this.batches = trainingInfo.batches;
        this.epoches = trainingInfo.epoches;
        this.lr = trainingInfo.lr;
    }
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getEpoches() {
        return epoches;
    }

    public void setEpoches(int epoches) {
        this.epoches = epoches;
    }

    public int getBatches() {
        return batches;
    }

    public void setBatches(int batches) {
        this.batches = batches;
    }

    public double getLr() {
        return lr;
    }

    public void setLr(double lr) {
        this.lr = lr;
    }

    public ComputationalGraph getCG() {
        return CG;
    }

    public void setCG(ComputationalGraph CG) {
        this.CG = CG;
    }

    public MultiVector[] getX() {
        return X;
    }

    public void setX(MultiVector[] x) {
        X = x;
    }

    public MultiVector[] getY() {
        return Y;
    }

    public void setY(MultiVector[] y) {
        Y = y;
    }

    public ServerInfo[] getServerInfoList() {
        return serverInfoList;
    }

    public void setServerInfoList(ServerInfo[] serverInfoList) {
        this.serverInfoList = serverInfoList;
    }

}
