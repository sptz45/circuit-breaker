package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import javax.management.JMException;

import com.tzavellas.circuitbreaker.jmx.CircuitBreakerMBean;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;
import com.tzavellas.test.ITimeService;

public abstract class AbstractJmxTest {
	
	protected ITimeService time;
	
	protected abstract void disableJmx();
	
	protected CircuitBreakerMBean mbean() throws JMException {
		return JmxUtils.getCircuitBreaker(time);
	}
	
	protected void readStatsAndOpenCircuitViaJmx() throws Exception {
		time.networkTime();
		
		CircuitBreakerMBean mbean = mbean();
		assertTrue(mbean.getCalls() >= 1);
		
		mbean.open();
		time.networkTime();
	}
}
