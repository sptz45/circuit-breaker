package com.tzavellas.circuitbreaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.tzavellas.circuitbreaker.support.CircuitConfiguration;
import com.tzavellas.circuitbreaker.util.Duration;
import com.tzavellas.test.ITimeService;

public abstract class AbstractCircuitConfigurationTest {
	
	protected final int newMaxFailures = CircuitInfo.DEFAULT_MAX_FAILURES + 1;
	
	protected ITimeService time;
	protected CircuitConfiguration configurator;
	

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
		configurator.setTimeoutMillis(1);
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
	
	protected void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
