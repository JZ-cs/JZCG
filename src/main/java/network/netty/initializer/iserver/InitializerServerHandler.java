package network.netty.initializer.iserver;

import dataDistribute.utils.GradPartitionMatrix;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network.netty.GradPackage;
import network.netty.server.GradTransferServerHandler;
import utils.TrainingInfo;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitializerServerHandler extends ChannelInboundHandlerAdapter {
    private final String selfIp;
    public TrainingInfo trainingInfo;
    public AtomicBoolean Closed;
    private static final Logger logger = Logger
            .getLogger(GradTransferServerHandler.class.getName());

    public InitializerServerHandler(String selfIp, TrainingInfo trainingInfo, AtomicBoolean Closed) {
        this.selfIp = selfIp;
        this.trainingInfo = trainingInfo;
        this.Closed = Closed;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println(new Date() + " -- " + this.selfIp + " read as Initializer Server from Channel");
//        GradPackage gradPackage = (GradPackage)msg;
//        this.gpm.setPartitions(gradPackage.partitionId, gradPackage.gradSeqs);
        if(msg instanceof TrainingInfo){
            TrainingInfo msgtrainingInfo = (TrainingInfo)msg;
            System.out.println("TrainingInfo got!!!");
            this.trainingInfo.setWith(msgtrainingInfo);
            String serverMsg = "Accepted and dealed!";
            ctx.writeAndFlush(serverMsg);
        }
        this.Closed.set(true);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }
}
