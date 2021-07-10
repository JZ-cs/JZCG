package network.netty.gradTransfer.server;

import dataDistribute.utils.GradPartition;
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

import io.netty.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class GradTransferServer {

    private final int port;
    private final String selfIp;
    public GradPartition gp;

    public GradTransferServer(String selfIp, int port, GradPartition gp) {
        this.selfIp = selfIp;
        this.port = port;
        this.gp = gp;
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
                                    new GradTransferServerHandler(selfIp, gp, Closed));
                        }
                    });

            // Bind and start to accept incoming connections.
            serverChannel = b.bind(port).channel();
            System.out.printf("GradTransfer Server-%s listen at %d start!%n", this.selfIp, port);
            serverChannel.closeFuture();
            for(;;){
                Thread.sleep(100);
                if(Closed.get()){
                    System.out.println("req GradTransfer server closed!");
                    closeServer();
                    break;
                }
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            Future<?> bs = bossGroup.shutdownGracefully();
            Future<?> ws = workerGroup.shutdownGracefully();
            bs.get();
            ws.get();
            System.out.printf("GradTransfer Server-%s listen at %d Closed!%n", this.selfIp, port);
        }
    }
}
