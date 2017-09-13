package curator;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 对于复杂得得任务，仅需要集群中一台服务器进行处理，可以进行master选举
 */
public class MasterSelectClient {

	static String connectString = "10.1.65.32:2181,10.1.65.31:2181,10.1.65.30:2181";
	static String master_path = "/master";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString(connectString)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	public static void main(String[] args) throws Exception {
		client.start();
		LeaderSelector selector = new LeaderSelector(client, master_path, new LeaderSelectorListenerAdapter() {

			@Override
			public void takeLeadership(CuratorFramework client) throws Exception {
				//执行完后，后续的才开始获取锁，执行改方法
				System.out.println("client 1 成为 master");
				Thread.sleep(6000);
				System.out.println("client 1 完成操作，释放 master权利");
			}
		});
		//释放master后，重新排队
		selector.autoRequeue();
		selector.start();
		Thread.sleep(Integer.MAX_VALUE);
	}

}