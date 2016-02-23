/**
 * 
 */
package net.xingws.common.service;

import net.xingws.common.exception.XingwsServiceException;

/**
 * service interface will be implemented by different service as client, master, engine and etc 
 * @author benxing
 *
 */

public interface Service {
	/**
	 * initialize the service.  
	 * @throws XingwsServiceException
	 */
	void initialize() throws XingwsServiceException;
	
	/**
	 *start the service. 
	 * @throws XingwsServiceException
	 */
	void start()throws XingwsServiceException;
	
	/**
	 *stop the service. 
	 * @throws XingwsServiceException
	 */
	void stop()throws XingwsServiceException;
	
	/**
	 *destroy and cleanup the service 
	 * @throws XingwsServiceException
	 */
	void destroy()throws XingwsServiceException;
}
