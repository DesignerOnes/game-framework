package xyz.sangeng.gameframework.core.io.session;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.sangeng.gameframework.utils.ClassUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


@ChannelHandler.Sharable
public class SessionHandlerAdapter extends ChannelInboundHandlerAdapter {

	public static final Logger logger = LoggerFactory.getLogger( SessionHandlerAdapter.class);

	/**
	 * 会话控制,交由业务层面处理
	 */
	private List<ISessionHandler> sessionHandlers = new ArrayList<>();

	public SessionHandlerAdapter() {
		// 加载所有的ISessionHandler
		ClassUtil.getClasses("xyz.sangeng.gameframework.core.io.session.handler").stream().filter(ISessionHandler.class::isAssignableFrom).forEach((k) -> {
			try {
				sessionHandlers.add((ISessionHandler) k.newInstance());
			} catch (Exception e) {
				logger.error("预加载session处理器出错:", e);
			}
		});
	}

	/**
	 * 接入
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (!sessionHandlers.isEmpty()) {
			sessionHandlers.forEach(handler -> handler.connect(ctx));
		}
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		logger.info("session {} {} connect.", ctx.channel().id(), insocket.getAddress().getHostAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive()) {
			ctx.close();
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
			if (!sessionHandlers.isEmpty()) {
				sessionHandlers.forEach(handler -> handler.lostHeartBit(ctx));
			}
		} else if (((IdleStateEvent) evt).state() == IdleState.ALL_IDLE) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			logger.info("session {} {}  all idle.", ctx.channel().id(), insocket.getAddress().getHostAddress());
			ctx.close();
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		try {
			if (ctx.channel().isActive()) {
				ctx.close();
			} else {
				if (!sessionHandlers.isEmpty()) {
					sessionHandlers.forEach(handler -> handler.disconnect(ctx));
				}
				InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
				logger.info("session {} {}  unregistered.", ctx.channel().id(), insocket.getAddress().getHostAddress());
			}
		} finally {
			super.channelUnregistered(ctx);
		}
	}

}
