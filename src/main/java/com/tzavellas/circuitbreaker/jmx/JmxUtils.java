package com.tzavellas.circuitbreaker.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Various helper methods for using JMX with CircuitInfo objects.
 * 
 * @author spiros
 */
public abstract class JmxUtils {
	
	private JmxUtils() { }
	
	private static CircuitBreakerMBean getCircuitBreaker(ObjectName name) throws JMException {
		return JMX.newMBeanProxy(
			ManagementFactory.getPlatformMBeanServer(),
			name,
			CircuitBreakerMBean.class);
	}
	
	public static CircuitBreakerMBean getCircuitBreaker(Object target) throws JMException {
		return getCircuitBreaker(new ObjectName(getObjectName(target)));
	}
	
	public static Set<CircuitBreakerMBean> circuitBreakersForType(Class<?> targetClass) throws JMException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String query = String.format("com.tzavellas.circuitbreaker:type=CircuitInfo,target=%s,code=*", targetClass.getSimpleName());
		Set<ObjectName> names = server.queryNames(new ObjectName(query), null);
		Set<CircuitBreakerMBean> mbeans = new HashSet<CircuitBreakerMBean>();
		for (ObjectName name: names)
			mbeans.add(getCircuitBreaker(name));
		return mbeans;
	}

	public static String getObjectName(Object target) {
		return String.format("com.tzavellas.circuitbreaker:type=CircuitInfo,target=%s,code=%d",
				target.getClass().getSimpleName(),
				target.hashCode());
	} 
}
