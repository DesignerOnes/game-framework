package xyz.sangeng.gameframework.core.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ComThreadQueue {

	NamingThreadFactory threadFactory;

	LinkedBlockingQueue<Runnable> runQueue;

	ExecutorService executorService;

	public ComThreadQueue(String threadName) {
		threadFactory = new NamingThreadFactory(threadName);
		runQueue = new LinkedBlockingQueue<Runnable>();
	}

	public void initSingleThreadExecutor() {
		executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				runQueue,
				threadFactory);
	}

	//默认一分钟没活干，释放
	public void init(int corePoolSize, int maxPoolSize) {
		init(corePoolSize, maxPoolSize, 60L);
	}

	public void init(int corePoolSize, int maxPoolSize, long keepAliveSeconds) {
		executorService = new ThreadPoolExecutor(
				corePoolSize,
				maxPoolSize,
				keepAliveSeconds,
				TimeUnit.SECONDS,
				runQueue,
				threadFactory);
	}

	public LinkedBlockingQueue<Runnable> getRunQueue() {
		return runQueue;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void shutDown() {
		executorService.shutdown();
	}

}
