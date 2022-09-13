package xyz.sangeng.gameframework.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 游戏配置信息
 */

@Setter
@Getter
@Component
public class ServerConfig {

    /**
     * 服务器id
     */
    @Value("${game.serverId}")
    private int serverId;

    /**
     * 服务器名称
     */
    @Value("${game.serverName}")
    private String serverName;

    /**
     * 服务器端口
     */
    @Value("${game.nettyPort}")
    private int nettyPort;
}
