package com.tzavellas.circuitbreaker.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.tzavellas.circuitbreaker.support.ProxyBasedCircuitBreakerAspectSupport;

/**
 * Implements the Circuit Breaker stability design pattern using a dynamic proxy.
 * 
 * @see ProxyBasedCircuitBreakerAspectSupport
 * 
 * @author Patrik Nordwall
 * @author spiros
 */
public class CircuitBreaker extends ProxyBasedCircuitBreakerAspectSupport {
	
	private CircuitBreaker() { }

    @SuppressWarnings("unchecked")
    public static <T> T introduceCircuitBreaker(T delegate) {
        Class<?>[] interfaces = delegate.getClass().getInterfaces();
        ClassLoader loader = delegate.getClass().getClassLoader();
        CircuitBreaker breaker = new CircuitBreaker(); 
        InvocationHandler handler = breaker.new CircuitHandler(delegate);
        T proxy = (T) Proxy.newProxyInstance(loader, interfaces, handler);
        return proxy;
    }
    
    public static CircuitBreaker getCircuitBreakerFrom(Object delegate) {
    	try {
    		InvocationHandler handler =  Proxy.getInvocationHandler(delegate);
    		return ((CircuitHandler) handler).getCircuitBreaker();
    	} catch (RuntimeException e) {
    		throw new RuntimeException("The specified object ["+delegate+"] does not have a circuit breaker", e);
    	}
    }

    @Override
    protected Object getTarget(Object invocation) {
        InvocationParameters params = (InvocationParameters) invocation;
        return params.delegate;
    }

    @Override
    protected Object proceed(Object invocation) throws Throwable {
        try {
            InvocationParameters params = (InvocationParameters) invocation;
            Object result = params.method.invoke(params.delegate, params.args);
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    protected boolean shouldBypassBreaker(InvocationParameters invocation) {
        if (invocation.method.getName().equals("toString") && invocation.args.length == 0) {
            return true;
        }
        return false;
    }

    private class CircuitHandler implements InvocationHandler {
        private final Object delegate;

        CircuitHandler(Object delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            InvocationParameters invocation = new InvocationParameters(delegate, method, args);
            if (shouldBypassBreaker(invocation)) {
                return proceed(invocation);
            }
            Object result = doExecute(invocation);
            return result;
        }
        
        CircuitBreaker getCircuitBreaker() {
        	return CircuitBreaker.this;
        }
    }

    private static class InvocationParameters {
        final Object delegate;
        final Method method;
        final Object[] args;

        InvocationParameters(Object delegate, Method method, Object[] args) {
            this.delegate = delegate;
            this.method = method;
            this.args = args;
        }

    }
}
