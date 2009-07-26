package com.tzavellas.circuitbreaker.support;

import com.tzavellas.circuitbreaker.util.Duration;

public interface CircuitConfiguration {

	/**
	 * Set the timeout before the circuit closes again.
	 */
	void setTimeout(Duration timout);

	/**
	 * Set the maximum number of failures that must occur before the
	 * circuit opens. 
	 */
	void setMaxFailures(int maxFailures);

	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duration, default is 1 hour.
	 * @see Duration.Editor
	 */
	void setCurrentFailuresDuration(Duration d);
	
	/**
	 * Register/unregister this circuit breaker to/from JMX.
	 * 
	 * @param enable whether to enable JMX, default is false.
	 */
	public void setEnableJmx(boolean enable);
	
	/**
	 * Set the duration after which a method execution is considered a failure.
	 * 
	 * @param d the duration after which a method execution is considered a
	 *          failure. Default is <code>null</code>, no tracking of execution
	 *          time happens.
	 */
	void setMaxMethodDuration(Duration d);
}