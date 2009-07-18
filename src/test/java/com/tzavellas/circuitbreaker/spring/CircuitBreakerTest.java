package com.tzavellas.circuitbreaker.spring;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.test.IStockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {
	
	public CircuitBreakerTest() {
		stocks = (IStockService) SpringContextLoader.context.getBean("stockService");
		stocksBreaker = (CircuitBreaker) SpringContextLoader.context.getBean("stocksBreaker");
	}
}
