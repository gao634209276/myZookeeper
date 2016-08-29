package zookeeper.Demo.Master;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Master5 implements Watcher {
	private static final Logger LOG = LoggerFactory.getLogger(Master5.class);
	static ZooKeeper zk;
	public String hostPort;

	Master5(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException {
		zk.close();
	}

	String serverId = Integer.toHexString(new Random().nextInt());
	boolean isLeader = false;
	// StringCallback有服务端回调,客户端执行
	StringCallback masterCreateCallback = new StringCallback() {
		MasterStates state;

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
				state = MasterStates.ELECTED;
				takeLeadership();
				break;
			// 其他-->不是Leader
			case NODEEXISTS:
				state = MasterStates.NOTELECTED;
				masterExists();
				break;
			default:
				state = MasterStates.NOTELECTED;
				LOG.error("Something went wrong when running for master.",
						KeeperException.create(Code.get(rc), path));
			}
			System.out.println("I am " + (isLeader ? "" : "not ")
					+ "the leader");
		}

	};
	MasterStates state;
	StatCallback masterExistsCallback = new StatCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				masterExists();
				break;
			case OK:
				if (stat == null) {
					state = MasterStates.RUNNING;
					runForMaster();
				}
				break;
			default:
				checkMaster();
				break;
			}
		}
	};

	void masterExists() {
		zk.exists("/master", masterExistsWatcher, masterExistsCallback, null);

	}

	void takeLeadership() {

	}

	Watcher masterExistsWatcher = new Watcher() {

		@Override
		public void process(WatchedEvent event) {
			if (event.getType() == EventType.NodeDeleted) {
				assert "/master".equals(event.getPath());
				runForMaster();
			}
		}

	};

	// DataCallback与StringCallback类似,只是用于getData中
	DataCallback masterCheckCallBack = new DataCallback() {
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
				// 其他-->..
			default:
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
		Master5 m = new Master5("127.0.0.1:2137");
		// 完成create操作的Client是Master
		m.runForMaster();
		m.stopZK();
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
	}
}
