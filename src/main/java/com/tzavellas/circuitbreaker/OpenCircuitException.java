package com.tzavellas.circuitbreaker;

/**
 * An exception that gets thrown by a circuit breaker when its circuit is
 * open.
 * 
 * @see CircuitBreaker
 * @see Circuit
 * 
 * @author spiros
 */
public class OpenCircuitException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private final transient Circuit circuit;
	
	/**
	 * Create an OpenCircuitException.
	 * 
	 * @param c the {@code Circuit} that caused this exception.
	 */
	public OpenCircuitException(Circuit c) {
		circuit = c;
	}
	
	/**
	 * Get the {@code Circuit} that is open and caused this
	 * exception to be thrown.
	 * 
	 * @return the {@code Circuit} that caused this exception.
	 */
	public Circuit getCircuit() {
		return circuit;
	}

	/**
	 * Overriden to return null to avoid the cost of creating the stack
	 * trace.
	 * 
	 * <p>This can be done because this exception is used as a control
	 * structure and not to report any error.</p>
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
