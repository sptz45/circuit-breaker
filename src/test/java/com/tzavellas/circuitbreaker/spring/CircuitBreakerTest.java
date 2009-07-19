package com.tzavellas.circuitbreaker.spring;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.test.IStockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {
	
	public CircuitBreakerTest() {
		stocks = (IStockService) SpringLoader.CONTEXT.getBean("stockService");
		stocksBreaker = (CircuitBreaker) SpringLoader.CONTEXT.getBean("stocksBreaker");
	}
}
