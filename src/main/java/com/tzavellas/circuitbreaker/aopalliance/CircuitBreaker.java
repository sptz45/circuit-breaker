package com.tzavellas.circuitbreaker.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.tzavellas.circuitbreaker.support.ProxyBasedCircuitBreakerAspectSupport;

/**
 * Implements the Circuit Breaker stability design pattern using AOP Alliance APIs.
 * 
 * @author spiros
 */
public class CircuitBreaker extends ProxyBasedCircuitBreakerAspectSupport implements MethodInterceptor {
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return doExecute(invocation);
	}
	
	@Override
	protected Object getTarget(Object invocation) {
		return ((MethodInvocation) invocation).getThis();
	}
	
	@Override
	protected Object proceed(Object invocation) throws Throwable {
		return ((MethodInvocation) invocation).proceed();
	}
}
