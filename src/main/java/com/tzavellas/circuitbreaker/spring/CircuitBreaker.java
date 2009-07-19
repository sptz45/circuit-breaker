package com.tzavellas.circuitbreaker.spring;

import java.util.Set;

import javax.annotation.PreDestroy;

import org.aspectj.lang.ProceedingJoinPoint;

import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.circuitbreaker.util.Duration;

/**
 * Implements the Circuit Breaker stability design pattern using spring-aop.
 * 
 * <p>This implementation uses an around advice and can be configured using
 * dependency injection.</p>
 * 
 * @author spiros
 */
public class CircuitBreaker extends CircuitBreakerAspectSupport implements CircuitConfiguration {

	// We need the target object to perform some initialization (JMX registration etc)
	// so we initialize lazily on the first call of the around advice.
	private volatile boolean needsInitialization = true;

	/** The method of the around advice. */
	public Object execute(ProceedingJoinPoint pjp) throws Throwable {
		if (needsInitialization) {
			synchronized (this) {
				if (needsInitialization) {
					onTargetInitialization(pjp.getTarget());
					needsInitialization = false;
				}
			}
		}
		
		checkIfTheCircuitIsOpenAndPreventExecution();
		
		try {
			Object result = pjp.proceed();
			checkIfTheCircuitIsHalfOpenAndClose();
			return result;
			
		} catch (Throwable e) {
			recordFailureAndIfHalfOpenThenOpen(e);
			throw e;
		}
	}
	
	/** {@inheritDoc} */
	public void setMaxFailures(int maxFailures) {
		getCircuitInfo().setMaxFailures(maxFailures);
	}
	/** {@inheritDoc} */
	public void setTimeoutMillis(long timeoutMillis) {
		getCircuitInfo().setTimeoutMillis(timeoutMillis);
	}
	/** {@inheritDoc} */
	public void setCurrentFailuresDuration(Duration d) {
		getCircuitInfo().setCurrentFailuresDuration(d);
	}
	
	/**
	 * Set the collection of exceptions that when thrown, as a result of a
	 * method execution of the target object, the failure counter will not
	 * get incremented.
	 * 
	 * @param ignored the set of exceptions to ignore
	 */
	public void setIgnoredExceptions(Set<Class<? extends Throwable>> ignored) {
		ignoredExceptions.clear();
		ignoredExceptions.addAll(ignored);
	}
	
	@PreDestroy
	public void unregisterFromJmx() {
		setEnableJmx(false);
	}
}
