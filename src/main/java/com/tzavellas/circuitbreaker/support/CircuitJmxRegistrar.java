package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.CircuitInfo;

/**
 * A class that knows how to register and unregister {@code CircuitInfo}
 * objects in JMX.
 * 
 * @see CircuitBreakerAspectSupport
 * @see CircuitInfo
 * @see JmxUtils
 * 
 * @author spiros
 */
class CircuitJmxRegistrar {
	
	private CircuitInfo circuit;
	private Object target;
	private ObjectName name;
	
	
	CircuitJmxRegistrar(CircuitInfo circuit, Object target) {
		this.circuit = circuit;
		this.target = target;
	}
	
	void register() {
		if (isRegistered())
			return;
		try {
			name = new ObjectName(JmxUtils.getObjectName(target));
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
