package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.jmx.CircuitBreaker;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;

/**
 * A class that knows how to register and unregister {@code CircuitBreaker}
 * objects in JMX.
 * 
 * @see CircuitBreakerAspectSupport
 * @see JmxUtils
 * 
 * @author spiros
 */
class CircuitJmxRegistrar {
	
	private final CircuitBreaker jmxBreaker;
	private final Object target;
	
	private ObjectName name;
	
	CircuitJmxRegistrar(CircuitBreakerAspectSupport breaker, Object target) {
		jmxBreaker = new CircuitBreaker(breaker);
		this.target = target;
	}
	
	void register() {
		if (isRegistered())
			return;
		try {
			name = new ObjectName(JmxUtils.getObjectName(target));
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(jmxBreaker, name);
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
