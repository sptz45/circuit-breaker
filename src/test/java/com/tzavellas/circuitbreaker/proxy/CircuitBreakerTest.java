package com.tzavellas.circuitbreaker.proxy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractCircuitBreakerTest;
import com.tzavellas.circuitbreaker.jmx.CircuitBreakerMBean;
import com.tzavellas.circuitbreaker.jmx.JmxUtils;
import com.tzavellas.test.IStockService;
import com.tzavellas.test.spring.StockService;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {

    public CircuitBreakerTest() {
        IStockService delegate = new StockService();
        stocks = CircuitBreaker.introduceCircuitBreaker(delegate);
        stocksBreaker = CircuitBreaker.getCircuitBreakerFrom(stocks);
    }

    @Test
    public void enable_and_use_jmx() throws Exception {
        stocksBreaker.setEnableJmx(true);
        stocks.getQuote("JAVA");

        CircuitBreakerMBean mbean = JmxUtils.circuitBreakersForType(StockService.class).iterator().next();
        assertTrue(mbean.getCalls() >= 1);

        mbean.open();
        assertTrue(stocksBreaker.getCircuitInfo().isOpen());

        stocksBreaker.setEnableJmx(false);
    }
    
    @Test(expected=RuntimeException.class)
    public void exception_when_specified_object_does_not_have_a_circuit_breaker() {
    	CircuitBreaker.getCircuitBreakerFrom("hello");
    }
}
