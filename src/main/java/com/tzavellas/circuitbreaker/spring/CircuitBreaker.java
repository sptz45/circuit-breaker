package com.tzavellas.circuitbreaker.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import com.tzavellas.circuitbreaker.support.ProxyBasedCircuitBreakerAspectSupport;

/**
 * Implements the Circuit Breaker stability design pattern using Spring AOP.
 * 
 * <p>This implementation uses an around advice and can be configured using
 * dependency injection.</p>
 * 
 * @see ProxyBasedCircuitBreakerAspectSupport
 * @author spiros
 */
public class CircuitBreaker extends ProxyBasedCircuitBreakerAspectSupport {

	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		return doExecute(pjp);
	}
	
	@Override
	protected Object getTarget(Object invocation) {
		return ((ProceedingJoinPoint) invocation).getTarget();
	}
	
	@Override
	protected Object proceed(Object invocation) throws Throwable {
		return ((ProceedingJoinPoint) invocation).proceed();
	}
}
