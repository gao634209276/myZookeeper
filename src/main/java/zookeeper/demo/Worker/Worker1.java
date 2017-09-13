package zookeeper.demo.Worker;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;
import org.jboss.netty.channel.socket.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker1 implements Watcher {
	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
	ZooKeeper zk;
	String hostPort;
	String serverId = Integer.toHexString(new Random().nextInt());

	Worker1(String hoString) {
		this.hostPort = hoString;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	@Override
	public void process(WatchedEvent event) {
		LOG.info(event.toString() + ", " + hostPort);
	}

	// Worker

	void register() {
		zk.create("/workers/work-" + serverId, "IDle".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				createWorkerCallback, null);
	}

	StringCallback createWorkerCallback = new StringCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				register();
				break;
			case OK:
				LOG.info("Registered successfully: " + serverId);
				break;
			case NODEEXISTS:
				LOG.warn("Aready registered: " + serverId);
				break;
			default:
				LOG.error("Something went wrong: "
						+ KeeperException.create(Code.get(rc), path));
				break;
			}
		}
	};

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Worker1 w = new Worker1(args[0]);
		w.startZK();
		w.register();
		Thread.sleep(30000);
	}

	// 异步 StatCallback
	StatCallback statusUpdateCallback = new StatCallback() {

		@SuppressWarnings("incomplete-switch")
		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				updateStatus((String) ctx);
				return;
			}
		}

	};
	String status = "";
	String name = "name";

	synchronized private void updateStatus(String status) {
		if (status == this.status) {
			zk.setData("/worker/" + name, status.getBytes(), -1,
					statusUpdateCallback, status);
		}
	}

	public void setStatus(String status) {
		this.status = status;
		updateStatus(status);
	}
	
	
	void test(){
		setStatus("test");
	}
}
