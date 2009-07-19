package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;

import javax.management.JMException;

import com.tzavellas.circuitbreaker.support.JmxUtils;
import com.tzavellas.test.ITimeService;

public abstract class AbstractJmxTest {
	
	protected ITimeService time;
	
	protected abstract void disableJmx();
	
	protected CircuitInfoMBean mbean() throws JMException {
		return JmxUtils.getCircuitInfo(time);
	}
	
	protected void readStatsAndOpenCircuitViaJmx() throws Exception {
		time.networkTime();
		
		CircuitInfoMBean mbean = mbean();
		assertTrue(mbean.getCalls() >= 1);
		
		mbean.open();
		time.networkTime();
	}
}
