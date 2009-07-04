package com.tzavellas.circuitbreaker;

/**
 * An exception that gets thrown in response to an open circuit.
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
	 * Get the {@code Circuit} that opened or is open and caused this
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
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
