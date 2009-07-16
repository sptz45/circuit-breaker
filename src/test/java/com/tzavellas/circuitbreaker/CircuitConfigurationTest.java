package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.tzavellas.circuitbreaker.util.Duration;
import com.tzavellas.test.TimeService;

public class CircuitConfigurationTest {
	
	final TimeService time = new TimeService();
	final int newMaxFailures = CircuitInfo.DEFAULT_MAX_FAILURES + 1;
	final CircuitConfiguratorBean configurator = new CircuitConfiguratorBean();
	
	@Before
	public void setupConfigurator() {
		configurator.setAspectClass(IntegrationPointBreaker.class);
		configurator.setCircuit(time);
	}
	
	@Test
	public void test_with_defaults() {
		assertEquals(TimeService.EXPECTED, time.networkTime());
	}
	
	@Test
	public void change_maxFailures_and_test() throws Exception {
		configurator.setMaxFailures(newMaxFailures);
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		assertEquals(TimeService.EXPECTED, time.networkTime());
		generateFaults(1);
		try {
			time.networkTime();
			fail("The circuit must be open!");
		} catch(OpenCircuitException expected) { }
	}

	@Test
	public void change_timeout_and_test() throws Exception {
		configurator.setTimeoutMillis(1);
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		Thread.sleep(5);
		assertEquals(TimeService.EXPECTED, time.networkTime());
	}
	
	@Test
	public void change_failure_duration_and_test() throws Exception {
		configurator.setCurrentFailuresDuration(Duration.nanos(1));
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		assertEquals(TimeService.EXPECTED, time.networkTime());
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
		configurator.setCircuit("I don't have a CircuitBreaker aspect bound on me");
	}
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
