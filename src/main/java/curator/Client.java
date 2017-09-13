package curator;

/**
 * Created by noah on 17-9-13.
 */

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;


public class Client {
	private static String connectString = "10.1.65.32:2181,10.1.65.31:2181,10.1.65.30:2181";
	private static CuratorFramework client = null;

	public static void main(String[] args) {
		try {
			client = createSimple(connectString);
			client.start();
			MyConnectionStateListener stateListener = new MyConnectionStateListener("/app2", "this is recon");
			client.getConnectionStateListenable().addListener(stateListener);
			/**
			 * 监控当前节点数据变化
			 */
			final NodeCache nodeCache = new NodeCache(client, "/app2", false);
			nodeCache.start(true);
			nodeCache.getListenable().addListener(new NodeCacheListener() {

				@Override
				public void nodeChanged() throws Exception {
					System.out.println("current data : " + nodeCache.getCurrentData().getData());
				}
			});

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