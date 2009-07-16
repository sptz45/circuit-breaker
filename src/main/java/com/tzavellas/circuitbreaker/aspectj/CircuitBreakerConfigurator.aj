package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.CircuitBreaker;

/**
 * An aspect that applies default configuration to all {@code CircuitBreaker}
 * aspects as soon as they are instantiated.
 * 
 * <p>If you change this aspect's configuration the changes will not propagate
 * to already instantiated {@code CircuitBreaker} aspects. The new configuration
 * will get applied only to newly instantiated aspects.</p> 
 * 
 * @author spiros
 */
public aspect CircuitBreakerConfigurator {
	
	private boolean enableJmx = false;
	
	after(CircuitBreaker breaker) : initialization(CircuitBreaker+.new(..)) && this(breaker) {
		breaker.setEnableJmx(enableJmx);
	}

	/**
	 * Enables support for JMX configuration and monitoring of the
	 * {@code CircuitBreaker} aspects.
	 * 
	 * @param enable whether to enable JMX, default is false.
	 */
	public void setEnableJmx(boolean enable) {
		enableJmx = enable;
	}
}
