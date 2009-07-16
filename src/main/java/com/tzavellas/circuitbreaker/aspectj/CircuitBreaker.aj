package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;


/**
 * Implements the CircuitInfo Breaker stability design pattern.
 * 
 * <p>A circuit breaker wraps an object with "dangerous" operations (such as
 * an integration point) and prevents the re-execution of faulty operations.</p>
 * 
 * <p>A circuit breaker has three states: <i>closed</i>, <i>open</i> and <i>half-open</i>.
 * During normal operation the circuit breaker is <i>closed</i> and lets any method calls
 * propagate to the wrapped object, recording the number of failures (exceptions thrown)
 * that happen from the methods of the wrapped object. When the number of failures exxeeds
 * a configured number then the circuit breaker moves to <i>open</i> state.  In the
 * <i>open</i> state when a call is made through the circuit breaker instead of propagating
 * to the wrapped object, the circuit breaker throws an {@code OpenCircuitException}. After
 * a configurable amount of time the circuit breaker goes to the <i>half-open</i> state. In
 * the <i>half-open</i> state when a call is made it propagates to the wrapped object and if
 * it succeeds the circuit breaker moves to the <i>closed</i> state, else it moves to the
 * <i>open</i> state.</p>
 * 
 * <p>Each {@code CircuitBreaker} instance is associated with an instance of
 * a circuit object as defined by the {@code circuit()} pointcut.</p>
 * 
 * @see CircuitInfo
 * @author spiros
 */
public abstract aspect CircuitBreaker extends CircuitBreakerAspectSupport perthis(circuit()) {
	
	/** A pointcut to specify the circuit object. */
	public abstract pointcut circuit();
	
	/** A public method execution of a circuit object. */
	pointcut circuitExecution() : circuit() && execution(public * *(..));
	
	/** An instantiation of a circuit object. */
	pointcut circuitInitialization(Object o) : circuit() && initialization(*.new(..)) && this(o);
	
	after(Object o) : circuitInitialization(o) {
		onTargetInitialization(o);
	}

	before() : circuitExecution() {
		checkIfTheCircuitIsOpenAndPreventExecution();
	}
	
	after() returning : circuitExecution() {
		checkIfTheCircuitIsHalfOpenAndClose();
	}
	
	after() throwing(Throwable e) : circuitExecution() {
		recordFailureAndIfHalfOpenThenOpen(e);
	}	
}