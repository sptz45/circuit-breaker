package com.tzavellas.circuitbreaker;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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
public abstract aspect CircuitBreaker perthis(circuit()){
	
	/** A pointcut to specify the circuit object. */
	abstract pointcut circuit();
	
	/** A public method execution of a circuit object. */
	pointcut circuitExecution() : circuit() && execution(public * *(..));
	
	/** An instantiation of a circuit object. */
	pointcut circuitInitialization(Object o) : circuit() && initialization(*.new(..)) && this(o);
	
	after(Object o) : circuitInitialization(o) {
		circuit = new CircuitInfo();
		registrar = new CircuitJmxRegistrar(circuit, o.getClass().getSimpleName());
		if (enableJmx)
			registrar.register();
	}

	before() : circuitExecution() {
		if (circuit.isOpen())
			throw new OpenCircuitException(circuit);
		circuit.recordCall();
	}
	
	after() returning : circuitExecution() {
		if (circuit.isHalfOpen())
			circuit.close();
	}
	
	after() throwing(Throwable e) : circuitExecution() {
		if (! ignoredExceptions.contains(e.getClass())) {
			circuit.recordFailure();
			if (circuit.isHalfOpen()) {
				circuit.open();
			}
		}
	}
	
	// -----------------------------------------------------------------------

	private CircuitInfo circuit;
	private CircuitJmxRegistrar registrar;
	
	private final Set<Class<? extends Throwable>> ignoredExceptions = new CopyOnWriteArraySet<Class<? extends Throwable>>();
	private boolean enableJmx = false;
	
	// -----------------------------------------------------------------------
	
	/**
	 * Get state and configuration data about this cicruit breaker.
	 */
	public CircuitInfo getCircuitInfo() {
		return circuit;
	}
	
	/**
	 * When an exception of the specified type gets thrown as a result of a
	 * method execution of a circuit object do not increment the failure counter.
	 * 
	 * <p>Please note that exception types are tested using equality and
	 * not instanceof.</p>
	 * 
	 * @param ignored the exception to ignore
	 */
	public void ignoreException(Class<? extends Throwable> ignored) {
		ignoredExceptions.add(ignored);
	}
	
	/**
	 * Stop ignoring exceptions of the specified type when recording failures.
	 */
	public void removeIgnoredExcpetion(Class<? extends Throwable> exc) {
		ignoredExceptions.remove(exc);
	}
	
	/**
	 * Register/unregister this circuit breaker to/from JMX.
	 * 
	 * @param enable whether to enable JMX, default is false.
	 */
	public void setEnableJmx(boolean enable) {
		enableJmx = enable;
		if (registrar != null) {
			if (enableJmx)
				registrar.register();
			else
				registrar.unregister();
		}
	}
}