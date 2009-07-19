package com.tzavellas.circuitbreaker.aspectj;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tzavellas.circuitbreaker.AbstractJmxTest;
import com.tzavellas.circuitbreaker.CircuitInfoMBean;
import com.tzavellas.test.ITimeService;

public class ConfigFromSpringTest {
	static AbstractApplicationContext context = new ClassPathXmlApplicationContext("/aspectj-impl-context.xml");
	
	ITimeService time = (ITimeService) context.getBean("timeService");
	
	@After
	public void unregisterFromJmx() {
		CircuitBreakerConfigurator.aspectOf().setEnableJmx(false);
		breaker().setEnableJmx(false);
	}
	
	@Test
	public void open_and_close_via_jmx() throws Exception {
		generateFaults(1);
		assertTrue(breaker().getCircuitInfo().isOpen());
		
		CircuitInfoMBean mbean = AbstractJmxTest.getCircuitInfoMBean("TimeService");
		mbean.close();
		assertFalse(breaker().getCircuitInfo().isOpen());
	}
	
	private IntegrationPointBreaker breaker() {
		return IntegrationPointBreaker.aspectOf(time);
	}
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
