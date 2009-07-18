package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.test.aj.StockService;

public class CricuitBreakerTest extends AbstractCircuitBreakerTest {
	
	public CricuitBreakerTest() {
		stocks = new StockService();
		stocksBreaker = StockBreaker.aspectOf(stocks); 
	}
}
