package com.tzavellas.circuitbreaker;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that holds all the metadata about the state of a circuit.
 *
 * <p>Instances of this class are thread safe.</p>
 * 
 * @see CircuitBreaker
 * @author spiros
 */
public class CircuitInfo implements CircuitInfoMBean {
	
	public static final int DEFAULT_MAX_FAILURES = 5;
	public static final long DEFAULT_TIMEOUT = 10 * 60 * 1000; // 10min
	
	private final AtomicInteger currentFailures = new AtomicInteger();
	private final AtomicLong openTimestamp = new AtomicLong();
	private final CircuitStatistics stats = new CircuitStatistics();
	
	private volatile int maxFailures = DEFAULT_MAX_FAILURES;
	private volatile long timeout = DEFAULT_TIMEOUT; //XXX not thread-safe (non atomic update)
	
	
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
		return timestampt != 0 && timestampt + timeout <= System.currentTimeMillis();
	}
	
	/** {@inheritDoc} */
	public void close() {
		currentFailures.set(0);
		openTimestamp.set(0);
	}
	
	/** {@inheritDoc} */
	public void open() {
		boolean opened = openTimestamp.compareAndSet(0, System.currentTimeMillis());
		if (opened) {
			currentFailures.set(maxFailures);
			stats.timesOpened.incrementAndGet();
		}
	}
	
	/**
	 * Record that a call to the circuit failed.
	 */
	public void recordFailure() {
		int cf = currentFailures.incrementAndGet();
		if (cf >= maxFailures) {
			open();
		}
		stats.failures.incrementAndGet();
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
		return timeout;
	}
	/** {@inheritDoc} */
	public void setTimeoutMillis(long millis) {
		timeout = millis;
	}
	/** {@inheritDoc} */
	public int getTimesOpened() {
		return stats.timesOpened.get();
	}
}


class CircuitStatistics {	
	AtomicInteger calls = new AtomicInteger();
	AtomicInteger failures = new AtomicInteger();
	AtomicInteger timesOpened = new AtomicInteger();
}
