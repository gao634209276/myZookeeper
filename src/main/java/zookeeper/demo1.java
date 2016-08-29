package zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class demo1 {
	// 会话超时时间，设置为与系统默认时间一致
	private static final int SESSION_TIMEOUT = 30000;

	// 创建 ZooKeeper 实例
	ZooKeeper zk;

	// 创建 Watcher 实例
	// Watcher指定events处理程序类必须实现的公共接口
	// zkClient 通过链接zkServer获得各种events
	// 一个app可以通过client注册一个回调对象处理这些events
	// 回调对象是Watcher的实现类实例
	Watcher wh = new Watcher() {
		public void process(org.apache.zookeeper.WatchedEvent event) {
			System.out.println(event.toString());
		}
	};

	// 初始化 ZooKeeper 实例
	private void createZKInstance() throws IOException {
		zk = new ZooKeeper("localhost:2181", demo1.SESSION_TIMEOUT, this.wh);

	}

	private void ZKOperations() throws IOException, InterruptedException,
			KeeperException {
		System.out.println("/n1. 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限：OPEN_ACL_UNSAFE ，节点类型： Persistent");
		zk.create("/zoo2", "myData2".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);

		System.out.println("/n2. 查看是否创建成功： ");
		System.out.println(new String(zk.getData("/zoo2", false, null)));

		System.out.println("/n3. 修改节点数据 ");
		zk.setData("/zoo2", "shenlan211314".getBytes(), -1);

		System.out.println("/n4. 查看是否修改成功： ");
		System.out.println(new String(zk.getData("/zoo2", false, null)));

		System.out.println("/n5. 删除节点 ");
		zk.delete("/zoo2", -1);

		System.out.println("/n6. 查看节点是否被删除： ");
		System.out.println(" 节点状态： [" + zk.exists("/zoo2", false) + "]");
	}

	private void ZKClose() throws InterruptedException {
		zk.close();
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, KeeperException {
		demo1 dm = new demo1();
		dm.createZKInstance();
		dm.ZKOperations();
		dm.ZKClose();
	}
}