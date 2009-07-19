package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.CircuitInfoMBean;

/**
 * Various helper methods for using JMX with CircuitInfo objects.
 * 
 * @author spiros
 */
public abstract class JmxUtils {
	
	private JmxUtils() { }
	
	public static CircuitInfoMBean getCircuitInfo(Class<?> targetClass) throws JMException {
		return JMX.newMBeanProxy(
			ManagementFactory.getPlatformMBeanServer(),
			new ObjectName(getObjectName(targetClass)),
			CircuitInfoMBean.class);
	}

	
	public static String getObjectName(Class<?> targetClass) {
		return "com.tzavellas.circuitbreaker:type=CircuitInfo,target=" + targetClass.getSimpleName();
	} 
}
