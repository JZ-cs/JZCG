package network.netty.server;

import dataDistribute.utils.GradPartitionMatrix;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import utils.GradSeq;

import java.util.concurrent.atomic.AtomicBoolean;

public class GradTransferServer {

    private final int port;
    private final String selfIp;
    public GradPartitionMatrix gpm;

    public GradTransferServer(String selfIp, int port, GradPartitionMatrix gpm) {
        this.selfIp = selfIp;
        this.port = port;
        this.gpm = gpm;
    }
    public Channel serverChannel;
    public AtomicBoolean Closed;
    public void closeServer() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            this.Closed = new AtomicBoolean();
            Closed.set(false);
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)),
                                    new GradTransferServerHandler(selfIp, gpm, Closed));
                        }
                    });

            // Bind and start to accept incoming connections.
            serverChannel = b.bind(port).channel();
            System.out.println(this.selfIp + " -- " + "server Run!");
            serverChannel.closeFuture();
            for(;;){
                Thread.sleep(50);
                if(Closed.get()){
                    System.out.println("req server closed!");
                    closeServer();
                    break;
                }
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println(this.selfIp + " -- " + "server done!");
        }
    }
}
