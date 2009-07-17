package com.tzavellas.circuitbreaker.aspectj;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractCircuitConfigurationTest;
import com.tzavellas.test.aj.TimeService;

public class CircuitConfigurationTest extends AbstractCircuitConfigurationTest {
		
	public CircuitConfigurationTest() {
		time = new TimeService();
		CircuitConfiguratorBean c = new CircuitConfiguratorBean();
		c.setAspectClass(IntegrationPointBreaker.class);
		c.setCircuit(time);
		configurator = c;
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void fail_fast_with_wrong_aspect_class_via_reflection() throws Throwable {
		Method classSetter = configurator.getClass().getMethod("setAspectClass", Class.class);
		try {
			classSetter.invoke(configurator, String.class);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void fail_fast_with_object_that_has_no_aspect_bound() {
		((CircuitConfiguratorBean)configurator).setCircuit("I don't have a CircuitBreaker aspect bound on me");
	}
}
