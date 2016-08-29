package zookeeper.Demo.Client;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class Client implements Watcher {
	static ZooKeeper zk;
	public String hostPort;

	Client(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException {
		zk.close();
	}

	String name;

	String queueCommand(String command) throws Exception {
		while (true) {
			try {
				name = zk.create("/task.task-", command.getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
				return name;
				// break;
			} catch (KeeperException e) {
				throw new Exception(name + "alread appears to be running");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void process(WatchedEvent event) {

		System.out.println(event);
	}

	public static void main(String[] args) throws Exception {
		Client c = new Client(args[0]);
		c.startZK();
		String name = c.queueCommand(args[1]);
		System.out.println("Create " + name);
	}

}
