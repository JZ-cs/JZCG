package network.netty.initializer.iclient;
import dataDistribute.utils.ServerInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import network.netty.GradPackage;
import network.netty.client.GradTransferClientHandler;
import utils.TrainingInfo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InitializerClient {
    private final String host;

    private final int port;
    private final String selfIp;
    private final EventLoopGroup loop = new NioEventLoopGroup();
    public TrainingInfo trainingInfo;
    public AtomicBoolean Closed;
    ChannelFuture f = null;

    public int serverNonReadyRetryIntervalMs = 500;
    public int getServerNonReadyRetryIntervalMs() {
        return serverNonReadyRetryIntervalMs;
    }

    public void setServerNonReadyRetryIntervalMs(int serverNonReadyRetryIntervalMs) {
        this.serverNonReadyRetryIntervalMs = serverNonReadyRetryIntervalMs;
    }

    public InitializerClient(String selfIp, String host, int port, TrainingInfo trainingInfo) {
        System.out.println(selfIp + "--" + " Initializer client init!");
        this.selfIp = selfIp;
        this.host = host;
        this.port = port;
        this.trainingInfo = trainingInfo;
    }


    public void doConnect(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        try {
            if (bootstrap != null) {
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(Integer.MAX_VALUE ,ClassResolvers.cacheDisabled(null)),
                                        new InitializerClientHandler(selfIp, trainingInfo, Closed));
                            }
                        }).remoteAddress(host, port);

                f = bootstrap.connect().addListener((ChannelFuture futureListener)->{
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        System.out.printf("Initializer Client FAILED to connected to Initializer Server! will retry in %d ms! %n", serverNonReadyRetryIntervalMs);
                        eventLoop.schedule(() -> doConnect(new Bootstrap(), eventLoop), serverNonReadyRetryIntervalMs, TimeUnit.MILLISECONDS);
                    }else {
                        System.out.printf("Initializer Client successfully connected to Initializer Server-%s:%d! %n", this.host, this.port);
                        f.channel().closeFuture().sync();
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("Fail to connect Initializer client,error：" + e);
        }
    }

    public void run() throws InterruptedException {
        this.Closed = new AtomicBoolean();
        this.Closed.set(false);
        doConnect(new Bootstrap(), loop);
        for(;;){
            Thread.sleep(100);
            if(Closed.get()){
                this.loop.shutdownGracefully();
                break;
            }
        }
        System.out.println(selfIp + " -- " + "Initializer client shutdown!");
    }
}
