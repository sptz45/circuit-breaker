package com.tzavellas.circuitbreaker.spring;

import org.springframework.context.ApplicationContext;

import com.tzavellas.circuitbreaker.AbstractCircuitConfigurationTest;
import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.test.ITimeService;

public abstract class CircuitConfigurationTest  extends AbstractCircuitConfigurationTest {
	
	private ApplicationContext ctx = CircuitBreakerTest.ctx;
	
	public CircuitConfigurationTest() {
		time = (ITimeService) ctx.getBean("timeService");
		configurator = (CircuitConfiguration) ctx.getBean("timeBreaker");
	}
}
