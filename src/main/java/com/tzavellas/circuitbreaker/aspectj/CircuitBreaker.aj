package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;


/**
 * Implements the Circuit Breaker stability design pattern using AspectJ.
 * 
 * <p>Each {@code CircuitBreaker} instance is associated with an instance of
 * a target object as defined by the {@code circuit()} pointcut.</p>
 * 
 * @see CircuitBreakerAspectSupport
 * 
 * @author spiros
 */
public abstract aspect CircuitBreaker extends CircuitBreakerAspectSupport perthis(circuit()) {
	
	/** A pointcut to specify the target object. */
	public abstract pointcut circuit();
	
	/** A public method execution of a target object. */
	pointcut circuitExecution() : circuit() && execution(public * *(..));
	
	/** An instantiation of a target object. */
	pointcut circuitInitialization(Object o) : circuit() && initialization(*.new(..)) && this(o);
	
	// This is needed because initialization() can match multiple constructor
	// calls when initializing objects that use inheritance. 
	private boolean needsInitialization = true;
	
	after(Object o) : circuitInitialization(o) {
		if (needsInitialization) {
			onTargetInitialization(o);
			needsInitialization = false;
		}
	}

	before() : circuitExecution() {
		checkIfTheCircuitIsOpenAndPreventExecution();
	}
	
	after() returning : circuitExecution() {
		checkIfTheCircuitIsHalfOpenAndClose();
	}
	
	after() throwing(Throwable e) : circuitExecution() {
		if (! (e instanceof OpenCircuitException))
			recordFailureAndIfHalfOpenThenOpen(e);
	}	
}