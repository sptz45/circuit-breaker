package com.tzavellas.circuitbreaker.spring;

import org.aspectj.lang.ProceedingJoinPoint;

import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.util.Duration;

/**
 * 
 * <p>If your pointcut matches more than one bean then this aspect must be
 * declared with <b>scope="prototype"</b>.</p>
 * 
 * @author spiros
 *
 */
public class CircuitBreaker extends CircuitBreakerAspectSupport {

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
	
	public void setMaxFailures(int maxFailures) {
		getCircuitInfo().setMaxFailures(maxFailures);
	}
	public void setTimeoutMillis(long timeoutMillis) {
		getCircuitInfo().setTimeoutMillis(timeoutMillis);
	}
	public void setCurrentFailuresDuration(Duration d) {
		getCircuitInfo().setCurrentFailuresDuration(d);
	}
}
