package xyz.sangeng.gameframework.core.io.session.handler;


import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 心跳handler
 */
public class HeartBeatServerHandler implements ISessionHandler {

	private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

	@Override
	public void connect(ChannelHandlerContext ctx) {}

	@Override
	public void disconnect(ChannelHandlerContext ctx) {
		// 玩家正常下线移除session
		logger.info("角色下线");
	}

	@Override
	public void lostHeartBit(ChannelHandlerContext ctx) {
		// 丢失心跳
		logger.info("角色丢失心跳");
		ctx.close();
	}

}
