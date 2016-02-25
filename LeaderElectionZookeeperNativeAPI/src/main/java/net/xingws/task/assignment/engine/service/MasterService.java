/**
 * 
 */
package net.xingws.task.assignment.engine.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import net.xingws.common.exception.XingwsServiceException;
import net.xingws.common.service.ZooKeeperService;

/**
 * @author benxing
 *
 */
public class MasterService extends ZooKeeperService {
	private static final Logger logger = LoggerFactory.getLogger(MasterService.class);
	//constants for zookeeper master node
	public static final String MASTER_NODE = "/master";
	
	/**
	 * @param serviceName
	 * @param connectionString
	 * @param connectionTimeout
	 */

	@Inject
	public MasterService(@Named("Zookeeper connection string") String connectionString, @Named("Zookeeper connection timeout") int connectionTimeout) {
		super(connectionString, connectionTimeout);
	}

	@Override
	public void initialize() throws XingwsServiceException {
		super.initialize();
		this.competeForLeader();
		
	}
	
	private void competeForLeader() {
		this.getZkServer().create(MASTER_NODE, 
				this.getServiceName().getBytes(), 
				Ids.OPEN_ACL_UNSAFE, 
				CreateMode.EPHEMERAL, 
				this.competeForLeaderCallback, 
				null);
	}
	
	private void checkLeaderShip() {
		this.getZkServer().getData("/AssignmentEngine", 
				false, 
				this.checkLeaderShipCallback, 
				this.getServiceName());
	}
	
	private void leaderExists() {
		this.getZkServer().exists(MASTER_NODE, leaderExistsWatcher, leaderExistsCallback, null);
	}
	
	StringCallback competeForLeaderCallback = new StringCallback() {
		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			switch(Code.get(rc)) {
			case CONNECTIONLOSS:
				competeForLeader();
				break;
			case OK:
				logger.info("Taking the leadership");
				break;
			case NODEEXISTS:
				leaderExists();
				break;
			default:
				logger.error("Something went wrong: ",
						KeeperException.create(Code.get(rc), path));
				break;				
			}
		}
	};
	
	DataCallback checkLeaderShipCallback = new DataCallback() {
		@Override
		public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
			switch(Code.get(rc)) {
			case CONNECTIONLOSS:
				checkLeaderShip();
				break;
			case NONODE:
				competeForLeader();
				break;
			case OK:
				if(((String)ctx).equals(new String(data))) {
					logger.info("Taking the leadership");
				}else{
					leaderExists();
				}
				break;
			default:
				logger.error("Something went wrong: ",
						KeeperException.create(Code.get(rc), path));
				break;
			}
		}
	};
	
	StatCallback leaderExistsCallback = new StatCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				leaderExists();
				break;
			case NONODE:
				competeForLeader();
				break;
			default:
				checkLeaderShip();
				break;
			}
		}
		
	};
	
	Watcher leaderExistsWatcher = new Watcher() {

		@Override
		public void process(WatchedEvent event) {
			if(event.getType() == Watcher.Event.EventType.NodeDeleted) {
				competeForLeader();
			}
		}
	};

	
	/* (non-Javadoc)
	 * @see net.xingws.common.service.ZooKeeperService#restartService()
	 */
	@Override
	protected void restartService() {
		try {
			this.destroy();
			this.initialize();
		} catch (XingwsServiceException e) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				//intentional silent
			}
			this.restartService();
		}
	}
}
