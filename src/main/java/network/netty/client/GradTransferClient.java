package network.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import network.netty.GradPackage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GradTransferClient {
    private final String host;

    private final int port;
    private final String selfIp;
    private final EventLoopGroup loop = new NioEventLoopGroup();
    public GradPackage gradPackage;
    public AtomicBoolean Closed;
    ChannelFuture f = null;

    public int serverNonReadyRetryIntervalMs = 500;
    public int getServerNonReadyRetryIntervalMs() {
        return serverNonReadyRetryIntervalMs;
    }

    public void setServerNonReadyRetryIntervalMs(int serverNonReadyRetryIntervalMs) {
        this.serverNonReadyRetryIntervalMs = serverNonReadyRetryIntervalMs;
    }

    public GradTransferClient(String selfIp, String host, int port, GradPackage gradPackage) {
        System.out.println(selfIp + "--" + "client init!");
        this.selfIp = selfIp;
        this.host = host;
        this.port = port;
        this.gradPackage = gradPackage;
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
                                        new GradTransferClientHandler(selfIp, gradPackage, Closed));
                            }
                        }).remoteAddress(host, port);

                f = bootstrap.connect().addListener((ChannelFuture futureListener)->{
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        System.out.printf("Client FAILED to connected to Server! will retry in %d ms! %n", serverNonReadyRetryIntervalMs);
                        eventLoop.schedule(() -> doConnect(new Bootstrap(), eventLoop), serverNonReadyRetryIntervalMs, TimeUnit.MILLISECONDS);
                    }else {
                        System.out.println("Client successfully connected to Server!");
                        f.channel().closeFuture().sync();
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("Fail to connect client,error：" + e);
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
        System.out.println(selfIp + " -- " + "client shutdown!");
    }
}
