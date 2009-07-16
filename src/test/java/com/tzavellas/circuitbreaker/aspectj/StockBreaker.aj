package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.circuitbreaker.aspectj.CircuitBreaker;

public aspect StockBreaker extends CircuitBreaker {
	
	public pointcut circuit(): within(com.tzavellas.test.StockService);
}
