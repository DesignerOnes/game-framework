package xyz.sangeng.gameframework.core.io.session.handler;


import io.netty.channel.ChannelHandlerContext;


public interface ISessionHandler {

	/**
	 * 玩家连接上
	 *
	 * @param ctx
	 */
	public void connect(ChannelHandlerContext ctx);

	/**
	 * 断连
	 *
	 * @param ctx
	 */
	public void disconnect(ChannelHandlerContext ctx);

	/**
	 * 长时间没收到心跳，有可能是游戏切换到后台，也可能是弱网
	 *
	 * @param ctx
	 */
	public void lostHeartBit(ChannelHandlerContext ctx);

}
