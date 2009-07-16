package com.tzavellas.circuitbreaker.support;

import javax.annotation.PostConstruct;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.util.Duration;

abstract public class AbstractCircuitConfiguration {

	private long timeoutMillis = CircuitInfo.DEFAULT_TIMEOUT;
	private int maxFailures = CircuitInfo.DEFAULT_MAX_FAILURES;
	private Duration currentFailuresDuration = CircuitInfo.DEFAULT_CURRENT_FAILURES_DURATION;
	
	public abstract CircuitInfo getCircuitInfo();
	
	/**
	 * Apply the configuration to the circuit breaker aspect.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	@PostConstruct
	public void configure() throws Exception {
		CircuitInfo circuitInfo = getCircuitInfo();
		circuitInfo.setMaxFailures(maxFailures);
		circuitInfo.setTimeoutMillis(timeoutMillis);
		circuitInfo.setCurrentFailuresDuration(currentFailuresDuration);
	}	
	
	/**
	 * Set the timeout in milliseconds before the circuit closes again.
	 */
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	
	/**
	 * Set the maximum number of failures that must occur before the
	 * circuit opens. 
	 */
	public void setMaxFailures(int maxFailures) {
		this.maxFailures = maxFailures;
	}
	
	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duratin, default is 1 hour.
	 * @see Duration.Editor
	 */
	public void setCurrentFailuresDuration(Duration d) {
		currentFailuresDuration = d;
	}
}
