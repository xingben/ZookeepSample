/**
 * 
 */
package net.xingws.common.exception;

/**
 * @author benxing
 *
 */
public class XingwsServiceException extends Exception {

	private static final long serialVersionUID = -5278898190508941907L;

	public XingwsServiceException() {
	}

	/**
	 * @param message
	 */
	public XingwsServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public XingwsServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public XingwsServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public XingwsServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
