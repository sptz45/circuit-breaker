package com.tzavellas.circuitbreaker.aspectj;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tzavellas.circuitbreaker.CircuitInfoMBean;
import com.tzavellas.circuitbreaker.support.JmxUtils;
import com.tzavellas.test.ITimeService;
import com.tzavellas.test.aj.TimeService;

public class ConfigFromSpringTest {
	
	final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/aspectj-impl-context.xml");
	final ITimeService time = (ITimeService) context.getBean("timeService");
	
	@After
	public void unregisterFromJmx() {
		CircuitBreakerConfigurator.aspectOf().setEnableJmx(false);
		breaker().setEnableJmx(false);
	}
	
	@Test
	public void open_and_close_via_jmx() throws Exception {
		generateFaults(1);
		assertTrue(breaker().getCircuitInfo().isOpen());
		
		CircuitInfoMBean mbean = JmxUtils.getCircuitInfo(TimeService.class);
		mbean.close();
		assertFalse(breaker().getCircuitInfo().isOpen());
	}
	
	
	private CircuitBreaker breaker() {
		return IntegrationPointBreaker.aspectOf(time);
	}
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
