package com.tzavellas.circuitbreaker.jmx;

import java.util.Date;

import com.tzavellas.circuitbreaker.util.Duration;

public interface CircuitBreakerMBean {
	
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
	 * The number of failures after the circuit opens.
	 */
	int getMaxFailures();
	
	/**
	 * Set the number of failures after the circuit opens.
	 */
	void setMaxFailures(int n);
	
	/**
	 * The timeout after which the circuit closes.
	 */
	String getTimeout();
	
	/**
	 * Set the timeout after which the circuit closes.
	 * 
	 * @param timeout a String formated Duration
	 * 
	 * @see Duration
	 */
	void setTimeout(String timeout);
	
	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration a string representing a {@code Duration} object.
	 * 
	 * @see Duration
	 */
	void setCurrentFailuresDuration(String duration);
	
	/**
	 * Set the duration after which a method execution is considered a failure.
	 * 
	 * @param d a String formated Duration or <code>null</code> if you want to disable
	 *          the tracking of execution time.
	 *          
	 * @see Duration
	 */
	void setMaxMethodDuration(String duration);
}
