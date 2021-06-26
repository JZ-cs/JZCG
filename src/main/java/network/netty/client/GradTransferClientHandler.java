package network.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.netty.GradPackage;

public class GradTransferClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(GradTransferClientHandler.class.getName());

    private final GradPackage gradPackage;
    private final String selfIp;
    public AtomicBoolean Closed;
    /**
     * Creates a client-side handler.
     */
    public GradTransferClientHandler(String selfIp, GradPackage gradPackage, AtomicBoolean Closed) {
        this.gradPackage = gradPackage;
        this.selfIp = selfIp;
        this.Closed = Closed;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the message to Server
        super.channelActive(ctx);
        System.out.println("channelActive");
        System.out.println(this.selfIp + " start send to server!");
        ctx.writeAndFlush(this.gradPackage);
        System.out.println(this.selfIp + " client data sent!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // you can use the Object from Server here
        System.out.println(new Date() + " -- " + this.selfIp + " read as Client from Channel, msg: " + msg);
        if(msg instanceof String){
            String smsg = (String) msg;
            if(smsg.equals("channel started")){
                System.out.println("channel started");
                ctx.writeAndFlush(this.gradPackage);
            }
            else{
                System.out.println("Client now should close!");
                ctx.close();
                Closed.set(true);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }
}

