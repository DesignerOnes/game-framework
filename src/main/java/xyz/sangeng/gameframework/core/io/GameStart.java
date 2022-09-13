package xyz.sangeng.gameframework.core.io;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.sangeng.gameframework.core.config.ApplicationContextProvider;
import xyz.sangeng.gameframework.core.config.ServerConfig;

/**
 * @Auther: HuangHaiLiang
 */
@Component
public class GameStart implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        int nettyPort = ApplicationContextProvider.getBean(ServerConfig.class).getNettyPort();
        new NettyBootstrap(nettyPort).startSocket();
    }
}
