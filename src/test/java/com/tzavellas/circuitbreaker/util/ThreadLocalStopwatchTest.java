package com.tzavellas.circuitbreaker.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class ThreadLocalStopwatchTest {
	
	final ThreadLocalStopwatch sw = new ThreadLocalStopwatch();
	
	@Test
	public void the_stopwatch_checks_for_legal_state_before_answering() {
		try { sw.stop(); fail(); } catch (IllegalStateException expected) { }
		try { sw.duration(); fail(); } catch (IllegalStateException expected) { }
		
		sw.start();
		
		try { sw.duration(); fail(); } catch (IllegalStateException expected) { }
		
		sw.stop();
		
		try { sw.start(); fail(); } catch (IllegalStateException expected) { }
		
		assertTrue(sw.duration() > 0);
	}
}
