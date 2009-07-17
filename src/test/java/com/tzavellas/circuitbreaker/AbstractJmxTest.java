package com.tzavellas.circuitbreaker;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.ObjectName;

import com.tzavellas.test.ITimeService;


public abstract class AbstractJmxTest {
	
	protected ITimeService time;
	
	public abstract void disableJmx();
	
	protected void testOpenCircuitViaJmx() throws Exception {
		time.networkTime();
		
		CircuitInfoMBean mbean = JMX.newMBeanProxy(
				ManagementFactory.getPlatformMBeanServer(),
				new ObjectName("com.tzavellas.circuitbreaker:type=CircuitInfo,target=TimeService"),
				CircuitInfoMBean.class);
		
		assertEquals(1, mbean.getCalls());
		mbean.open();
		time.networkTime();
	}
}
