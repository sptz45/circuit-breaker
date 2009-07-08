package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
