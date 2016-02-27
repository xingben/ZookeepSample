package net.xingws.LeaderLatch;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xingws.common.exception.XingwsServiceException;
import net.xingws.common.service.Service;

public class LeaderLatchService implements Service, LeaderLatchListener, ConnectionStateListener {
	private static final Logger logger = LoggerFactory.getLogger(LeaderLatchService.class);
	private String connectionString = "127.0.0.1:2181";
	private CuratorFramework client;
	private int timeInterval = 1000;
	private int maxRetries = 3;
	private LeaderLatch latch;
	private String leaderPath = "/master";
	private String id = UUID.randomUUID().toString();
	
	@Inject
	public LeaderLatchService(@Named("Zookeeper connection string")String connectionString, 
			@Named("interval") int timeInterval, 
			@Named("maxium retries") int maxRetries) {
		this.connectionString = connectionString;
		this.timeInterval = timeInterval;
		this.maxRetries = maxRetries;
	}
	
	@Override
	public void initialize() throws XingwsServiceException {
		client = CuratorFrameworkFactory.newClient(this.connectionString, 
				new ExponentialBackoffRetry(this.timeInterval, this.maxRetries));
		
		try {
			client.getConnectionStateListenable().addListener(this);
			client.start();
			client.getChildren().usingWatcher(warcher).forPath(this.leaderPath);
			client.getZookeeperClient().blockUntilConnectedOrTimedOut();
			
		} catch (Exception e) {
			logger.error("Connection Error", e);
			throw new XingwsServiceException(e);
		}
	}

	@Override
	public void start() throws XingwsServiceException {
		logger.info("Service is starting");
		try {
			latch = new LeaderLatch(this.client, this.leaderPath, this.id);
			latch.addListener(this);
			latch.start();
		} catch (Exception e) {
			logger.error("Error in starting service", e);
			throw new XingwsServiceException(e);
		}
	}

	@Override
	public void stop() throws XingwsServiceException {
		logger.info("Service is stoping");
		this.latch.removeListener(this);
		closeLatch();
		this.client.getConnectionStateListenable().removeListener(this);
		client.close();
	}

	@Override
	public void destroy() throws XingwsServiceException {
		this.latch.removeListener(this);
		closeLatch();
		this.client.getConnectionStateListenable().removeListener(this);
		client.close();
	}

	private void closeLatch() {
		try {
			if(latch.getState() == LeaderLatch.State.STARTED)
				latch.close();
		} catch (IOException e) {
			logger.error("Error in starting service", e);
		}	
	}
	@Override
	public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void isLeader() {
		logger.info("Getting the leadership");
		
	}

	@Override
	public void notLeader() {
		logger.info("lost leadership");
	}
	
	CuratorWatcher warcher = new CuratorWatcher() {
		@Override
		public void process(WatchedEvent event) throws Exception {
			logger.info(event.toString());
			client.getChildren().usingWatcher(this).forPath("/master");			
		}
	};
}
