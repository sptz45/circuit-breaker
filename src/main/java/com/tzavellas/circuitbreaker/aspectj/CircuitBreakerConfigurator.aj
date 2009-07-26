package com.tzavellas.circuitbreaker.aspectj;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	
	private Set<Class<? extends Throwable>> ignoredExceptions = new HashSet<Class<? extends Throwable>>();
	private boolean enableJmx = false;
	
	after(CircuitBreaker breaker) : initialization(CircuitBreaker.new(..)) && this(breaker) {
		breaker.setEnableJmx(enableJmx);
		for (Class<? extends Throwable> ec: ignoredExceptions)
			breaker.ignoreException(ec);
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
	
	/**
	 * Set the collection of exceptions that when thrown, as a result of a
	 * method execution of the target object, the failure counter will not
	 * get incremented.
	 * 
	 * @param ignored the set of exceptions to ignore
	 */
	@SuppressWarnings("unchecked")
	public void setIgnoredExceptions(Set<Class<? extends Throwable>> ignored) {
		if (ignored == null) {
			ignoredExceptions = Collections.EMPTY_SET;
		} else {
			ignoredExceptions = ignored;
		}
	}
}
