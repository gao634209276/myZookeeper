package zookeeper.Demo.Master;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Master1 implements Watcher {
	ZooKeeper zk;
	String hostPort;

	Master1(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException{
		zk.close();
	}
	@Override
	public void process(WatchedEvent event) {
		System.out.println("Hello");
		System.out.println(event);
		System.out.println("event");
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {
		//Master m = new Master(args[0]);
		Master1 m = new Master1("127.0.0.1:2181");
		
		m.startZK();
		// wairt for a bit
		Thread.sleep(60000);
		m.stopZK();
	}

}
