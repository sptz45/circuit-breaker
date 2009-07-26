package com.tzavellas.circuitbreaker.aspectj;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.circuitbreaker.util.Duration;
import com.tzavellas.test.ITimeService;
import com.tzavellas.test.aj.TimeService;

public class CircuitConfigurationTest {
	
	final int newMaxFailures = CircuitInfo.DEFAULT_MAX_FAILURES + 1;
	final ITimeService time = new TimeService();
	final CircuitConfiguratorBean configurator = new CircuitConfiguratorBean();
		
	public CircuitConfigurationTest() {
		configurator.setAspectClass(IntegrationPointBreaker.class);
		configurator.setTarget(time);
	}

	
	@Test
	public void test_with_defaults() {
		assertEquals(ITimeService.EXPECTED, time.networkTime());
	}
	
	@Test
	public void change_maxFailures_and_test() throws Exception {
		configurator.setMaxFailures(newMaxFailures);
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		assertEquals(ITimeService.EXPECTED, time.networkTime());
		generateFaults(1);
		try {
			time.networkTime();
			fail("The circuit must be open!");
		} catch(OpenCircuitException expected) { }
	}

	@Test
	public void change_timeout_and_test() throws Exception {
		configurator.setTimeout(Duration.millis(1));
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		Thread.sleep(5);
		assertEquals(ITimeService.EXPECTED, time.networkTime());
	}
	
	@Test
	public void change_failure_duration_and_test() throws Exception {
		configurator.setCurrentFailuresDuration(Duration.nanos(1));
		configurator.configure();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		assertEquals(ITimeService.EXPECTED, time.networkTime());
	}
	
	@Test(expected=OpenCircuitException.class)
	public void enable_slow_metnod_execution_tracking() throws Exception {
		configurator.setMaxMethodDuration(Duration.nanos(1));
		configurator.configure();
		for (int i = 0; i < CircuitInfo.DEFAULT_MAX_FAILURES; i++)
			time.networkTime();
		time.networkTime();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void add_ignored_exception_and_test() {
		Set<Class<? extends Throwable>> ignored = new HashSet<Class<? extends Throwable>>();
		ignored.add(IllegalStateException.class);
		CircuitBreakerConfigurator.aspectOf().setIgnoredExceptions(ignored);
		ITimeService ts = new TimeService();
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		ts.networkTime();
		CircuitBreakerConfigurator.aspectOf().setIgnoredExceptions(Collections.EMPTY_SET);
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
		((CircuitConfiguratorBean)configurator).setTarget("I don't have a CircuitBreaker aspect bound on me");
	}
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
