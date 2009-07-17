package com.tzavellas.circuitbreaker.support;

import javax.annotation.PostConstruct;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.util.Duration;

abstract public class AbstractCircuitConfiguration implements CircuitConfiguration {

	private long timeoutMillis = CircuitInfo.DEFAULT_TIMEOUT;
	private int maxFailures = CircuitInfo.DEFAULT_MAX_FAILURES;
	private Duration currentFailuresDuration = CircuitInfo.DEFAULT_CURRENT_FAILURES_DURATION;
	
	public abstract CircuitInfo getCircuitInfo();
	
	@PostConstruct
	public void configure() throws Exception {
		CircuitInfo circuitInfo = getCircuitInfo();
		circuitInfo.setMaxFailures(maxFailures);
		circuitInfo.setTimeoutMillis(timeoutMillis);
		circuitInfo.setCurrentFailuresDuration(currentFailuresDuration);
	}	
	
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	
	public void setMaxFailures(int maxFailures) {
		this.maxFailures = maxFailures;
	}
	
	public void setCurrentFailuresDuration(Duration d) {
		currentFailuresDuration = d;
	}
}
