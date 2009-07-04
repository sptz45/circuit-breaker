package com.tzavellas.circuitbreaker;

/**
 * An aspect that applies default configuration to all {@code CircuitBreaker}
 * aspects as soon as they are instantiated. 
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
