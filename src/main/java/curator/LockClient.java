package curator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 分布式锁
 * @author ywd
 *
 */
public class LockClient {

	static String connectString = "10.1.65.32:2181,10.1.65.31:2181,10.1.65.30:2181";
	static String path = "/lock";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString(connectString)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	public static void main(String[] args) throws Exception {
		client.start();
		final CountDownLatch downLatch = new CountDownLatch(1);
		final InterProcessMutex lock = new InterProcessMutex(client, path);
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable(){

				@Override
				public void run() {
					try {
						downLatch.await();
						//获取锁
						lock.acquire();
					} catch (Exception e) {
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("生成订单号是：" + orderNo);
					try {
						//释放锁
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}).start();
		}
		downLatch.countDown();
	}

}