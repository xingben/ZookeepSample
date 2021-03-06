/**
 *
 */
package net.xingws.LeaderSelector.service;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.xingws.common.exception.XingwsServiceException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author benxing
 */
public class EntryPoint implements Daemon {
    private LeaderSelectorService service = null;

    @Override
    public void destroy() {
        try {
            service.destroy();
        } catch (XingwsServiceException e) {
        }
    }

    @Override
    public void init(DaemonContext arg0) throws DaemonInitException, Exception {
        Injector injector = Guice.createInjector(new LeaderSelectorModule());
        service = injector.getInstance(LeaderSelectorService.class);
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
        System.out.println("Press enter/return to quit\n");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        entry.stop();
        entry.destroy();
    }
}
