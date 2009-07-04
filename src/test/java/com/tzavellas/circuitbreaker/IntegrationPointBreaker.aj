package com.tzavellas.circuitbreaker;

import com.tzavellas.test.IntegrationPoint;

public aspect IntegrationPointBreaker extends CircuitBreaker {
	
	pointcut circuit(): within(@IntegrationPoint com.tzavellas.test.*);
}
