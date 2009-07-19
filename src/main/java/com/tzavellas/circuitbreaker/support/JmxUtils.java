package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.CircuitInfoMBean;

public abstract class JmxUtils {
	
	private JmxUtils() { }
	
	public static CircuitInfoMBean getCircuitInfo(String targetName) throws JMException {
		return JMX.newMBeanProxy(
			ManagementFactory.getPlatformMBeanServer(),
			new ObjectName("com.tzavellas.circuitbreaker:type=CircuitInfo,target=" + targetName),
			CircuitInfoMBean.class);
	}

}
