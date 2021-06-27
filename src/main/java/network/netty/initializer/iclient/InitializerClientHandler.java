package network.netty.initializer.iclient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network.netty.gradTransfer.client.GradTransferClientHandler;
import utils.TrainingInfo;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitializerClientHandler extends ChannelInboundHandlerAdapter{
    private static final Logger logger = Logger
            .getLogger(GradTransferClientHandler.class.getName());

    private final TrainingInfo trainingInfo;
    private final String selfIp;

    public AtomicBoolean Closed;
    public InitializerClientHandler(String selfIp, TrainingInfo trainingInfo, AtomicBoolean Closed) {
        this.trainingInfo = trainingInfo;
        this.selfIp = selfIp;
        this.Closed = Closed;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the message to Server
        super.channelActive(ctx);
        System.out.printf("Initializer client-%s channel built, starting to send training info! %n", this.selfIp);
        ctx.writeAndFlush(this.trainingInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // you can use the Object from Server here
        System.out.printf("Initializer client-%s receive close message. %n", this.selfIp);
        this.Closed.set(true);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }
}
