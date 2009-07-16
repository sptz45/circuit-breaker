package com.tzavellas.circuitbreaker.util;

import java.beans.PropertyEditorSupport;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;

/**
 * A value object that hold a time duration in some {@link TimeUnit}.
 * 
 * @author spiros
 */
public final class Duration {
	
	/** A {@code PropertyEditor} for {@code Duration} objects. */
	public static class Editor extends PropertyEditorSupport {
		/** {@inheritDoc} */
		public String getAsText() {
			return getValue().toString();
		}
		/** {@inheritDoc} */
		public void setAsText(String text) throws IllegalArgumentException {
			setValue(Duration.valueOf(text));
		}
	}
	
	
	// -----------------------------------------------------------------------
	
	private final long duration;
	private final TimeUnit unit;
	
	public Duration(long duration, TimeUnit unit) {
		this.duration = duration;
		this.unit = unit;
	}
	
	public boolean hasPastSince(long nanos) {
		long now = System.nanoTime();
		return now - nanos >= toNanos();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Duration) {
			Duration that = (Duration) obj;
			return this.toNanos() == that.toNanos(); 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 37 * Long.valueOf(toNanos()).hashCode() * TimeUnit.NANOSECONDS.hashCode();  
	}
	
	@Override
	public String toString() {
		String unitString = null;
		switch (unit) {
		case NANOSECONDS:  unitString = "ns"; break;
		case MICROSECONDS: unitString = "μs"; break;
		case MILLISECONDS: unitString = "ms"; break;
		case SECONDS:      unitString = "s"; break;
		case MINUTES:      unitString = "m"; break;
		case HOURS:        unitString = "h"; break;
		case DAYS:         unitString = "d"; break;
		default:
			throw new AssertionError();
		}
		return duration + unitString;
	}
	
	public static Duration valueOf(String durationAsString) {
		try {
			Scanner s = new Scanner(durationAsString);
			s.findInLine("(\\d+)[\\s]*([μ]*\\w+)");
			MatchResult result = s.match();
			long duration = Long.valueOf(result.group(1));
			String us = result.group(2);
			TimeUnit unit = null;
			// order is important cause we have 2 matches for startsWith("m"): "m" an "ms"
			if (us.startsWith("d")) unit = TimeUnit.DAYS;
			if (us.startsWith("h")) unit = TimeUnit.HOURS;
			if (us.startsWith("m")) unit = TimeUnit.MINUTES;
			if (us.startsWith("s")) unit = TimeUnit.SECONDS;
			if (us.startsWith("ms")) unit = TimeUnit.MILLISECONDS;
			if (us.startsWith("μs")) unit = TimeUnit.MICROSECONDS;
			if (us.startsWith("ns")) unit = TimeUnit.NANOSECONDS;
			if (unit == null) throw new NullPointerException();
			
			return new Duration(duration, unit);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Could not parse [" + durationAsString + "] in a Duration object");
		}
	}
	
	
	// -----------------------------------------------------------------------
	
	public long toNanos()   { return unit.toNanos(duration); }
	public long toMicros()  { return unit.toMicros(duration); }
	public long toMillis()  { return unit.toMillis(duration); }
	public long toSeconds() { return unit.toSeconds(duration); }
	public long toMinutes() { return unit.toMinutes(duration); }
	public long toHours()   { return unit.toHours(duration); }
	public long toDays()    { return unit.toDays(duration); }
	
	
	// -----------------------------------------------------------------------
	
	public boolean hasNanos() {
		return true;
	}
	public boolean hasMicros() {
		return unit.toMicros(duration) > 0 || duration == 0;
	}
	public boolean hasMillis() {
		return unit.toMillis(duration) > 0 || duration == 0;
	}
	public boolean hasSeconds() {
		return unit.toSeconds(duration) > 0 || duration == 0;
	}
	public boolean hasMinutes() {
		return unit.toMinutes(duration) > 0 || duration == 0;
	}
	public boolean hasHours() {
		return unit.toHours(duration) > 0 || duration == 0;
	}
	public boolean hasDays() {
		return unit.toDays(duration) > 0 || duration == 0;
	}
	
	
	// -----------------------------------------------------------------------
	
	public static Duration nanos(long duration) {
		return new Duration(duration, TimeUnit.NANOSECONDS);
	}
	public static Duration micros(long duration) {
		return new Duration(duration, TimeUnit.MICROSECONDS);
	}
	public static Duration millis(long duration) {
		return new Duration(duration, TimeUnit.MILLISECONDS);
	}
	public static Duration seconds(long duration) {
		return new Duration(duration, TimeUnit.SECONDS);
	}
	public static Duration minutes(long duration) {
		return new Duration(duration, TimeUnit.MINUTES);
	}
	public static Duration hours(long duration) {
		return new Duration(duration, TimeUnit.HOURS);
	}
	public static Duration days(long duration) {
		return new Duration(duration, TimeUnit.DAYS);
	}
}
