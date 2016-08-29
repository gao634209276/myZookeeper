package zookeeper.Demo.Master;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;
import org.jboss.netty.channel.socket.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Master4 implements Watcher {
	private static final Logger LOG = LoggerFactory.getLogger(Master4.class);
	ZooKeeper zk;
	String hostPort;
	String serverId = Integer.toHexString(new Random().nextInt());

	public Master4(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	@Override
	public void process(WatchedEvent event) {
		LOG.info(event.toString() + ", " + hostPort);
	}

	void stopZK() throws InterruptedException {
		zk.close();
	}

	// *****************************************************************//
	// Master
	// *****************************************************************//

	// StringCallback有服务端回调,客户端执行
	StringCallback masterCreateCallback = new StringCallback() {
		// processResult对rc进行解析,分类处理
		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			// 连接中断异常-->check
			case CONNECTIONLOSS:
				checkMaster();
				break;
			// 返回OK-->是Leader
			case OK:
				isLeader = true;
				break;
			// 其他-->不是Leader
			default:
				isLeader = false;
				break;
			}
			System.out.println("I am " + (isLeader ? "" : "not ")
					+ "the leader");
		}

	};
	// DataCallback与StringCallback类似,只是用于getData中
	DataCallback masterCheckCallBack = new DataCallback() {
		@SuppressWarnings("incomplete-switch")
		@Override
		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			// 仍然是连接中断将会不断迭代
			case CONNECTIONLOSS:
				checkMaster();
				return;
				// 如果是没有Leader,执行注册
			case NONODE:
				runForMaster();
				return;
			}
		}
	};

	// 通过DataCallback masterCheckCallBack回调进行检测
	void checkMaster() {
		zk.getData("/master", false, masterCheckCallBack, null);
	}

	// 异步调用create,传入回调对象DataCallback masterCheckCallBack
	// 当发生create以后,在服务器执行processResult回调-->客户端进行响应
	void runForMaster() {
		zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL, masterCreateCallback, null);
		isLeader = true;
	}

	public static void main(String[] args) throws InterruptedException {
		Master3 m = new Master3("127.0.0.1:2137");
		// 完成create操作的Client是Master
		m.runForMaster();
		m.stopZK();
	}

	// ******************************************************************//
	// Data
	boolean isLeader = false;
	StringCallback createParentCallback = new StringCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				createParent(path, (byte[]) ctx);
				break;
			case OK:
				LOG.info("Parent created");
				break;
			case NODEEXISTS:
				LOG.warn("Parent alread registered: " + path);
				break;
			default:
				LOG.error("Something went wrong: "
						+ KeeperException.create(Code.get(rc), path));
				break;
			}
		}
	};

	void createParent(String path, byte[] data) {
		zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
				createParentCallback, data);

	}

	public void bootstrap() {
		createParent("/workers", new byte[0]);
		createParent("/assign", new byte[0]);
		createParent("/tasks", new byte[0]);
		createParent("/status", new byte[0]);
	}

}
