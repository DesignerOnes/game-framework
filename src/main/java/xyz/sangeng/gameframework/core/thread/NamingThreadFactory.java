package xyz.sangeng.gameframework.core.thread;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NamingThreadFactory implements ThreadFactory {

	private String threadName;

	final ThreadGroup group;

	final AtomicInteger threadNumber = new AtomicInteger(1);

	public NamingThreadFactory(String name) {
		this.threadName = name;
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, threadName + "-" + threadNumber.getAndIncrement());
		t.setDaemon(true);
		return t;
	}
}
