package xyz.sangeng.gameframework.core.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ExecutorServiceFactory {

	//默认60秒空闲释放线程
	public static ExecutorService newFixedThreadPool(String threadName, int threadNum) {
		return newFixedThreadPool(threadName, threadNum, 60);
	}

	public static ExecutorService newFixedThreadPool(String threadName, int threadNum, int keepSeconds) {
		ComThreadQueue threadQueue = new ComThreadQueue(threadName);
		threadQueue.init(threadNum, threadNum, keepSeconds);
		return threadQueue.getExecutorService();
	}

	public static ScheduledThreadPoolExecutor newFixedScheduledThreadPool(String threadName, int coreThreadNum, int maxThreadNum) {
		maxThreadNum = maxThreadNum >= coreThreadNum ? maxThreadNum : coreThreadNum;
		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(coreThreadNum, new NamingThreadFactory(threadName));
		pool.setKeepAliveTime(60, TimeUnit.SECONDS);
		pool.setMaximumPoolSize(maxThreadNum);
		return pool;
	}

}
