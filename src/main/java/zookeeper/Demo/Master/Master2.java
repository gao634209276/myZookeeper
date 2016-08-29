package zookeeper.Demo.Master;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Master2 implements Watcher {
	ZooKeeper zk;
	String hostPort;

	Master2(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException {
		zk.close();
	}

	String serverId = Integer.toHexString(new Random().nextInt());
	static boolean isLeader = false;

	void runForMaster() {
		while (true) {
			try {
				zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				isLeader = true;
				break;
			} catch (KeeperException e) {
				// isLeader = false;
				if (checkMaster())
					break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	boolean checkMaster() {
		while (true) {
			try {
				Stat stat = new Stat();
				byte data[] = zk.getData("/master", false, stat);
				isLeader = new String(data).equals(serverId);
			} catch (KeeperException e) {
				// no master
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Master2 m = new Master2("127.0.0.1:2137");
		m.startZK();
		m.runForMaster();
		if (isLeader) {
			System.out.println("I am the Leader");
			Thread.sleep(60000);
		} else {
			System.out.println("Some else is the leader");
		}
		m.stopZK();
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
	}
}
