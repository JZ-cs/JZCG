package network.netty.gradTransfer.server;

import dataDistribute.utils.GradPartition;
import dataDistribute.utils.GradPartitionMatrix;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network.netty.GradPackage;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GradTransferServerHandler extends ChannelInboundHandlerAdapter {
    private final String selfIp;
    public GradPartition gp;
    public AtomicBoolean Closed;
    private static final Logger logger = Logger
            .getLogger(GradTransferServerHandler.class.getName());

    public GradTransferServerHandler(String selfIp, GradPartition gp, AtomicBoolean Closed) {
        this.selfIp = selfIp;
        this.gp = gp;
        this.Closed = Closed;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println(new Date() + String.format("   GradTransfer Server-%s read from Channel.", this.selfIp));
//        GradPackage gradPackage = (GradPackage)msg;
//        this.gpm.setPartitions(gradPackage.partitionId, gradPackage.gradSeqs);
        if(msg instanceof GradPackage){
            GradPackage gradPackage = (GradPackage)msg;
            System.out.printf("GradTransfer Server-%s msg got! %n", this.selfIp);
            if(gradPackage.op == GradPackage.ADD){
                this.gp.addWith(gradPackage.partitionId, gradPackage.gradSeq);
            }
            else if(gradPackage.op == GradPackage.SET){
                this.gp.replaceWith(gradPackage.partitionId, gradPackage.gradSeq);
            }
            String serverMsg = "Accepted!";
            ctx.writeAndFlush(serverMsg);
        }
        this.Closed.set(true);
    }

    // @Override
    // public void channelReadComplete(ChannelHandlerContext ctx) throws
    // Exception {
    // ctx.flush();
    // ctx.close();
    // }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }

}
