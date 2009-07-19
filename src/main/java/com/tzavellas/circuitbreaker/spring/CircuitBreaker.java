package com.tzavellas.circuitbreaker.spring;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;

import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.circuitbreaker.util.Duration;

/**
 * Implements the CircuitInfo Breaker stability design pattern using spring-aop.
 * 
 * @author spiros
 */
public class CircuitBreaker extends CircuitBreakerAspectSupport implements CircuitConfiguration {

	private volatile boolean needsInitialization = true;

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
	
	public void setIgnoredExceptions(Set<Class<? extends Throwable>> ignored) {
		ignoredExceptions.clear();
		ignoredExceptions.addAll(ignored);
	}
}
