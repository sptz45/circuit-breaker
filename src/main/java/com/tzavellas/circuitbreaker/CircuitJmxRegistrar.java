package com.tzavellas.circuitbreaker;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * A class that knows how to register and unregister {@code Circuit}
 * objects in JMX.
 * 
 * <p> The {@code ObjectName} used for each {@code CircuitMBean} is:
 * {@literal com.tzavellas.circuitbreaker:type=Circuit,target=targetName},
 * where targetName is the simple name of the class of the circuit (defined
 * by the circuit() pointcut). 
 * 
 * @see CircuitBreaker
 * @see Circuit
 * 
 * @author spiros
 */
class CircuitJmxRegistrar {
	
	private Circuit circuit;
	private String targetName;
	private ObjectName name;
	
	
	CircuitJmxRegistrar(Circuit c, String targetName) {
		circuit = c;
		this.targetName = targetName;
	}
	
	void register() {
		try {
			name = new ObjectName("com.tzavellas.circuitbreaker:type=Circuit,target=" + targetName);
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(circuit, name);
		} catch (JMException e) {
			throw new RuntimeException(e);
		}
	}
	
	void unregister() {
		if (isRegistered()) {
			try {
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				server.unregisterMBean(name);
			} catch (JMException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	boolean isRegistered() {
		return name != null;
	}
}
