package com.tzavellas.circuitbreaker.spring;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.support.AbstractCircuitConfiguration;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.circuitbreaker.util.Duration;

/**
 * 
 * <p>If your pointcut matches more than one bean then this aspect must be
 * declared with <b>scope="prototype"</b>.</p>
 * 
 * @author spiros
 *
 */
public class CircuitBreaker extends CircuitBreakerAspectSupport implements CircuitConfiguration {

	private volatile boolean needsInitialization = true;
	private SimpleCircuitConfiguration config;
	
	public CircuitBreaker() {
		config = new SimpleCircuitConfiguration(circuit);
	}
	
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
	
	@PostConstruct
	public void configure() throws Exception {
		config.configure();
	}
	public void setCurrentFailuresDuration(Duration d) {
		config.setCurrentFailuresDuration(d);
	}
	public void setMaxFailures(int maxFailures) {
		config.setMaxFailures(maxFailures);
	}
	public void setTimeoutMillis(long timeoutMillis) {
		config.setTimeoutMillis(timeoutMillis);
	}
}

class SimpleCircuitConfiguration extends AbstractCircuitConfiguration {	
	private CircuitInfo circuitInfo;
	SimpleCircuitConfiguration(CircuitInfo ci) { circuitInfo = ci; }
	public CircuitInfo getCircuitInfo() { return circuitInfo; }
}
