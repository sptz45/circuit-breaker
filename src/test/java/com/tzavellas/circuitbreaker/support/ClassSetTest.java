package com.tzavellas.circuitbreaker.support;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassSetTest {

	final ClassSet classes = new ClassSet();
	
	@Test
	public void normal_operation() {
		assertTrue(classes.isEmpty());
		
		classes.add(RuntimeException.class);
		assertEquals(1, classes.size());
		assertFalse(classes.isEmpty());
		
		classes.remove(RuntimeException.class);
		assertTrue(classes.isEmpty());
	}
	
	@Test
	public void operation_while_empty() {
		assertTrue(classes.isEmpty());
		assertEquals(0, classes.size());
		assertFalse(classes.contains(Exception.class));
	}
	
	@Test
	public void simple_contains_test() {
		classes.add(ArithmeticException.class);
		assertFalse(classes.contains(RuntimeException.class));
		assertTrue(classes.contains(ArithmeticException.class));
		assertFalse(classes.contains(IllegalStateException.class));
	}
	
	@Test
	public void contains_also_checks_for_subclasses() {
		classes.add(RuntimeException.class);
		assertTrue(classes.contains(RuntimeException.class));
		assertTrue(classes.contains(ArithmeticException.class));
	}
	
	@Test
	public void adding_a_superclass_removes_subclasses() {
		classes.add(ArithmeticException.class);
		assertEquals(1, classes.size());
		
		classes.add(RuntimeException.class);
		assertEquals(1, classes.size());
	}
}
