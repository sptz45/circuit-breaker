package com.tzavellas.circuitbreaker.support;

import javax.annotation.PostConstruct;

import com.tzavellas.circuitbreaker.util.Duration;

public interface CircuitConfiguration {

	/**
	 * Apply the configuration to the circuit breaker aspect.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	@PostConstruct
	void configure() throws Exception;

	/**
	 * Set the timeout in milliseconds before the circuit closes again.
	 */
	void setTimeoutMillis(long timeoutMillis);

	/**
	 * Set the maximum number of failures that must occur before the
	 * circuit opens. 
	 */
	void setMaxFailures(int maxFailures);

	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duratin, default is 1 hour.
	 * @see Duration.Editor
	 */
	void setCurrentFailuresDuration(Duration d);

}