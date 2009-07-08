package com.tzavellas.circuitbreaker;

import java.util.Date;

/**
 * The MBean interface for CircuitInfo objects to JMX.
 * 
 * @author spiros
 */
public interface CircuitInfoMBean {

	/**
	 * Test whether the circuit is closed.
	 */
	boolean isClosed();
	
	/**
	 * Test whether the circuit is open.
	 */
	boolean isOpen();
	
	/**
	 * Test whether the circuit is open.
	 */
	boolean isHalfOpen();
	
	/**
	 * Get the Date the circuit was opened.
	 * 
	 * @return if the circuit is open return the open timestamp else null 
	 */
	Date getOpenTimestamp();
	
	/**
	 * The number of calls being made through the circuit.
	 */
	int getCalls();
	
	/**
	 * The number of total failures.
	 */
	int getFailures();
	
	/**
	 * The number of failures since the circuit was closed. 
	 */
	int getCurrentFailures();
	
	/**
	 * The number of times the circuit has been opened.
	 */
	int getTimesOpened();
	
	/**
	 * The number of failures after the cicuit opens.
	 */
	int getMaxFailures();
	
	/**
	 * Set the number of failures after the cicuit opens.
	 */
	void setMaxFailures(int n);
	
	/**
	 * The timeout after which the circuit closes.
	 */
	long getTimeoutMillis();
	
	/**
	 * Set the timeout after which the circuit closes.
	 */
	void setTimeoutMillis(long millis);
	
	/**
	 * Open the circuit, causing any subsequent calls made through the
	 * circuit to stop throwing an OpenCircuitException.
	 */
	void open();
	
	/**
	 * Close the circuit, allowing any method call to propagate to
	 * their recipients.
	 */
	void close();
}
