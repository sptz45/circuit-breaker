package com.tzavellas.circuitbreaker.support;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.OpenCircuitException;

/**
 * An abstract class that provides all the functionality needed to implement the
 * Circuit Breaker stability design pattern using an aspect.
 * 
 * <p>A circuit breaker wraps an object with "dangerous" operations (such as
 * an integration point) and prevents the re-execution of faulty operations.</p>
 * 
 * <p>A circuit breaker has three states: <i>closed</i>, <i>open</i> and <i>half-open</i>.
 * During normal operation the circuit breaker is <i>closed</i> and lets any method calls
 * propagate to the target object, recording the number of failures (exceptions thrown)
 * that happen from the methods of the target object. When the number of failures exceeds
 * a configured number then the circuit breaker moves to <i>open</i> state.  In the
 * <i>open</i> state when a call is made through the circuit breaker instead of propagating
 * to the target object, the circuit breaker throws an {@code OpenCircuitException}. After
 * a configurable amount of time the circuit breaker goes to the <i>half-open</i> state. In
 * the <i>half-open</i> state when a call is made it propagates to the target object and if
 * it succeeds the circuit breaker moves to the <i>closed</i> state, else it moves to the
 * <i>open</i> state.</p>
 * 
 * @see OpenCircuitException
 * @see CircuitInfo
 * 
 * @author spiros
 */ 
public abstract class CircuitBreakerAspectSupport {
	
	protected CircuitJmxRegistrar registrar;
	protected CircuitInfo circuit = new CircuitInfo();
	
	protected final Set<Class<? extends Throwable>> ignoredExceptions = new CopyOnWriteArraySet<Class<? extends Throwable>>();
	private boolean enableJmx = false;
	
	// -----------------------------------------------------------------------------
	
	/**
	 * Perform initialization tasks using the specified target object.
	 * 
	 * <p>To be invoked once, by the extending aspect.</p>
	 */
	protected void onTargetInitialization(Object target) {
		registrar = new CircuitJmxRegistrar(circuit, target);
		if (enableJmx)
			registrar.register();
	}
	
	/**
	 * Check if the circuit breaker is open and if it is throw an
	 * {@code OpenCircuitException}.
	 * 
	 * <p>To be invoked, by the extending aspect, each time a method is called
	 * on the target object.</p>
	 */
	protected void checkIfTheCircuitIsOpenAndPreventExecution() {
		if (circuit.isOpen())
			throw new OpenCircuitException(circuit);
		circuit.recordCall();
	}
	
	/**
	 * Close the circuit breaker if it is half-open.
	 * 
	 * <p>To be invoked, by the extending aspect, for each successful method
	 * call on the target object.</p>
	 */
	protected void checkIfTheCircuitIsHalfOpenAndClose() {
		if (circuit.isHalfOpen())
			circuit.close();
	}
	
	/**
	 * Record a failure and optionally open the circuit breaker.
	 * 
	 * <p>To be invoked, by the extending aspect, each time an exception gets
	 * thrown by the target object.</p>
	 */
	protected void recordFailureAndIfHalfOpenThenOpen(Throwable e) {
		if (! ignoredExceptions.contains(e.getClass())) {
			circuit.recordFailure();
			if (circuit.isHalfOpen()) {
				circuit.open();
			}
		}
	}
	
	
	// -----------------------------------------------------------------------------
	
	/**
	 * Get state and configuration data about this circuit breaker.
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
