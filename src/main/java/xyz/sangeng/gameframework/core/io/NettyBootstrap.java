package xyz.sangeng.gameframework.core.io;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.sangeng.gameframework.core.config.ApplicationContextProvider;
import xyz.sangeng.gameframework.core.config.ServerConfig;
import xyz.sangeng.gameframework.core.io.session.SessionHandlerAdapter;
import xyz.sangeng.gameframework.core.io.session.handler.test.EchoServerHandler;
import xyz.sangeng.gameframework.core.thread.ExecutorServiceFactory;

import java.util.concurrent.ExecutorService;

public class NettyBootstrap {

    public static final Logger log = LogManager.getLogger(NettyBootstrap.class);

    private final int port;

    private final SessionHandlerAdapter sessionAdapter;

    private final ExecutorService nettyBootThread;

    public NettyBootstrap(int port) {
        this.sessionAdapter = new SessionHandlerAdapter();
        this.port = port;
        this.nettyBootThread = ExecutorServiceFactory.newFixedThreadPool("netty_start_thread", 1);
    }

    public void stopSocketThread() {
        this.nettyBootThread.shutdownNow();
    }

    public void startSocket() {
        ServerConfig serverConfig = ApplicationContextProvider.getBean(ServerConfig.class);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // FIXME 开发阶段先把心跳调大
                    ch.pipeline().addLast("heartbeat", new IdleStateHandler(15, 0, 3000))
                            .addLast(new EchoServerHandler())
                            .addLast("sessionHandler", sessionAdapter);
                }
            }).option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            ChannelFuture f = bootstrap.bind(port).sync();
            log.error("netty启动完成，端口:{}", serverConfig.getNettyPort());
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
