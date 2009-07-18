package com.tzavellas.circuitbreaker;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.tzavellas.circuitbreaker.util.Duration;

/**
 * A class that holds all the metadata about the state of a circuit.
 *
 * <p>Instances of this class are thread-safe.</p>
 * 
 * @see CircuitBreaker
 * @author spiros
 */
public class CircuitInfo implements CircuitInfoMBean {
	
	public static final int DEFAULT_MAX_FAILURES = 5;
	public static final long DEFAULT_TIMEOUT = 10 * 60 * 1000; // 10min
	public static final Duration DEFAULT_CURRENT_FAILURES_DURATION = Duration.hours(1);
	
	private final AtomicInteger currentFailures = new AtomicInteger();
	private final AtomicLong firstCurrentFailureTimestamp = new AtomicLong();
	private final AtomicLong openTimestamp = new AtomicLong();
	private final CircuitStatistics stats = new CircuitStatistics();
	
	private volatile int maxFailures = DEFAULT_MAX_FAILURES;
	private AtomicLong timeout = new AtomicLong(DEFAULT_TIMEOUT);
	private volatile Duration currentFailuresDuration = DEFAULT_CURRENT_FAILURES_DURATION;
	
	
	/** {@inheritDoc} */
	public boolean isClosed() {
		return ! isOpen();
	}
	
	/** {@inheritDoc} */
	public boolean isOpen() {
		return currentFailures.get() >= maxFailures && !isHalfOpen();
	}
	
	/** {@inheritDoc} */
	public boolean isHalfOpen() {
		return hasExpired();
	}
	
	private boolean hasExpired() {
		long timestampt = openTimestamp.get(); 
		return timestampt != 0 && timestampt + timeout.get() <= System.currentTimeMillis();
	}
	
	/** {@inheritDoc} */
	public void close() {
		currentFailures.set(0);
		openTimestamp.set(0);
	}
	
	/** {@inheritDoc} */
	public void open() {
		openTimestamp.set(System.currentTimeMillis());	
		currentFailures.set(maxFailures);
		stats.timesOpened.incrementAndGet();
	}
	
	/**
	 * Record that a call to the wrapped object failed.
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

	/** {@inheritDoc} */
	public void setMaxFailures(int maxFailures) {
		this.maxFailures = maxFailures;
	}
	
	/** {@inheritDoc} */
	public Date getOpenTimestamp() {
		long timestamp = openTimestamp.get();
		return (timestamp == 0)? null: new Date(timestamp);
	}
	
	/** {@inheritDoc} */
	public int getCalls() {
		return stats.calls.get();
	}

	/** {@inheritDoc} */
	public int getCurrentFailures() {
		return currentFailures.get();
	}

	/** {@inheritDoc} */
	public int getFailures() {
		return stats.failures.get();
	}

	/** {@inheritDoc} */
	public int getMaxFailures() {
		return maxFailures;
	}

	/** {@inheritDoc} */
	public long getTimeoutMillis() {
		return timeout.get();
	}
	/** {@inheritDoc} */
	public void setTimeoutMillis(long millis) {
		timeout.set(millis);
	}
	/** {@inheritDoc} */
	public int getTimesOpened() {
		return stats.timesOpened.get();
	}
	/** {@inheritDoc} */
	public void setCurrentFailuresDuration(String duration) {
		currentFailuresDuration = Duration.valueOf(duration);
	}
	/**
	 * Specify the duration after which the number of failures track by
	 * the circuit breaker gets reset. 
	 * 
	 * @param duration the duratin, default is 1 hour.
	 */
	public void setCurrentFailuresDuration(Duration d) {
		currentFailuresDuration = d;
	}
	
	public void resetConfig() {
		maxFailures = DEFAULT_MAX_FAILURES;
		timeout = new AtomicLong(DEFAULT_TIMEOUT);
		currentFailuresDuration = DEFAULT_CURRENT_FAILURES_DURATION;
	}
	
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
