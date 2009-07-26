package com.tzavellas.circuitbreaker.aopalliance;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.circuitbreaker.jmx.CircuitBreakerMBean;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;
import com.tzavellas.circuitbreaker.spring.SpringLoader;
import com.tzavellas.test.IStockService;
import com.tzavellas.test.spring.StockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {

	public CircuitBreakerTest() {
		stocks = (IStockService) SpringLoader.CONTEXT.getBean("stockServiceAlliance");
		stocksBreaker = (CircuitBreaker) SpringLoader.CONTEXT.getBean("stocksBreakerAlliance");
	}
	
	
	@Test
	public void test_jmx() throws Exception {
		stocksBreaker.setEnableJmx(true);
		stocks.getQuote("JAVA");
		
		CircuitBreakerMBean mbean = JmxUtils.circuitBreakersForType(StockService.class).iterator().next();
		assertTrue(mbean.getCalls() >= 1);
		
		mbean.open();
		assertTrue(stocksBreaker.getCircuitInfo().isOpen());
		
		stocksBreaker.setEnableJmx(false);
	}
}
