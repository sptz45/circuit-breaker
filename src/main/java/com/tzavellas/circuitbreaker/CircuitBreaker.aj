package com.tzavellas.circuitbreaker;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implements the CircuitInfo Breaker stability design pattern.
 * 
 * <p>Each {@code CircuitBreaker} instance is associated with an instance of
 * a circuit object as defined by the {@code circuit()} pointcut.
 * 
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

		if (circuit.isHalfOpen())
			circuit.close();

		circuit.recordCall();
	}
	
	after() throwing(Throwable e) : circuitExecution() {
		if (! ignoredExceptions.contains(e.getClass())) {
			circuit.recordFailure();
		}
	}
	
	// -----------------------------------------------------------------------

	private CircuitInfo circuit;
	private CircuitJmxRegistrar registrar;
	
	private final Set<Class<? extends Throwable>> ignoredExceptions = new CopyOnWriteArraySet<Class<? extends Throwable>>();
	private boolean enableJmx = false;
	
	// -----------------------------------------------------------------------
	
	/**
	 * Get the circuit of this circuit breaker.
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