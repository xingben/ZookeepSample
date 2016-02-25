/**
 * 
 */
package net.xingws.task.assignment.engine.service;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.xingws.common.exception.XingwsServiceException;
import net.xingws.common.guice.module.ZooKeeperModule;

/**
 * @author benxing
 *
 */
public class EntryPoint implements Daemon {
	private MasterService service = null;
	
	@Override
	public void destroy() {
		try {
			service.destroy();
		} catch (XingwsServiceException e) {
		}
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		Injector injector = Guice.createInjector(new ZooKeeperModule());
		service = injector.getInstance(MasterService.class);
		service.initialize();
	}

	@Override
	public void start() throws Exception {
		service.start();
	}

	@Override
	public void stop() throws Exception {
		service.stop();
	}

	/**
	 * @param args
	 * @throws Exception 
	 * @throws DaemonInitException 
	 */
	public static void main(String[] args) throws DaemonInitException, Exception {
		EntryPoint entry = new EntryPoint();
		entry.init(null);
		entry.start();
		Thread.sleep(5000000);
	}
}
