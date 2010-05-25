package com.tzavellas.circuitbreaker.support;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.tzavellas.circuitbreaker.jmx.CircuitBreaker;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;

/**
 * A class that knows how to register and unregister {@code CircuitBreaker}
 * objects in JMX.
 * 
 * @see CircuitBreakerAspectSupport
 * @see JmxUtils
 * @author spiros
 */
class CircuitJmxRegistrar {
	
	private final ObjectName name;
	private final CircuitBreaker jmxBreaker;
	
	private volatile boolean registered = false;	
	
	CircuitJmxRegistrar(CircuitBreakerAspectSupport breaker, Object target) {
		jmxBreaker = new CircuitBreaker(breaker);
		try {
			name = new ObjectName(JmxUtils.getObjectName(target));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}
	
	void register() {
		if (isRegistered())
			return;
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(jmxBreaker, name);
			registered = true;
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
			registered = false;
		}
	}
	
	boolean isRegistered() {
		return registered;
	}
}
