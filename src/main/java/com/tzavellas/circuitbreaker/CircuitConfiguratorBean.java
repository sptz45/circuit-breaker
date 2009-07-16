package com.tzavellas.circuitbreaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import com.tzavellas.circuitbreaker.util.Duration;

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
	
	private Method aspectOf;
	private CircuitInfo circuitInfo;
	
	/**
	 * Apply the configuration to the circuit breaker aspect.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	@PostConstruct
	public void configure() throws Exception {
		circuitInfo.setMaxFailures(maxFailures);
		circuitInfo.setTimeoutMillis(timeoutMillis);
		circuitInfo.setCurrentFailuresDuration(currentFailuresDuration);
	}
	
	/**
	 * Set the class of the CircuitInfo Breaker aspect.
	 */
	public void setAspectClass(Class<? extends CircuitBreaker> aspectClass) {
		try {
			aspectOf = aspectClass.getMethod("aspectOf", Object.class);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Wrong aspect class! Could not access static method with signature aspectOf(Object)");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Wrong aspect class! There is no static method with signature aspectOf(Object)");
		}
	}
	
	/**
	 * Set the circuit object of the circuit breaker (as defined by the circuit()
	 * pointcut).
	 */
	public void setCircuit(Object circuit) {
		try {
			circuitInfo = ((CircuitBreaker) aspectOf.invoke(null, circuit)).getCircuitInfo();
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("The specified object has no CircuitBreaker aspect bound!", e);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
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
