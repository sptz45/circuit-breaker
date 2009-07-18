package com.tzavellas.circuitbreaker.spring;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.test.IStockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {
	
	private static AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
	
	@BeforeClass
	public static void loadContext() {
		ctx = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
	}
	
	@AfterClass
	public static void destroyContext() {
		ctx.destroy();
		ctx = null;
	}
	
	public CircuitBreakerTest() {
		stocks = (IStockService) ctx.getBean("stockService");
		stocksBreaker = (CircuitBreaker) ctx.getBean("stocksBreaker");
	}
}
