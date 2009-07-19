package com.tzavellas.circuitbreaker.support;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.OpenCircuitException;

public class CircuitBreakerAspectSupport {
	
	protected CircuitJmxRegistrar registrar;
	protected CircuitInfo circuit = new CircuitInfo();
	
	protected final Set<Class<? extends Throwable>> ignoredExceptions = new CopyOnWriteArraySet<Class<? extends Throwable>>();
	protected boolean enableJmx = false;
	
	
	// -----------------------------------------------------------------------------
	
	protected void onTargetInitialization(Object target) {
		registrar = new CircuitJmxRegistrar(circuit, target.getClass());
		if (enableJmx)
			registrar.register();
	}
	
	protected void checkIfTheCircuitIsOpenAndPreventExecution() {
		if (circuit.isOpen())
			throw new OpenCircuitException(circuit);
		circuit.recordCall();
	}
	
	protected void checkIfTheCircuitIsHalfOpenAndClose() {
		if (circuit.isHalfOpen())
			circuit.close();
	}
	
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
