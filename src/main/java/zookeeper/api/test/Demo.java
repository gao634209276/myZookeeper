package zookeeper.api.test;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Demo {

	static String connectString = "127.0.0.1:2181";
	static int sessionTimeout = 2000;
	static String path = "/root";
	static Watcher watch = new Watcher() {

		@Override
		public void process(WatchedEvent event) {
			System.out.println(event);
		}
	};
	static Stat stat = new Stat();

	public static void main(String[] args) throws IOException, KeeperException,
			InterruptedException {

		ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, watch);
		byte[] b = zk.getData(path, watch, stat);
		System.out.println(new String(b));
	}
}
