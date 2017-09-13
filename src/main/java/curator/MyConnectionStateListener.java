package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * Created by noah on 17-9-13.
 */
public class MyConnectionStateListener implements ConnectionStateListener {

	public MyConnectionStateListener(String s, String s1) {
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {

	}
}
