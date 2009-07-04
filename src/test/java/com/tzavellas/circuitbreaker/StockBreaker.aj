package com.tzavellas.circuitbreaker;

import com.tzavellas.circuitbreaker.CircuitBreaker;

public aspect StockBreaker extends CircuitBreaker {
	
	pointcut circuit(): within(com.tzavellas.test.StockService);
}
