package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import com.tzavellas.circuitbreaker.support.JmxUtils;
import com.tzavellas.test.ITimeService;

public abstract class AbstractJmxTest {
	
	protected ITimeService time;
	
	public abstract void disableJmx();
	
	protected void testOpenCircuitViaJmx() throws Exception {
		time.networkTime();
		
		CircuitInfoMBean mbean = JmxUtils.getCircuitInfo("TimeService");
		
		assertTrue(mbean.getCalls() >= 1);
		mbean.open();
		time.networkTime();
	}
}
