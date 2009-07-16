package com.tzavellas.circuitbreaker.spring;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 
 * <p>If your pointcut matches more than one bean then this aspect must be
 * declared with <b>scope="prototype"</b>.</p>
 * 
 * @author spiros
 *
 */
public class CircuitBreaker extends ConfigurableCircuitBreakerAspectSupport {

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
}
