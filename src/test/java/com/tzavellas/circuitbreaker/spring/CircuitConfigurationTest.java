package com.tzavellas.circuitbreaker.spring;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tzavellas.circuitbreaker.AbstractCircuitConfigurationTest;
import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.test.ITimeService;

public class CircuitConfigurationTest  extends AbstractCircuitConfigurationTest {
	
	private static AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
	
	@BeforeClass
	public static void loadContext() {
		ctx = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
	}
	
	@AfterClass
	public static void destroyContext() {
		ctx.destroy();
		ctx = null;
	}
	
	public CircuitConfigurationTest() {
		time = (ITimeService) ctx.getBean("timeService");
		configurator = (CircuitConfiguration) ctx.getBean("timeBreaker");
	}
}
