/**
 * 
 */
package net.xingws.LeaderLatch;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import net.xingws.common.guice.module.ZooKeeperModule;

/**
 * @author benxing
 *
 */
public class LeaderLatchModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		this.install(new ZooKeeperModule());
		this.bind(Integer.class).annotatedWith(Names.named("interval")).toInstance(1000);
		this.bind(Integer.class).annotatedWith(Names.named("maxium retries")).toInstance(3);
	}

}
