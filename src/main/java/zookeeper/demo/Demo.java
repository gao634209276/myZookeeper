package zookeeper.demo;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

/**
 * Page58
 */
public class Demo {
	static ZooKeeper zk;
	DataCallback masterCheckCallBack = new DataCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				checkMaster();
				break;

			default:
				break;
			}
		}
		void checkMaster() {
			zk.getData("/master", false, masterCheckCallBack,null);
		}
	};
}
