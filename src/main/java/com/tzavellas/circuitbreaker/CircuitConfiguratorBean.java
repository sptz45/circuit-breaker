package com.tzavellas.circuitbreaker;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

/**
 * A class to configure individual instances of the {@code CircuitBreaker} aspect.
 * 
 * <p>To configure a circuit breaker you first specify the configuration values using this class'
 * property setters and then call the {@code CircuitConfiguratorBean#configure()}</p> method to apply
 * the configuration to the aspect.</p>
 * 
 * <p>This class is designed to be used with dependency injection frameworks. All
 * the configuration is done via Javabean properties and the {@code CircuitConfiguratorBean#configure()}
 * method is annotated with {@code @PostConstruct} so when used with the Spring Framework it is called
 * automatically.</p> 
 * 
 * @author spiros
 */
public class CircuitConfiguratorBean {
	
	private long timeoutMillis = CircuitInfo.DEFAULT_TIMEOUT;
	private int maxFailures = CircuitInfo.DEFAULT_MAX_FAILURES;
	private Duration currentFailuresDuration = CircuitInfo.DEFAULT_CURRENT_FAILURES_DURATION;
	private Class<? extends CircuitBreaker> aspectClass;
	private Object circuit;
	
	/**
	 * Apply the configuration to the circuit breaker aspect.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	@PostConstruct
	public void configure() throws Exception {
		Method m = aspectClass.getMethod("aspectOf", Object.class);
		CircuitBreaker cb = (CircuitBreaker) m.invoke(null, circuit);
		CircuitInfo c = cb.getCircuitInfo();
		c.setMaxFailures(maxFailures);
		c.setTimeoutMillis(timeoutMillis);
		c.setCurrentFailuresDuration(currentFailuresDuration);
	}
	
	/**
	 * Set the class of the CircuitInfo Breaker aspect.
	 */
	public void setAspectClass(Class<? extends CircuitBreaker> aspectClass) {
		this.aspectClass = aspectClass;
	}
	
	/**
	 * Set the circuit object of the circuit breaker (as defined by the circuit()
	 * pointcut).
	 */
	public void setCircuit(Object circuit) {
		this.circuit = circuit;
	}
	
	/**
	 * Set the timeout in milliseconds before the circuit closes again.
	 */
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	
	/**
	 * Set the maximum number of failures that must occur before the
	 * circuit opens. 
	 */
	public void setMaxFailures(int maxFailures) {
		this.maxFailures = maxFailures;
	}
	
	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duratin, default is 1 hour.
	 * @see Duration.Editor
	 */
	public void setCurrentFailuresDuration(Duration d) {
		currentFailuresDuration = d;
	}
}
