package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * PathChildrenCache  --监控当前节点子节点的变化 新增 数据改变 删除
 * 1.client 客户端实例
 * 2.path 数据节点的路径
 * 3.dataIsCompressed 是否进行数据压缩
 * 4.cacheDate 是否把节点内容缓存起来，如果true，收到节点的数据内容同时也能够获取节点的数据内容
 * 5.threadFactory and executor Service 构造单独线程池处理事件通知
 */
public class Client2 {

	private static String connectString = "10.1.65.32:2181,10.1.65.31:2181,10.1.65.30:2181";
	private static CuratorFramework client = null;
	private static CountDownLatch cl = new CountDownLatch(1);

	public static void main(String[] args) {
		try {
			client = createSimple(connectString);
			client.start();
//          MyConnectionStateListener stateListener = new MyConnectionStateListener("/app2", "this is recon");
//          client.getConnectionStateListenable().addListener(stateListener);
			/**
			 * 监控当前节点子节点的变化 新增 数据改变 删除
			 */
			PathChildrenCache cache = new PathChildrenCache(client, "/app2", true);
			cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
			cache.getListenable().addListener(new PathChildrenCacheListener() {

				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
						throws Exception {
					switch (event.getType()) {
						case CHILD_ADDED:
							System.out.println("add:" + event.getData().getPath());
							break;
						case CHILD_REMOVED:
							System.out.println("remove:" + event.getData().getPath());
							break;
						case CHILD_UPDATED:
							System.out.println("data change");
							break;
						case CONNECTION_LOST:
							System.out.println("lost");
							break;
						case CONNECTION_RECONNECTED:
							System.out.println("recon");
							break;
						case CONNECTION_SUSPENDED:
							System.out.println("susp");
							break;
						default:
							break;
					}
				}
			});
			cl.await();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CuratorFramework createSimple(String connects) {
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
		//DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout", 60 * 1000);
		//DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout", 15 * 1000);
		return CuratorFrameworkFactory.newClient(connects, retryPolicy);
	}

	public static CuratorFramework createWithOptions(String connects, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) {
		// using the CuratorFrameworkFactory.builder() gives fine grained control
		// over creation options. See the CuratorFrameworkFactory.Builder javadoc details
		return CuratorFrameworkFactory.builder().connectString(connects)
				.retryPolicy(retryPolicy)
				.connectionTimeoutMs(connectionTimeoutMs)
				.sessionTimeoutMs(sessionTimeoutMs)
				// etc. etc.
				.build();
	}

}