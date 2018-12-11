/**
 * 
 */
package net.xingws.common.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * module to inject the zookeeper information
 * @author benxing
 *
 */
public class ZooKeeperModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//local host for standalone zookeeper
		this.bind(String.class).annotatedWith(Names.named("Zookeeper connection string")).toInstance("redis-01:2181,redis-2:2181,redis-3:2181");
		//client connection timeout is 5s
		this.bind(Integer.class).annotatedWith(Names.named("Zookeeper connection timeout")).toInstance(5000);
	}
}
