package com.tzavellas.circuitbreaker;

import static org.junit.Assert.*;
import org.junit.Test;

import com.tzavellas.circuitbreaker.Circuit;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.test.StockService;

public class CricuitBreakerTest {
	
	StockService stocks = new StockService();
	
	@Test
	public void normal_operation_during_closed() {
		assertEquals(5, stocks.getQuote("JAVA"));
	}
	
	@Test(expected=OpenCircuitException.class)
	public void after_a_number_of_faults_the_circuit_opens() {
		generateFaultsToOpen();
		stocks.getQuote("JAVA");
	}
	
	@Test
	public void the_circuit_can_be_opened_after_being_closed() {
		generateFaultsToOpen();
		try {
			stocks.getQuote("JAVA");
		} catch (OpenCircuitException e) {
			Circuit c = e.getCircuit();
			assertTrue(c.isOpen());
			c.close();
			assertFalse(c.isOpen());
			assertEquals(5, stocks.getQuote("JAVA"));
		}
	}
	
	@Test
	public void the_circuit_closes_after_a_timeout() throws Exception {
		StockBreaker.aspectOf(stocks).getCircuit().setTimeoutMillis(1);
		generateFaultsToOpen();
		Thread.sleep(2);
		assertEquals(5, stocks.getQuote("JAVA"));
	}
	
	
	public void ingoredExceptions() {
		StockBreaker breaker = StockBreaker.aspectOf(stocks);
		breaker.ignoreException(RuntimeException.class);
		generateFaultsToOpen();
		assertEquals(5, stocks.getQuote("JAVA"));
		assertFalse(breaker.getCircuit().isOpen());
		breaker.removeIgnoredExcpetion(RuntimeException.class);
	}
	
	private void generateFaultsToOpen() {
		for (int i = 0; i < Circuit.DEFAULT_MAX_FAILURES; i++) {
			try {stocks.faultyGetQuote("JAVA"); } catch (RuntimeException expected) { }
		}
	}
}
