package zookeeper.api.test;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestZK {

	static String connectString = "127.0.0.1:2181";
	static int sessionTimeout = 2000;
	ZooKeeper zk;
	Watcher watch = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			System.out.println("已经触发了even:path " + event.getPath() + " type:"
					+ event.getType() + " stat:" + event.getState());
		}
	};

	@Before
	public void before() throws Exception {
		zk = new ZooKeeper(connectString, sessionTimeout, watch);
	}

	@Test
	public void testCreate() throws Exception {

		String path = "/test";
		byte[] data = "data".getBytes();
		// ACL :access control list
		// CreateMode : presistent,ephemeral
		String retPath = zk.create(path, data, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		System.out.println(retPath);
	}

	@Test
	public void testGet() throws Exception {
		Stat stat = new Stat();
		String path = "/test";
		byte[] b = zk.getData(path, watch, stat);
		System.out.println(new String(b));

		States s = zk.getState();
		System.out.println(s.toString());

		// Test getChildren
		List<String> children = zk.getChildren(path, watch);
		for (String child : children) {
			System.out.println(child);
		}
	}

	@Test
	public void testDelete() throws Exception {

		String path = "/test";
		// 在ZK CLI中通过get查看/test version
		int version = 0;
		zk.delete(path, version);
	}

	// Watch的process回调机制:只回调一次
	// 所以每次操作之前都需要重新设置Watch
	@Test
	public void testWatch() throws KeeperException, InterruptedException {
		Stat stat = new Stat();
		String path = "/test";
		int version = 0;
		// 第一个Watch在new zk的时候触发,所以测试set时候,需要预先再次使用get进行设置Watch
		byte[] b = zk.getData(path, watch, stat);
		System.out.println(new String(b));
		zk.setData(path, "new Data".getBytes(), version);
	}

	@After
	public void after() throws InterruptedException {
		System.out.println("close...");
		zk.close();
	}
}
