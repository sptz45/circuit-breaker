package com.tzavellas.circuitbreaker.spring;

import javax.annotation.PostConstruct;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.support.AbstractCircuitConfiguration;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.util.Duration;

abstract public class ConfigurableCircuitBreakerAspectSupport extends CircuitBreakerAspectSupport {
	
	private CircuitConfiguration config;
	
	public ConfigurableCircuitBreakerAspectSupport() {
		config = new CircuitConfiguration(circuit);
	}
	
	@PostConstruct
	public void configure() throws Exception {
		config.configure();
	}
	public void setCurrentFailuresDuration(Duration d) {
		config.setCurrentFailuresDuration(d);
	}
	public void setMaxFailures(int maxFailures) {
		config.setMaxFailures(maxFailures);
	}
	public void setTimeoutMillis(long timeoutMillis) {
		config.setTimeoutMillis(timeoutMillis);
	}
}


class CircuitConfiguration extends AbstractCircuitConfiguration {
	
	private CircuitInfo circuitInfo;
	
	CircuitConfiguration(CircuitInfo ci) { circuitInfo = ci; }
	
	public CircuitInfo getCircuitInfo() { return circuitInfo; }
}