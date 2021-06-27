package network.netty.gradTransfer.server;

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
    public GradPartitionMatrix gpm;
    public AtomicBoolean Closed;
    private static final Logger logger = Logger
            .getLogger(GradTransferServerHandler.class.getName());

    public GradTransferServerHandler(String selfIp, GradPartitionMatrix gpm, AtomicBoolean Closed) {
        this.selfIp = selfIp;
        this.gpm = gpm;
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
            this.gpm.setPartitions(gradPackage.partitionId, gradPackage.gradSeqs);
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
