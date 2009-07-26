package com.tzavellas.circuitbreaker.spring;

import javax.management.JMException;

import org.junit.After;
import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractJmxTest;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.circuitbreaker.jmx.CircuitBreakerMBean;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;
import com.tzavellas.test.ITimeService;
import com.tzavellas.test.spring.TimeService;

public class JmxTest extends AbstractJmxTest {

	final CircuitBreaker timeBreaker;
	
	public JmxTest() throws JMException {
		time = (ITimeService) SpringLoader.CONTEXT.getBean("timeService");
		timeBreaker = (CircuitBreaker) SpringLoader.CONTEXT.getBean("timeBreaker");
	}
	
	@Override
	protected CircuitBreakerMBean mbean() throws JMException {
		return JmxUtils.circuitBreakersForType(TimeService.class).iterator().next();
	}
	
	@After
	public void disableJmx() {
		timeBreaker.setEnableJmx(false);
	}

	@Test(expected=OpenCircuitException.class)
	public void enable_jmx_via_aspect_setter() throws Exception {
		timeBreaker.setEnableJmx(true);
		readStatsAndOpenCircuitViaJmx();
	}
}
