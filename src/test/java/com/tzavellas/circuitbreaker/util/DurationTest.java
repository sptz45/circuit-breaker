package com.tzavellas.circuitbreaker.util;

import static org.junit.Assert.*;

import java.beans.PropertyEditor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.tzavellas.circuitbreaker.util.Duration;

public class DurationTest {
	
	@Test
	public void has_past_since_tests() {
		// time dependent test (to System.nanoTime).
		assertTrue(Duration.nanos(1).hasPastSince(System.nanoTime()));
		assertFalse(Duration.millis(1).hasPastSince(System.nanoTime()));
	}
	
	@Test
	public void equality_tests() {
		assertEquals(Duration.nanos(10), Duration.nanos(10));
		assertEquals(Duration.micros(10), Duration.nanos(10000));
		assertEquals(Duration.nanos(10000), Duration.micros(10));
		assertEquals(Duration.days(10), new Duration(Duration.days(10).toNanos(), TimeUnit.NANOSECONDS));
		assertFalse(Duration.micros(3).equals(null));
		assertFalse(Duration.days(1).equals(Duration.nanos(1)));
		assertFalse(Duration.hours(1).equals(("1h")));
	}
	
	@Test
	public void hashCode_equals_contract() {
		assertEquals(Duration.micros(10).hashCode(), Duration.nanos(10000).hashCode());
	}
	
	@Test
	public void test_with_legal_duration_strings() {
		assertEquals(10, Duration.valueOf("10ns").toNanos());
		assertEquals(10, Duration.valueOf("10μs").toMicros());
		assertEquals(10, Duration.valueOf("10ms").toMillis());
		assertEquals(10, Duration.valueOf("10s").toSeconds());
		assertEquals(10, Duration.valueOf("10m").toMinutes());
		assertEquals(10, Duration.valueOf("10h").toHours());
		assertEquals(10, Duration.valueOf("10d").toDays());
		
		assertEquals(10, Duration.valueOf("10 ns").toNanos());
		assertEquals(10, Duration.valueOf("10nsec").toNanos());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void missing_unit_in_string() {
		Duration.valueOf("10");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void illegal_string_for_duration() {
		Duration.valueOf("I am not a duration");
	}
	
	@Test
	public void test_has_unit() {
		assertTrue(Duration.days(1).hasDays());
		assertFalse(Duration.hours(1).hasDays());
		
		assertTrue(Duration.hours(1).hasHours());
		assertFalse(Duration.minutes(1).hasHours());
		
		assertTrue(Duration.minutes(1).hasMinutes());
		assertFalse(Duration.seconds(1).hasMinutes());
		
		assertTrue(Duration.seconds(1).hasSeconds());
		assertFalse(Duration.millis(1).hasSeconds());
		
		assertTrue(Duration.millis(1).hasMillis());
		assertFalse(Duration.micros(1).hasMillis());
		
		assertTrue(Duration.micros(1).hasMicros());
		assertFalse(Duration.nanos(1).hasMicros());
		
		assertTrue(Duration.nanos(1).hasNanos());
	}
	
	@Test
	public void test_toString() {
		assertEquals("10ns", Duration.nanos(10).toString());
		assertEquals("10μs", Duration.micros(10).toString());
		assertEquals("10ms", Duration.millis(10).toString());
		assertEquals("10s", Duration.seconds(10).toString());
		assertEquals("10m", Duration.minutes(10).toString());
		assertEquals("10h", Duration.hours(10).toString());
		assertEquals("10d", Duration.days(10).toString());
	}

	@Test
	public void simple_roperty_editor_test() {
		PropertyEditor editor  = new Duration.Editor();
		editor.setAsText("10ns");
		assertEquals(10, ((Duration)editor.getValue()).toNanos());
		assertEquals("10ns", editor.getAsText());
	}
}
