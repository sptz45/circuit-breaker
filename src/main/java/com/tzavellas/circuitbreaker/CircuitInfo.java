package com.tzavellas.circuitbreaker;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.util.Duration;

/**
 * A class that holds all the metadata about the state of a circuit.
 *
 * <p>Instances of this class are thread-safe.</p>
 * 
 * @see CircuitBreakerAspectSupport
 * @author spiros
 */
public class CircuitInfo {
	
	public static final int DEFAULT_MAX_FAILURES = 5;
	public static final long DEFAULT_TIMEOUT = 10 * 60 * 1000; // 10min
	public static final Duration DEFAULT_CURRENT_FAILURES_DURATION = Duration.hours(1);
	
	private final AtomicInteger currentFailures = new AtomicInteger();
	private final AtomicLong firstCurrentFailureTimestamp = new AtomicLong();
	private final AtomicLong openTimestamp = new AtomicLong();
	private final CircuitStatistics stats = new CircuitStatistics();
	
	private volatile int maxFailures = DEFAULT_MAX_FAILURES;
	private volatile Duration timeout = Duration.millis(DEFAULT_TIMEOUT);
	private volatile Duration currentFailuresDuration = DEFAULT_CURRENT_FAILURES_DURATION;
	
	
	/**
	 * Test whether the circuit is closed.
	 */
	public boolean isClosed() {
		return ! isOpen();
	}
	
	/**
	 * Test whether the circuit is open.
	 */
	public boolean isOpen() {
		return currentFailures.get() >= maxFailures && !isHalfOpen();
	}
	
	/**
	 * Test whether the circuit is open.
	 */
	public boolean isHalfOpen() {
		return hasExpired();
	}
	
	private boolean hasExpired() {
		long timestampt = openTimestamp.get(); 
		return timestampt != 0 && timestampt + timeout.toMillis() <= System.currentTimeMillis();
	}
	
	/**
	 * Close the circuit, allowing any method call to propagate to
	 * their recipients.
	 */
	public void close() {
		currentFailures.set(0);
		openTimestamp.set(0);
	}
	
	/**
	 * Open the circuit, causing any subsequent calls made through the
	 * circuit to stop throwing an OpenCircuitException.
	 */
	public void open() {
		openTimestamp.set(System.currentTimeMillis());	
		currentFailures.set(maxFailures);
		stats.timesOpened.incrementAndGet();
	}
	
	/**
	 * Record that a call to the target object failed.
	 */
	public void recordFailure() {
		initFirstFailureTimeStampIfNeeded();
		int tmpCurrentFailures = 0;
		if (currentFailuresDuration.hasPastSince(firstCurrentFailureTimestamp.get())) {
			resetFailures();
			tmpCurrentFailures = 1;
		} else {
			tmpCurrentFailures = currentFailures.incrementAndGet();
		}
		if (tmpCurrentFailures >= maxFailures)
				open();
		stats.failures.incrementAndGet();
	}
	
	private void resetFailures() {
		currentFailures.set(1);
		firstCurrentFailureTimestamp.set(System.nanoTime());
	}
	
	private void initFirstFailureTimeStampIfNeeded() {
		firstCurrentFailureTimestamp.compareAndSet(0, System.nanoTime());
	}
	
	/**
	 * Record that a successful call to the circuit.
	 */
	public void recordCall() {
		stats.calls.incrementAndGet();
	}

	/**
	 * Get the Date the circuit was opened.
	 * 
	 * @return if the circuit is open return the open timestamp else null 
	 */
	public Date getOpenTimestamp() {
		long timestamp = openTimestamp.get();
		return (timestamp == 0)? null: new Date(timestamp);
	}
	
	/**
	 * The number of calls being made through the circuit.
	 */
	public int getCalls() {
		return stats.calls.get();
	}

	/**
	 * The number of failures since the circuit was closed. 
	 */
	public int getCurrentFailures() {
		return currentFailures.get();
	}

	/**
	 * The number of total failures.
	 */
	public int getFailures() {
		return stats.failures.get();
	}

	/**
	 * The number of failures after the circuit opens.
	 */
	public int getMaxFailures() {
		return maxFailures;
	}
	
	/**
	 * Set the number of failures after the circuit opens.
	 */
	public void setMaxFailures(int maxFailures) {
		this.maxFailures = maxFailures;
	}

	/**
	 * The timeout after which the circuit closes.
	 */
	public Duration getTimeout() {
		return timeout;
	}
	
	/**
	 * Set the timeout after which the circuit closes.
	 */
	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * The number of times the circuit has been opened.
	 */
	public int getTimesOpened() {
		return stats.timesOpened.get();
	}

	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duration, default is 1 hour.
	 */
	public void setCurrentFailuresDuration(Duration d) {
		currentFailuresDuration = d;
	}
	
	/**
	 * Get the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 */
	public Duration getCurrentFailuresDuration() {
		return currentFailuresDuration;
	}
	
	/**
	 * Resets this circuit's configuration to default values.
	 * 
	 * @see {@link DEFAULT_MAX_FAILURES}
	 * @see {@link DEFAULT_TIMEOUT}
	 * @see {@link DEFAULT_CURRENT_FAILURES_DURATION}
	 */
	public void resetToDefaultConfig() {
		maxFailures = DEFAULT_MAX_FAILURES;
		timeout = Duration.millis(DEFAULT_TIMEOUT);
		currentFailuresDuration = DEFAULT_CURRENT_FAILURES_DURATION;
	}
	
	/**
	 * Resets this circuit's statistics.
	 */
	public void resetStatistics() {
		stats.calls.set(0);
		stats.failures.set(0);
		stats.timesOpened.set(0);
	}
}


class CircuitStatistics {	
	AtomicInteger calls = new AtomicInteger();
	AtomicInteger failures = new AtomicInteger();
	AtomicInteger timesOpened = new AtomicInteger();
}
