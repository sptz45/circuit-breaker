package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.CircuitInfoMBean;

/**
 * Various helper methods for using JMX with CircuitInfo objects.
 * 
 * @author spiros
 */
public abstract class JmxUtils {
	
	private JmxUtils() { }
	
	public static CircuitInfoMBean getCircuitInfo(Object target) throws JMException {
		return getCircuitInfo(new ObjectName(getObjectName(target)));
			
	}
	
	private static CircuitInfoMBean getCircuitInfo(ObjectName name) throws JMException {
		return JMX.newMBeanProxy(
			ManagementFactory.getPlatformMBeanServer(),
			name,
			CircuitInfoMBean.class);
	}
	
	public static Set<CircuitInfoMBean> circuitInfoForType(Class<?> targetClass) throws JMException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String query = String.format("com.tzavellas.circuitbreaker:type=CircuitInfo,target=%s,code=*", targetClass.getSimpleName());
		Set<ObjectName> names = server.queryNames(new ObjectName(query), null);
		Set<CircuitInfoMBean> mbeans = new HashSet<CircuitInfoMBean>();
		for (ObjectName name: names)
			mbeans.add(getCircuitInfo(name));
		return mbeans;
	}


	/**
	 * Get the ObjectName (as a String) used to register the ...
	 * 
	 * The format of the {@code ObjectName} used for each {@code CircuitInfoMBean} is:
	 * {@literal com.tzavellas.circuitbreaker:type=CircuitInfo,target=targetName},
	 * where targetName is the simple name of the class of the target (defined
	 * by the circuit() pointcut). 
	 * 
	 */
	public static String getObjectName(Object target) {
		return String.format("com.tzavellas.circuitbreaker:type=CircuitInfo,target=%s,code=%d",
				target.getClass().getSimpleName(),
				target.hashCode());
	} 
}
