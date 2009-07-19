package com.tzavellas.circuitbreaker.spring;

import org.junit.After;
import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractJmxTest;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.test.ITimeService;

public class JmxTest extends AbstractJmxTest {

	final CircuitBreaker timeBreaker;
	
	public JmxTest() {
		time = (ITimeService) SpringLoader.CONTEXT.getBean("timeService");
		timeBreaker = (CircuitBreaker) SpringLoader.CONTEXT.getBean("timeBreaker");
	}
	
	@After
	public void disableJmx() {
		timeBreaker.setEnableJmx(false);
	}

	@Test(expected=OpenCircuitException.class)
	public void enable_jmx_via_aspect_setter() throws Exception {
		timeBreaker.setEnableJmx(true);
		testOpenCircuitViaJmx();
	}
}
