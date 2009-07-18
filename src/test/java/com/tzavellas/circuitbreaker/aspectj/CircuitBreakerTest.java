package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.test.aj.StockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {
	
	public CircuitBreakerTest() {
		stocks = new StockService();
		stocksBreaker = StockBreaker.aspectOf(stocks); 
	}
}
