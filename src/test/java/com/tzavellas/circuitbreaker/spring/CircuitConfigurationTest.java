package com.tzavellas.circuitbreaker.spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.test.ITimeService;

public class CircuitConfigurationTest  {
	
	final ITimeService time;
	final CircuitBreaker timeBreaker;
	
	public CircuitConfigurationTest() {
		time = (ITimeService) SpringLoader.CONTEXT.getBean("timeService");
		timeBreaker = (CircuitBreaker) SpringLoader.CONTEXT.getBean("timeBreaker");
	}
	
	@Before
	public void closeCircuit() {
		timeBreaker.getCircuitInfo().close();
	}
	
	@Test(expected=OpenCircuitException.class)
	public void increment_maxFailures_and_test() throws Exception {
		assertEquals(ITimeService.EXPECTED, time.networkTime());
		generateFaults(CircuitInfo.DEFAULT_MAX_FAILURES);
		assertEquals(ITimeService.EXPECTED, time.networkTime());
		generateFaults(1);
		assertTrue(timeBreaker.getCircuitInfo().isOpen());
		time.networkTime();
	}
	
	@Test
	public void test_configured_timeout() throws Exception {
		assertEquals(ITimeService.EXPECTED, time.networkTime());
		generateFaults(6);
		assertTrue(timeBreaker.getCircuitInfo().isOpen());
		Thread.sleep(5);
		assertEquals(ITimeService.EXPECTED, time.networkTime());
	}
	
	@Test
	public void test_configured_duration() throws Exception {
		generateFaults(5);
		assertTrue(timeBreaker.getCircuitInfo().isClosed());
		Thread.sleep(5);
		generateFaults(1);
		assertTrue(timeBreaker.getCircuitInfo().isClosed());
		assertEquals(ITimeService.EXPECTED, time.networkTime());
	}
	
	@Test 
	public void ignored_exceptions_prevent_the_circuit_from_opening() {
		ITimeService iTime = (ITimeService) SpringLoader.CONTEXT.getBean("ignoredTimeService");
		for (int i = 0; i < CircuitInfo.DEFAULT_MAX_FAILURES; i++) {
			try { iTime.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
		assertEquals(ITimeService.EXPECTED, iTime.networkTime());
	}
	
	private void generateFaults(int failures) {
		for (int i = 0; i < failures; i++) {
			try { time.faultyNetworkTime(); } catch (IllegalStateException expected) { }
		}
	}
}
