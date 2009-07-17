package com.tzavellas.circuitbreaker.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.test.IStockService;

public abstract class CircuitBreakerTest extends AbstractCircuitBreakerTest {
	
	static ApplicationContext ctx = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
	
	public CircuitBreakerTest() {
		stocks = (IStockService) ctx.getBean("stockService");
	}
	
	@Override
	protected CircuitBreakerAspectSupport getCircuitBreaker() {
		return (CircuitBreaker) ctx.getBean("stocksBreaker");
	}
}
