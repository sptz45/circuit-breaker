package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.test.aj.StockService;

public class CricuitBreakerTest extends AbstractCircuitBreakerTest {
	
	public CricuitBreakerTest() {
		stocks = new StockService();
	}
	
	@Override
	protected CircuitBreakerAspectSupport getCircuitBreaker() {
		return StockBreaker.aspectOf(stocks);
	}
}
