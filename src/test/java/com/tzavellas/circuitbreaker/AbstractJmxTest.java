package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import com.tzavellas.test.ITimeService;

public abstract class AbstractJmxTest {
	
	protected ITimeService time;
	protected CircuitInfoMBean mbean; 
	
	public abstract void disableJmx();
	
	protected void readStatsAndOpenCircuitViaJmx() throws Exception {
		time.networkTime();
		assertTrue(mbean.getCalls() >= 1);
		
		mbean.open();
		time.networkTime();
	}
}
