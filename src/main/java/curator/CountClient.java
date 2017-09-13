package curator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * 分布式计数器
 *
 */
public class CountClient {

	static String connectString = "10.1.65.32:2181,10.1.65.31:2181,10.1.65.30:2181";
	static String path = "/lock";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString(connectString)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	public static void main(String[] args) throws Exception {
		client.start();
		DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, path, new RetryNTimes(3, 1000));
		AtomicValue<Integer> rc = atomicInteger.add(8);
		System.out.println("Result: " + rc.succeeded());
	}

}