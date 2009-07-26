package com.tzavellas.circuitbreaker.support;

import java.util.Set;

import javax.annotation.PreDestroy;

import com.tzavellas.circuitbreaker.util.Duration;

/**
 * An abstract class that implements the basic template for a Circuit Breaker implemented
 * using proxy based AOP.
 * 
 * @author spiros
 */
public abstract class ProxyBasedCircuitBreakerAspectSupport extends CircuitBreakerAspectSupport implements CircuitConfiguration {

	// We need the target object to perform some initialization (JMX registration etc)
	// so we initialize lazily on the first call of the around advice.
	private volatile boolean needsInitialization = true;
	
	protected abstract Object proceed(Object invocation) throws Throwable;
	
	protected abstract Object getTarget(Object invocation);

	/** The method of the around advice. */
	public Object doExecute(Object invocation) throws Throwable {
		if (needsInitialization) {
			synchronized (this) {
				if (needsInitialization) {
					onTargetInitialization(getTarget(invocation));
					needsInitialization = false;
				}
			}
		}
		
		beforeMethodExecution();
		
		try {
			Object result = proceed(invocation);
			afterSucessfulMethodExecution();
			return result;
			
		} catch (Throwable e) {
			afterFailure(e);
			throw e;
		}
	}
	
	/** {@inheritDoc} */
	public void setMaxFailures(int maxFailures) {
		getCircuitInfo().setMaxFailures(maxFailures);
	}
	/** {@inheritDoc} */
	public void setTimeout(Duration timeout) {
		getCircuitInfo().setTimeout(timeout);
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
