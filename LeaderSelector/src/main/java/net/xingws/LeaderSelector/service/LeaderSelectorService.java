package net.xingws.LeaderSelector.service;

import net.xingws.common.exception.XingwsServiceException;
import net.xingws.common.service.Service;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

public class LeaderSelectorService extends LeaderSelectorListenerAdapter
        implements Service {
    private static final Logger logger = LoggerFactory.getLogger(LeaderSelectorService.class);
    private String connectionString = "127.0.0.1:2181";
    private CuratorFramework client;
    private int timeInterval = 1000;
    private int maxRetries = 3;
    private LeaderSelector selector;
    private String leaderPath = "/master";

    @Inject
    public LeaderSelectorService(@Named("Zookeeper connection string") String connectionString,
                                 @Named("interval") int timeInterval,
                                 @Named("maxium retries") int maxRetries) {
        this.connectionString = connectionString;
        this.timeInterval = timeInterval;
        this.maxRetries = maxRetries;
    }

    @Override
    public void takeLeadership(CuratorFramework arg0) throws Exception {
        logger.info("take the leadership");
        Thread.sleep(10000);
        logger.info("give up the leadership");
    }

    @Override
    public void initialize() throws XingwsServiceException {
        client = CuratorFrameworkFactory.newClient(this.connectionString,
                new ExponentialBackoffRetry(this.timeInterval, this.maxRetries));

        try {
            client.getConnectionStateListenable().addListener(this);
            client.start();
            if (!client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                logger.error("Failed to connect to server");
                throw new XingwsServiceException("Failed to connect to server");
            }

            logger.info("Connect to server successfully");
        } catch (Exception e) {
            logger.error("Connection Error", e);
            throw new XingwsServiceException(e);
        }
    }

    @Override
    public void start() throws XingwsServiceException {
        logger.info("Service is starting");
        try {
            selector = new LeaderSelector(this.client, this.leaderPath, this);
            selector.autoRequeue();
            selector.start();
            logger.info("Service is started");
        } catch (Exception e) {
            logger.error("Error in starting service", e);
            throw new XingwsServiceException(e);
        }
    }

    @Override
    public void stop() throws XingwsServiceException {
        if (selector != null) {
            selector.close();
        }

        logger.info("Service is stopped");
    }

    @Override
    public void destroy() throws XingwsServiceException {
        if (this.client != null) {
            this.client.close();
        }

        logger.info("Service is destroyed");
    }

}
