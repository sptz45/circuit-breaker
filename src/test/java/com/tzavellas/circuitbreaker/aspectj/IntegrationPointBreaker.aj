package com.tzavellas.circuitbreaker.aspectj;

import com.tzavellas.test.IntegrationPoint;
import com.tzavellas.circuitbreaker.aspectj.CircuitBreaker;;

public aspect IntegrationPointBreaker extends CircuitBreaker {
	
	public pointcut circuit(): within(@IntegrationPoint com.tzavellas.test.aj.*);
}
