/**
 * 
 */
package net.xingws.common.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import net.xingws.common.exception.XingwsServiceException;

/**
 * @author benxing
 *
 */
public abstract class ZooKeeperService implements Service, Watcher {
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperService.class);
	private String serviceName = UUID.randomUUID().toString();
	private String connectionString = null;
	private int connectionTimeout = 5000;
	private ZooKeeper zkServer = null;
	
	public ZooKeeperService(String connectionString, int connectionTimeout) {
		this.connectionString = connectionString;
		this.connectionTimeout = connectionTimeout;
	}
	
	@Override
	public void initialize() throws XingwsServiceException {
		logger.info(serviceName + " is initializing");
		
		try {
			this.zkServer = new ZooKeeper(this.connectionString, this.connectionTimeout, this);
		} catch (IOException e) {
			logger.error("initialize is failed", e);
			throw new XingwsServiceException(e);
		}

	}

	@Override
	public void start() throws XingwsServiceException{
		logger.info(serviceName + " is starting");
	}

	@Override
	public void stop() throws XingwsServiceException {
		logger.info(serviceName + " is stopping");
	}

	@Override
	public void destroy() throws XingwsServiceException{
		logger.info(serviceName + " is destroing");
		try {
			this.zkServer.close();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new XingwsServiceException(e);
		}
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the zkServer
	 */
	public ZooKeeper getZkServer() {
		return zkServer;
	}

	/**
	 * @param zkServer the zkServer to set
	 */
	public void setZkServer(ZooKeeper zkServer) {
		this.zkServer = zkServer;
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("Recieve Session event for " + this.serviceName);
		logger.info("Event path = " + event.getPath());
		logger.info("Event type = " + event.getType());
		logger.info("Keeper state = " + event.getState());		
		if(event.getState() == Watcher.Event.KeeperState.Expired) {
			logger.info("Session expired restarting service");
			this.restartService();
		}
	}
	
	protected abstract void restartService();
}
