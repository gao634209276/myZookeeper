package zookeeper.demo.Client;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class AdminClient implements Watcher {

	static ZooKeeper zk;
	public String hostPort;

	AdminClient(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException {
		zk.close();
	}

	void listStat() throws KeeperException, InterruptedException {
		try {
			Stat stat = new Stat();
			byte masterData[] = zk.getData("/master", false, stat);
			Date startDate = new Date(stat.getCtime());
			System.out.println("Master: " + new String(masterData) + " since "
					+ startDate);
		} catch (KeeperException e) {
			System.out.println("No master");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Workers:");
		for (String w : zk.getChildren("/workers", false)) {
			byte data[] = zk.getData("/workers" + w, false, null);
			String state = new String(data);
			System.out.println("\t" + w + ": " + state);
		}
		System.out.println("Task: ");
		for (String t : zk.getChildren("/assign", false)) {
			System.out.println("\t" + t);
		}
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
	}

	public static void main(String[] args) throws KeeperException,
			InterruptedException, IOException {
		AdminClient c = new AdminClient(args[0]);
		c.startZK();
		c.listStat();
	}

}
